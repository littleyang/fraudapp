package com.fraud.commons.enums;

/**
 * 消息类型枚举
 * 0:CREATE, 1:DELETE, 2:CLOSE, 3:DISCARD, 4:REPLY
 */
public enum RuleTypeEnum {
    AMOUNT("amount_threshold", "固定额度检测"),
    BLACKLIST("blacklist", "黑名单检测"),
    OFF_HOURS("off_hours", "非交易时间检测");

    private final String type;
    private final String desc;


    RuleTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDesc(String type) {
        for (RuleTypeEnum enumObj : values()) {
            if (enumObj.getType() == type) {
                return enumObj.getDesc();
            }
        }
        return null;
    }

    public static String getDesc(RuleTypeEnum type) {
        return type.getDesc();
    }
}
