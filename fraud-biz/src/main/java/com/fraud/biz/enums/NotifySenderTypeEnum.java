package com.fraud.biz.enums;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public enum NotifySenderTypeEnum {
    EMAIL("email"),
    SMS("sms"),
    PHONE("phone"),
    ;


    private String type;


    NotifySenderTypeEnum(String type) {
        this.type = type;
    }
}
