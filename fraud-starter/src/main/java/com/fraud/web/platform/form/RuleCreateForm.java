package com.fraud.web.platform.form;

import com.sct.commons.api.base.ToString;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
public class RuleCreateForm extends ToString {

    private String amount;

    /**
     * 2-3，横线划分
     */
    private String offHours;

    private boolean blackCheck;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOffHours() {
        return offHours;
    }

    public void setOffHours(String offHours) {
        this.offHours = offHours;
    }

    public boolean isBlackCheck() {
        return blackCheck;
    }

    public void setBlackCheck(boolean blackCheck) {
        this.blackCheck = blackCheck;
    }
}
