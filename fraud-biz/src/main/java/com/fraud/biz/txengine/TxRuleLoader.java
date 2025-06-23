package com.fraud.biz.txengine;

import com.fraud.biz.service.RuleService;
import com.fraud.repository.app.model.RuleDO;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class TxRuleLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RuleService ruleService;

    @Autowired
    public TxRuleLoader(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    public RuleDO loadRule(String key) {
       List<RuleDO> ruleDOList = this.ruleService.getAllRules();
       if(CollectionUtils.isNotEmpty(ruleDOList)){
           Map<String,RuleDO> ruleDOMap = ruleDOList.stream().collect(Collectors.toMap(RuleDO::getRuleKey, Function.identity()));
           return ruleDOMap.get(key);
       }else{
           logger.info("RuleLoader.loadRule is empty");
           throw  new RuntimeException("未成查询到监控规则");
       }
    }
}
