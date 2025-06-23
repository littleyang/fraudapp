package com.fraud.biz.service.impl;

import com.fraud.biz.service.AlertRecordService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.mapper.AlertRecordDOMapper;
import com.fraud.repository.app.model.AlertRecordDO;
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

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class AlertRecordServiceImpl implements AlertRecordService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AlertRecordDOMapper alertRecordDOMapper;

    @Autowired
    public AlertRecordServiceImpl(AlertRecordDOMapper alertRecordDOMapper) {
        this.alertRecordDOMapper = alertRecordDOMapper;
    }

    @Override
    public boolean createAlertRecord(AlertRecordDO alertRecordDO) {
       int result = alertRecordDOMapper.insertSelective(alertRecordDO);
        return result>0;
    }

    @Override
    public AlertRecordDO findAlertRecordById(Long id) {
        logger.error("findAlertRecordById id={}",id);
        AlertRecordDO alertRecordDO = alertRecordDOMapper.selectByPrimaryKey(id);
        if(alertRecordDO==null){
            logger.error("findAlertRecordById data not exist,id={}",id);
            throw new BusinessException(FraudRestResultCode.DATA_NOT_EXIST,"为查询到数据信息");
        }

        return alertRecordDO;
    }

    @Override
    public PageResultVO<AlertRecordDO> findAlertRecordByPage(String user, Page page) {
        Limit limit = Pages.toLimit(page);

        List<AlertRecordDO> alertRecordDOList = this.alertRecordDOMapper.selectAlertDOByUser(
                user, limit.getOffset(),limit.getLimit());
        int totalSize = this.alertRecordDOMapper.countAlertDOByUser(user);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page.getPage());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotalItems(totalSize);
        pageInfo.setTotalPage(PageInfo.convertTotalPages(pageInfo));
        PageResultVO<AlertRecordDO> pageResultVO = new PageResultVO<>(alertRecordDOList,pageInfo);
        return pageResultVO;
    }

    @Override
    public boolean deleteById(long id) {
        int result = alertRecordDOMapper.deleteByPrimaryKey(id);
        return result>0;
    }
}
