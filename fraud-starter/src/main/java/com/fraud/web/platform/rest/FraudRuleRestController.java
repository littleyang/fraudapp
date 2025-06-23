package com.fraud.web.platform.rest;

import cn.hutool.core.util.ObjectUtil;
import com.fraud.biz.service.RuleService;
import com.fraud.biz.txengine.TxRuleLoader;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.commons.enums.RuleTypeEnum;
import com.fraud.repository.app.model.RuleDO;
import com.fraud.web.platform.form.RuleCreateForm;
import com.fraud.web.platform.vo.RuleVO;
import com.sct.bizmsg.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
@RestController
@RequestMapping("/rule")
public class FraudRuleRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RuleService ruleService;

    @Autowired
    private TxRuleLoader txRuleLoader;

    @PostMapping("/create")
    public Object createRule(HttpServletRequest request,
                             @RequestBody RuleCreateForm createForm) {

        logger.info("FraudRuleRestController.createRule, createForm={}", createForm);

        if(ObjectUtil.isNull(createForm)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(createForm.getAmount())
                ||ObjectUtil.isNull(createForm.getOffHours())
                ||ObjectUtil.isNull(createForm.isBlackCheck())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }


        boolean result = this.ruleService.createFraudRule(createForm.getAmount(),
                createForm.getOffHours(), createForm.isBlackCheck());

        return result;
    }


    @PutMapping("/update")
    public Object updateRule(HttpServletRequest request,
                             @RequestBody RuleCreateForm createForm) {

        logger.info("FraudRuleRestController.updateRule, createForm={}", createForm);

        if(ObjectUtil.isNull(createForm)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(createForm.getAmount())
                ||ObjectUtil.isNull(createForm.getOffHours())
                ||ObjectUtil.isNull(createForm.isBlackCheck())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }


        boolean result = this.ruleService.updateRule(createForm.getAmount(),
                createForm.getOffHours(),
                createForm.isBlackCheck());

        return result;
    }

    @GetMapping("/current/rule")
    public Object getCurrentRule(HttpServletRequest request) {

        RuleDO amountRule = txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType());
        RuleDO offHoursRule = txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType());
        RuleDO blackListRule = txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType());
        if(ObjectUtil.isNotNull(amountRule)
                ||ObjectUtil.isNull(offHoursRule)
                ||ObjectUtil.isNull(blackListRule)){
            RuleVO ruleVO = new RuleVO();
            ruleVO.setAmount(amountRule.getValue());
            ruleVO.setOffHours(offHoursRule.getValue());
            ruleVO.setBlackCheck(Boolean.parseBoolean(blackListRule.getValue()));
            return ruleVO;
        }
        return null;
    }

}
