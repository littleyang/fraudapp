package com.fraud.biz.service.impl;

import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.biz.service.RuleService;
import com.fraud.commons.enums.RuleTypeEnum;
import com.fraud.repository.app.mapper.RuleDOMapper;
import com.fraud.repository.app.model.RuleDO;
import org.apache.commons.codec.language.bm.Rule;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class RuleServiceImpl implements RuleService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final static String RULE_CACHE_NAME = "fraud:rule";

    private final RuleDOMapper ruleDOMapper;
    private final RedisUtilService redisUtilService;

    @Autowired
    public RuleServiceImpl(RuleDOMapper ruleDOMapper, RedisUtilService redisUtilService) {
        this.ruleDOMapper = ruleDOMapper;
        this.redisUtilService = redisUtilService;
    }

    //初始化预热缓存
    @PostConstruct
    public void preload() {

        List<RuleDO> ruleDOList = this.ruleDOMapper.selectAllRule();
        for (RuleDO ruleDO : ruleDOList) {
            redisUtilService.opsForSetObject(RULE_CACHE_NAME, ruleDO);
        }

    }

    @Override
    public boolean createFraudRule(String amount,
                                   String offHours,
                                   boolean blackCheck) {

        List<RuleDO> ruleDOList = this.getAllRules();
        if(CollectionUtils.isEmpty(ruleDOList)){
            // 调用创建逻辑
            // 创建规则，先删除已经存在的规则，再新建规则
            RuleDO amountDO = new RuleDO();
            amountDO.setRuleKey(RuleTypeEnum.AMOUNT.getType());
            amountDO.setDescription(RuleTypeEnum.AMOUNT.getDesc());
            amountDO.setRuleType(RuleTypeEnum.AMOUNT.getType());
            amountDO.setValue(amount);
            amountDO.setStatus(0);
            this.ruleDOMapper.insertSelective(amountDO);

            RuleDO offHoursDO = new RuleDO();
            offHoursDO.setRuleKey(RuleTypeEnum.OFF_HOURS.getType());
            offHoursDO.setDescription(RuleTypeEnum.OFF_HOURS.getDesc());
            offHoursDO.setRuleType(RuleTypeEnum.OFF_HOURS.getType());
            offHoursDO.setValue(offHours);
            offHoursDO.setStatus(0);
            this.ruleDOMapper.insertSelective(offHoursDO);

            RuleDO blackCheckDO = new RuleDO();
            blackCheckDO.setStatus(0);
            blackCheckDO.setRuleKey(RuleTypeEnum.BLACKLIST.getType());
            blackCheckDO.setDescription(RuleTypeEnum.BLACKLIST.getDesc());
            blackCheckDO.setRuleType(RuleTypeEnum.BLACKLIST.getType());
            blackCheckDO.setValue(String.valueOf(blackCheck));
            // 创建新的规则
            this.ruleDOMapper.insertSelective(blackCheckDO);

            // 更新缓存信息
            List<RuleDO> createList = this.ruleDOMapper.selectAllRule();
            for (RuleDO ruleDO : ruleDOList) {
                redisUtilService.opsForSetObject(RULE_CACHE_NAME, ruleDO);
            }
            return !createList.isEmpty();
        }else{
            // 以及存在规则，调用更新 逻辑
            return  this.updateRule(amount,offHours,blackCheck);
        }
    }

    @Override
    public List<RuleDO> getAllRules() {
        Set<Object> ruleDOSet = this.redisUtilService.members(RULE_CACHE_NAME);
        if(CollectionUtils.isNotEmpty(ruleDOSet)){
            List<RuleDO> ruleDOList = new ArrayList<RuleDO>();
            for (Object ruleDO : ruleDOSet) {
                RuleDO ruleDOObj = (RuleDO) ruleDO;
                ruleDOList.add(ruleDOObj);
            }
            return ruleDOList;
        }else {
            List<RuleDO> ruleDOList = this.ruleDOMapper.selectAllRule();
            return ruleDOList;
        }
    }

    @Transactional
    @Override
    public boolean updateRule(String amount, String offHours, boolean blackCheck) {
        // 创建规则，先删除已经存在的规则，再新建规则
        this.ruleDOMapper.removeCurrentRule();
        // 创建规则，先删除已经存在的规则，再新建规则
        RuleDO amountDO = new RuleDO();
        amountDO.setDescription(RuleTypeEnum.AMOUNT.getDesc());
        amountDO.setRuleType(RuleTypeEnum.AMOUNT.getType());
        amountDO.setRuleKey(RuleTypeEnum.AMOUNT.getType());
        amountDO.setValue(amount);
        amountDO.setStatus(0);
        this.ruleDOMapper.insertSelective(amountDO);

        RuleDO offHoursDO = new RuleDO();
        offHoursDO.setDescription(RuleTypeEnum.OFF_HOURS.getDesc());
        offHoursDO.setRuleType(RuleTypeEnum.OFF_HOURS.getType());
        offHoursDO.setValue(offHours);
        offHoursDO.setStatus(0);
        offHoursDO.setRuleKey(RuleTypeEnum.OFF_HOURS.getType());
        this.ruleDOMapper.insertSelective(offHoursDO);

        RuleDO blackCheckDO = new RuleDO();
        blackCheckDO.setStatus(0);
        blackCheckDO.setDescription(RuleTypeEnum.BLACKLIST.getDesc());
        blackCheckDO.setRuleType(RuleTypeEnum.BLACKLIST.getType());
        blackCheckDO.setValue(String.valueOf(blackCheck));
        blackCheckDO.setRuleKey(RuleTypeEnum.BLACKLIST.getType());
        // 创建新的规则
        this.ruleDOMapper.insertSelective(blackCheckDO);

        // 更新缓存信息
        this.redisUtilService.deleteCache(RULE_CACHE_NAME);
        List<RuleDO> ruleDOList = this.ruleDOMapper.selectAllRule();
        for (RuleDO ruleDO : ruleDOList) {
            redisUtilService.opsForSetObject(RULE_CACHE_NAME, ruleDO);
        }
        return !ruleDOList.isEmpty();
    }
}
