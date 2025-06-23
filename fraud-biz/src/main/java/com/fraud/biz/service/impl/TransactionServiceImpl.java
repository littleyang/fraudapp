package com.fraud.biz.service.impl;

import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.biz.service.TransactionService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.mapper.AlertRecordDOMapper;
import com.fraud.repository.app.mapper.TransactionDOMapper;
import com.fraud.repository.app.model.AlertRecordDO;
import com.fraud.repository.app.model.TransactionDO;
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
import java.util.concurrent.TimeUnit;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final TransactionDOMapper transactionDOMapper;
    private final AlertRecordDOMapper alertRecordDOMapper;

    private final RedisUtilService redisUtilService;

    @Autowired
    public TransactionServiceImpl(TransactionDOMapper transactionDOMapper,
                                  AlertRecordDOMapper alertRecordDOMapper,
                                  RedisUtilService redisUtilService) {
        this.transactionDOMapper = transactionDOMapper;
        this.alertRecordDOMapper = alertRecordDOMapper;
        this.redisUtilService = redisUtilService;
    }

    @Override
    public boolean createTransaction(TransactionDO transaction) {
        int result = transactionDOMapper.insertSelective(transaction);
        return result > 0;
    }

    @Override
    public boolean deleteTransaction(String txId) {
        int result = this.transactionDOMapper.deleteByTransactionID(txId);
        return result > 0;
    }

    @Override
    public boolean deleteTransactionById(String id) {
        int result = this.transactionDOMapper.deleteByPrimaryKey(Long.valueOf(id));
        return result>0;
    }

    @Override
    public TransactionDO getTransactionByTxId(String txId) {
        TransactionDO transactionDO = this.transactionDOMapper.selectByTxID(txId);
        return transactionDO;
    }

    @Override
    public TransactionDO getTransactionById(long id) {
        TransactionDO transactionDO = this.transactionDOMapper.selectByPrimaryKey(id);
        return transactionDO;
    }

    @Override
    public List<AlertRecordDO> queryTransactionAlert(String txID) {
        List<AlertRecordDO> alertRecordDOList = this.alertRecordDOMapper.selectAlertDOByTxID(txID);
        return alertRecordDOList;
    }

    @Override
    public PageResultVO<TransactionDO> queryTransaction(TransactionDO transaction, Page page) {

        Limit limit = Pages.toLimit(page);

        List<TransactionDO> transactionDOList = this.transactionDOMapper.selectByTransaction(transaction.getAccount(),
                transaction.getTransactionId(),
                limit.getOffset(),limit.getLimit());
        int totalSize = this.transactionDOMapper.countByTransaction(transaction.getAccount(),
                transaction.getTransactionId());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page.getPage());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotalItems(totalSize);
        pageInfo.setTotalPage(PageInfo.convertTotalPages(pageInfo));
        PageResultVO<TransactionDO> pageResultVO = new PageResultVO<>(transactionDOList,pageInfo);
        return pageResultVO;
    }

    @Override
    public boolean checkTransactionIdempotent(String token) {
        if (!redisUtilService.setIfAbsent("idem:" + token, "1")) {
            throw new BusinessException(FraudRestResultCode.DATA_DUPLICATION_ERROR, "请勿重复提交");
        }
        return true;
    }
}
