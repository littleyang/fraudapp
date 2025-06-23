package com.fraud.biz.txengine;

import com.fraud.biz.notify.NotifyService;
import com.fraud.biz.service.BlackListService;
import com.fraud.biz.service.TransactionService;
import com.fraud.biz.service.impl.LockService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.commons.enums.RuleTypeEnum;
import com.fraud.repository.app.model.RuleDO;
import com.fraud.repository.app.model.TransactionDO;
import com.sct.bizmsg.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TXEvalEngine {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlackListService blackListService;
    private final TxRuleLoader txRuleLoader;
    private final TransactionService transactionService;
    private final NotifyService notifyService;

    private final LockService lockService;

    @Autowired
    public TXEvalEngine(BlackListService blackListService,
                        TxRuleLoader txRuleLoader,
                        TransactionService transactionService,
                        NotifyService notifyService,
                        LockService lockService) {
        this.blackListService = blackListService;
        this.txRuleLoader = txRuleLoader;
        this.transactionService = transactionService;
        this.notifyService = notifyService;
        this.lockService = lockService;
    }

    public List<String> evaluateTx(TransactionDO transactionDO) {
        logger.info("TXEvalEngine evaluateTx start, transaction = {}", transactionDO);
        List<String> reasons = new ArrayList<>();
        boolean locked = false;
        try {
            // 执行上锁操作
            locked = lockService.tryLock(transactionDO.getTransactionId());
            if (!locked) {
                logger.info("TXEvalEngine evaluateTx locked, transaction = {}", transactionDO);
                throw new BusinessException(FraudRestResultCode.DATA_DUPLICATION_ERROR,"重复请求，正在处理中，请稍后重试");
            }
            checkAmountRule(transactionDO, reasons);
            checkBlacklistRule(transactionDO, reasons);
            checkOffHoursRule(transactionDO, reasons);

            if (!reasons.isEmpty()) {
                logger.info("TXEvalEngine evaluation failed, reasons size = {}", reasons.size());
                transactionDO.setStatus(1);
                transactionService.createTransaction(transactionDO);
                try {
                    notifyService.sendTransactionCheckNotify(transactionDO, reasons);
                } catch (Exception e) {
                    logger.error("Notification sending failed: {}", e.getMessage(), e);
                }
            }
        }finally {
            if (locked) {
                // 执行解锁
                lockService.unlock(transactionDO.getTransactionId());
            }
        }
        logger.info("TXEvalEngine evaluation completed, transaction pass");
        return reasons;
    }

    private void checkAmountRule(TransactionDO tx, List<String> reasons) {

        RuleDO rule = txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType());
        if (rule == null || tx.getAmount() == null) return;
        try {
           if (rule != null) {
               double threshold = Double.parseDouble(rule.getValue());
               double amount = Double.parseDouble(tx.getAmount());
               if (amount > threshold) {
                   String reason = "Transaction amount exceeds: " + threshold;
                   reasons.add(reason);
                   logger.info("Amount rule triggered: {}, transaction = {}", reason, tx);
               }
           }
       }catch (Exception ex){
           logger.error("TXEvalEngine evaluation checkAmountRule error={}", ex.getMessage());
       }
    }

    private void checkBlacklistRule(TransactionDO tx, List<String> reasons) {
        RuleDO rule = txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType());
        if (rule == null || !"true".equalsIgnoreCase(rule.getValue())) return;

        try {
            if (blackListService.isBlackListExist(tx.getAccount())) {
                String reason = "Transaction account blacklisted";
                reasons.add(reason);
                logger.info("Blacklist rule triggered: {}, transaction = {}", reason, tx);
            }
        } catch (Exception e) {
            logger.warn("黑名单检测失败（可能 Redis 异常）：{}", e.getMessage());
        }
    }

    private void checkOffHoursRule(TransactionDO tx, List<String> reasons) {
        RuleDO rule = txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType());
        if (rule == null || tx.getTransactionTime() == null) return;
        try {
            String[] range = rule.getValue().split("-");
            int startHour = Integer.parseInt(range[0].trim());
            int endHour = Integer.parseInt(range[1].trim());
            Date txDate = tx.getTransactionTime();
            LocalDateTime txTime = txDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            int hour = txTime.getHour();
            if (hour >= startHour && hour <= endHour) {
                String reason = "Transaction time in off-hours: (" + startHour + " - " + endHour + ")";
                reasons.add(reason);
                logger.info("Off-hours rule triggered: {}, transaction = {}", reason, tx);
            }
        } catch (Exception e) {
            logger.error("Failed to evaluate off-hours rule", e);
        }
    }
}
