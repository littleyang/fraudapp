package com.fraud.api.feign;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fraud.api.reqest.fraud.FraudTxEvaReqDTO;
import com.fraud.biz.txengine.TXEvalEngine;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.model.TransactionDO;
import com.fraud.web.platform.vo.TxEvaluateVO;
import com.sct.bizmsg.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@RestController
@RequestMapping("/feign/tx")
public class FraudFeignAPIImpl implements FraudFeignAPI{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TXEvalEngine txEvalEngine;


    @Override
    @PostMapping("/evaluate")
    public String fraudTxEvaluate(FraudTxEvaReqDTO fraudTxEvaReqDTO) {
        logger.info("FraudFeignAPIImpl.TxEvaluate, FraudTxEvaReqDTO={}", fraudTxEvaReqDTO);

        if(ObjectUtil.isNull(fraudTxEvaReqDTO)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(fraudTxEvaReqDTO.getTransactionId())
                ||ObjectUtil.isNull(fraudTxEvaReqDTO.getAccount())
                ||ObjectUtil.isNull(fraudTxEvaReqDTO.getAmount())
                ||ObjectUtil.isNull(fraudTxEvaReqDTO.getTransactionTime())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        TransactionDO txDO = new TransactionDO();
        txDO.setAccount(fraudTxEvaReqDTO.getAccount());
        txDO.setAmount(fraudTxEvaReqDTO.getAmount());
        txDO.setTransactionTime(fraudTxEvaReqDTO.getTransactionTime());
        txDO.setTransactionId(fraudTxEvaReqDTO.getTransactionId());


        try {
            List<String> result = this.txEvalEngine.evaluateTx(txDO);
            TxEvaluateVO vo = new TxEvaluateVO();
            if(CollectionUtils.isNotEmpty(result)){
                // 检测有错误原因表示错误
                vo.setCheckResult(false);
                vo.setReasons("检测交易信息:\n " + JSONUtil.toJsonStr(txDO) +"\n"
                        + StringUtils.join(result,"\n"));
            }else{
                vo.setCheckResult(true);
                vo.setReasons("检测交易信息:\n " + JSONUtil.toJsonStr(txDO) + "正常");
            }
            return JSONUtil.toJsonStr(vo);
        }catch (Exception ex){
            logger.error("fraudTxEvaluate,transaction={},error={}",txDO,ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}
