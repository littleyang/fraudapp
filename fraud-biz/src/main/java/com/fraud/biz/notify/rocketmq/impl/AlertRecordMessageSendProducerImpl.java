package com.fraud.biz.notify.rocketmq.impl;

import com.fraud.biz.notify.AlertRecordMessageDTO;
import com.fraud.biz.notify.rocketmq.AlertRecordMessageSendProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */

@Service
public class AlertRecordMessageSendProducerImpl implements AlertRecordMessageSendProducer {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.fraud_alert_topic:fraud_alert_topic}")
    private String fraudAlertTopic;

    @Override
    public boolean send(List<AlertRecordMessageDTO> alerts) {
        boolean flag = true;
        for (AlertRecordMessageDTO alert : alerts) {
            try {
                logger.info("AlertRecordMessageSendProducerImpl,send alert to fraudAlertTopic, alert:{}", alert);
                rocketMQTemplate.syncSend(fraudAlertTopic, alert);
            } catch (Exception ex) {
                logger.error("AlertRecordMessageSendProducerImpl,send alert error", ex);
                flag = false;
            }
        }
        return flag;
    }
}
