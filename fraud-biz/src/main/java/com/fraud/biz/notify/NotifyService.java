package com.fraud.biz.notify;

import com.fraud.repository.app.model.TransactionDO;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public interface NotifyService {

    boolean sendTransactionCheckNotify(TransactionDO transactionDO, List<String> reasons);
}
