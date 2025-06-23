package com.fraud.commons.enums;


import com.sct.bizmsg.codes.BizResultCode;

public enum FraudRestResultCode implements BizResultCode {

    SUCCESS("success", "调用成功", "系统接口调用成功"),
    ERROR("error", "调用错误", "系统调用信息错误"),

    PARAMETER_ERROR("1000", "参数不能为空", "参数错误"),
    VERIFY_CODE_ERROR("1001", "验证码错误", "验证码错误"),
    USER_PASSWORD_ERROR("1003", "用户密码验证失败", "用户密码验证失败"),
    ACCOUNT_BAND("1005","账号禁用","账号禁用"),
    USER_DATA_NOT_EXIST("1004", "账号信息不存在请联系注册", "账号信息不存在请联系注册"),

    DATA_NOT_EXIST("2001", "数据不存在", "数据不存在"),
    DATA_RELATION_ERROR("2002", "数据存在绑定资源", "数据存在绑定资源"),
    DATA_DUPLICATION_ERROR("2003", "已经存在", "数据重复等问题"),
    QUOTA_OVER_LIMIT_ERROR("2004", "资源超限额", "资源超限额"),

    DATA_NOT_PERMIT_DELETE("2004", "数据不能删除", "数据不能删除"),
    MENUS_NOT_EXITS("3000","权限资源不存在","权限资源不存在"),
    DATA_NOT_PERMISSION("4000","无查询权限","无查询权限"),

    CHECK_NOT_PERMISSION("4001","权限验证失败","权限验证失败"),


    ;

    private String code;
    private String alertMessage;
    private String debugMessage;

    private FraudRestResultCode(String code) {
        this.code = code;
    }

    private FraudRestResultCode(String code, String alertMessage) {
        this.code = code;
        this.alertMessage = alertMessage;
    }

    private FraudRestResultCode(String code, String alertMessage, String debugMessage) {
        this.code = code;
        this.alertMessage = alertMessage;
        this.debugMessage = debugMessage;
    }


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getAlertMessage() {
        return this.alertMessage;
    }

    @Override
    public  String getDebugMessage() {
        return this.debugMessage;
    }
}
