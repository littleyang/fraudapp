package com.fraud.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.fraud.biz.notify.AlertRecordMessageDTO;
import com.fraud.biz.notify.NotifySenderService;
import com.fraud.biz.service.AlertRecordService;
import com.fraud.repository.app.model.AlertRecordDO;
import com.sct.bizmsg.exception.BusinessException;
import com.sct.context.trace.TraceIdContext;
import com.fraud.commons.enums.FraudRestResultCode;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2024/9/18
 */
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.fraud.consumer.group}", topic = "${rocketmq.fraud_alert_topic}")
public class AlertRecordMessageListener implements RocketMQListener<AlertRecordMessageDTO> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AlertRecordService alertRecordService;

    @Autowired
    private NotifySenderService notifySenderService;

    @Override
    public void onMessage(AlertRecordMessageDTO message) {
        logger.info("AlertRecordMessageListener.onMessage:{}", JSONObject.toJSONString(message));
        if(ObjectUtils.isNotEmpty(message)){
            AlertRecordDO alertRecordDO = new AlertRecordDO();
            alertRecordDO.setAmount(message.getAmount());
            alertRecordDO.setTransactionId(message.getTransactionId());
            alertRecordDO.setAccount(message.getAccount());
            alertRecordDO.setContent(message.getContent());
            alertRecordDO.setDestination(message.getDestination());
            alertRecordDO.setNotifyUser(message.getNotifyUser());
            alertRecordDO.setType(message.getType());
            alertRecordDO.setGmtCreator(message.getGmtCreator());
            alertRecordDO.setGmtUpdater(message.getGmtUpdater());

            try {
                // 记录数据库
                this.alertRecordService.createAlertRecord(alertRecordDO);
                // 发送消息
                this.notifySenderService.sendNotifyRecord(message);

            }catch (Exception exception){
                logger.info("写入日志失败,error={}",exception.getMessage());
                throw new BusinessException(FraudRestResultCode.ERROR,"写入日志失败");
            }
        }
    }
}
