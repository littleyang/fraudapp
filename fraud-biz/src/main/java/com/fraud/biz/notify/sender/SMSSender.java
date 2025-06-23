package com.fraud.biz.notify.sender;

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
public class SMSSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String sendSms(String mobile,String content) {
        logger.info("SMSSender: sendSms,mobile={},content={}",mobile,content);
        return "send ok";
    }
}
