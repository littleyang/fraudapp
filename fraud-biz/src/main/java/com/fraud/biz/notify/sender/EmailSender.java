package com.fraud.biz.notify.sender;

import com.fraud.biz.notify.rocketmq.impl.AlertRecordMessageSendProducerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class EmailSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public String sendToEmail(String email,String body) {
        logger.info("EmailSender: sendToEmail,email="+email+",body="+body);
        return "send ok";
    }

}
