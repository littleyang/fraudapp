package com.fraud.biz.service;

import com.fraud.repository.app.model.AlertRecordDO;
import com.fraud.repository.app.model.TransactionDO;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.result.vo.PageResultVO;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public interface TransactionService {

    boolean createTransaction(TransactionDO transaction);

    boolean deleteTransaction(String txId);

    boolean deleteTransactionById(String id);

    TransactionDO getTransactionByTxId(String txId);

    TransactionDO getTransactionById(long id);

    List<AlertRecordDO> queryTransactionAlert(String txId);

    PageResultVO<TransactionDO> queryTransaction(TransactionDO transaction,
                                                 Page page);
    boolean checkTransactionIdempotent(String  idempotentToken);



}
