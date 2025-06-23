package com.fraud.web.platform.rest;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fraud.biz.service.TransactionService;
import com.fraud.biz.txengine.TXEvalEngine;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.model.NotifyUserDO;
import com.fraud.repository.app.model.TransactionDO;
import com.fraud.web.platform.form.CreateNotifyUserForm;
import com.fraud.web.platform.form.TransactionForm;
import com.fraud.web.platform.vo.TxEvaluateVO;
import com.sct.bizmsg.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
@RestController
@RequestMapping("/tx/evaluate")
public class FraudTXRestController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TXEvalEngine txEvalEngine;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/check")
    public Object TxEvaluate(HttpServletRequest request,
                             @RequestHeader(value = "Idempotent-Token",required = false) String token,
                             @RequestBody TransactionForm transactionForm) {

        /**
         * 对于并发幂等控制
         * 为了防止重复提交错误提交等，在每次请求http header头里面新增一个"Idempotent-Token"，
         * 唯一的uuid值，放入缓存key中，过期时间5秒钟，每次检查请求token是否存在，如果存在直接返回重复提交信息。
         * 数据库对于transaction id 进行唯一主键索引，避免脏数据的出现
         */

        logger.info("FraudTXRestController.TxEvaluate, transactionForm={}", transactionForm);
        if(ObjectUtil.isNull(transactionForm)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(transactionForm.getTransactionId())
                ||ObjectUtil.isNull(transactionForm.getAccount())
                ||ObjectUtil.isNull(transactionForm.getAmount())
                ||ObjectUtil.isNull(transactionForm.getTransactionTime())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(token)) {
            token = transactionForm.getTransactionId();
        }

        logger.info("FraudTXRestController.TxEvaluate, Idempotent-Token={}", token);
        /**
         * 检测是否重复提交,如果是是重复提交直接报错
         */
        transactionService.checkTransactionIdempotent(token);

        TransactionDO txDO = new TransactionDO();
        txDO.setAccount(transactionForm.getAccount());
        txDO.setAmount(transactionForm.getAmount());
        txDO.setTransactionTime(transactionForm.getTransactionTime());
        txDO.setTransactionId(transactionForm.getTransactionId());

        try {
            // 进行交易检测
            List<String> result = this.txEvalEngine.evaluateTx(txDO);
            TxEvaluateVO vo = new TxEvaluateVO();
            if(CollectionUtils.isNotEmpty(result)){
                // 检测有欺诈，并且返回原因
                vo.setCheckResult(true);
                vo.setReasons("检测交易信息:\n " + JSONUtil.toJsonStr(txDO) +"\n"
                        +StringUtils.join(result,"\n"));
            }else{
                // 不是欺诈行为
                vo.setCheckResult(false);
                vo.setReasons("检测交易信息:\n " + JSONUtil.toJsonStr(txDO) + "正常");
            }
            return vo;
        }catch (Exception ex){
            logger.info("fraudTxEvaluate,transaction={},error={}",txDO, ex.getMessage());
            if(ex instanceof DuplicateKeyException){
                throw new BusinessException(FraudRestResultCode.DATA_DUPLICATION_ERROR);
            }
            throw new BusinessException(FraudRestResultCode.ERROR,ex.getMessage());
        }
    }
}
