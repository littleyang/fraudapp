package com.fraud.biz.service.impl;

import com.fraud.biz.service.NotifyUserService;
import com.fraud.repository.app.mapper.NotifyUserDOMapper;
import com.fraud.repository.app.model.NotifyUserDO;
import com.sct.commons.api.page.Limit;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.page.PageInfo;
import com.sct.commons.api.page.Pages;
import com.sct.commons.api.result.vo.PageResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class NotifyUserServiceImpl implements NotifyUserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotifyUserDOMapper notifyUserDOMapper;

    @Autowired
    public NotifyUserServiceImpl(NotifyUserDOMapper notifyUserDOMapper) {
        this.notifyUserDOMapper = notifyUserDOMapper;
    }

    @Override
    public boolean createNotifyUser(NotifyUserDO notifyUserDO) {
        NotifyUserDO existDO = notifyUserDOMapper.selectByUserAccount(notifyUserDO.getUser());
        if(existDO != null) {
            logger.info("createNotifyUser,user exist user={}", notifyUserDO);
        }
        int result = this.notifyUserDOMapper.insertSelective(notifyUserDO);
        return result>0;
    }

    @Override
    public boolean updateNotifyUser(NotifyUserDO notifyUserDO) {
        int result = this.notifyUserDOMapper.updateByPrimaryKeySelective(notifyUserDO);
        return result>0;
    }

    @Override
    public boolean deleteNotifyUser(long id) {
        int result = this.notifyUserDOMapper.deleteByPrimaryKey(id);
        return result>0;
    }

    @Override
    public NotifyUserDO getNotifyUserByID(long id) {
        return this.notifyUserDOMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<NotifyUserDO> selectAllNotifyUser() {
        List<NotifyUserDO> notifyUserDOList = this.notifyUserDOMapper.selectAllNotifyUser();
        return  notifyUserDOList;
    }

    @Override
    public PageResultVO<NotifyUserDO> selectNotifyUser(String user, Page page) {
        Limit limit = Pages.toLimit(page);
        List<NotifyUserDO> notifyUserDOList = this.notifyUserDOMapper.selectNotifyUserAccount(user,
                limit.getOffset(), limit.getLimit());
        int totalSize = this.notifyUserDOMapper.countNotifyUserAccount(user);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page.getPage());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotalItems(totalSize);
        pageInfo.setTotalPage(PageInfo.convertTotalPages(pageInfo));
        return new PageResultVO<>(notifyUserDOList, pageInfo);
    }
}
