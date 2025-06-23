package com.fraud.web.platform.form;

import com.sct.commons.api.base.ToString;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
public class BlackListCreateForm extends ToString {

    private String account;

    private String name;

    private String reason;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
