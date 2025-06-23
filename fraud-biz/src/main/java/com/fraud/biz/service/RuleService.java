package com.fraud.biz.service;

import com.fraud.repository.app.model.RuleDO;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public interface RuleService {

    // 创建检测规则
    boolean createFraudRule(String amount,String offHours, boolean blackCheck);

    // 查询所有的检测规则
    List<RuleDO> getAllRules();

    // 更新rule
    boolean updateRule(String amount,String offHours, boolean blackCheck);

}
