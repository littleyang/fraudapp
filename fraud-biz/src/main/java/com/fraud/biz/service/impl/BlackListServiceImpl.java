package com.fraud.biz.service.impl;

import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.biz.service.BlackListService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.mapper.BlacklistDOMapper;
import com.fraud.repository.app.model.BlacklistDO;
import com.sct.bizmsg.exception.BusinessException;
import com.sct.commons.api.page.Limit;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.page.PageInfo;
import com.sct.commons.api.page.Pages;
import com.sct.commons.api.result.vo.PageResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class BlackListServiceImpl implements BlackListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String BLACKLIST_KEY = "fraud:blacklist";

//    @Autowired
//    private BlacklistDOMapper blacklistDOMapper;
//
//    @Autowired
//    private RedisUtilService redisUtilService;

    private final BlacklistDOMapper blacklistDOMapper;
    private final RedisUtilService redisUtilService;

    @Autowired
    public BlackListServiceImpl(BlacklistDOMapper blacklistDOMapper, RedisUtilService redisUtilService) {
        this.blacklistDOMapper = blacklistDOMapper;
        this.redisUtilService = redisUtilService;
    }
    /**
     * 缓存预热
     */
    @PostConstruct
    public void preload() {
        List<BlacklistDO> blacklistDOList = this.blacklistDOMapper.queryAllValidBlackList();
        for (BlacklistDO blacklistDO : blacklistDOList) {
            redisUtilService.opsForSet(BLACKLIST_KEY,blacklistDO.getAccount());
        }
    }

    @Override
    public boolean createBlackList(BlacklistDO blacklistDO) {

        // 检查黑名单是否已经存在
        BlacklistDO existDO = this.blacklistDOMapper.selectByAccount(blacklistDO.getAccount());
        if(existDO!=null){
            // 表示已经存在
            logger.error("createBlackList data exist,blacklistDO ={}",blacklistDO);
            throw new BusinessException(FraudRestResultCode.DATA_DUPLICATION_ERROR,"数据已经存在");
        }
        // 设置默认的状态，黑名单
        blacklistDO.setStatus(0);
        int result = this.blacklistDOMapper.insertSelective(blacklistDO);
        // 更新缓存
        if(result > 0){
            redisUtilService.opsForSet(BLACKLIST_KEY,blacklistDO.getAccount());
        }
        return result > 0;
    }

    @Override
    public boolean updateBlackList(BlacklistDO blacklistDO) {
        int result = this.blacklistDOMapper.updateByPrimaryKeySelective(blacklistDO);
        return result > 0;
    }

    @Override
    public boolean deleteBlackList(long id) {

        BlacklistDO exist = this.blacklistDOMapper.selectByPrimaryKey(id);
        if(exist != null){
            int result = this.blacklistDOMapper.deleteByPrimaryKey(id);
            if(result > 0){
                redisUtilService.opsForSetRemove(BLACKLIST_KEY,exist.getAccount());
            }
            return result>0;
        }
        return false;

    }

    @Override
    public BlacklistDO getBlacklistDOById(String id) {
      return this.blacklistDOMapper.selectByPrimaryKey(Long.valueOf(id));
    }

    @Override
    public List<BlacklistDO> getAllBlacklists() {
        List<BlacklistDO> blacklistDOList = this.blacklistDOMapper.queryAllValidBlackList();
        return blacklistDOList;
    }

    @Override
    public PageResultVO<BlacklistDO> getBlacklistsByPage(String account, Page page) {
        Limit limit = Pages.toLimit(page);

        List<BlacklistDO> blacklistDOList = this.blacklistDOMapper.queryAllValidBlackListByAccountLike(account,
                limit.getOffset(), limit.getLimit());
        int totalSize = this.blacklistDOMapper.countValidBlackListByAccountLike(account);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page.getPage());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotalItems(totalSize);
        pageInfo.setTotalPage(PageInfo.convertTotalPages(pageInfo));
        PageResultVO<BlacklistDO> pageResultVO = new PageResultVO<>(blacklistDOList,pageInfo);
        return pageResultVO;
    }

    @Override
    public boolean isBlackListExist(String account) {
        return redisUtilService.isMember(BLACKLIST_KEY,account);
    }

    @Override
    public Set<String> getAllBlackListedAccount() {
        return redisUtilService.membersString(BLACKLIST_KEY);
    }
}
