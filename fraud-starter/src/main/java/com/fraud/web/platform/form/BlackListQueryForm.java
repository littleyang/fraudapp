package com.fraud.web.platform.form;

import com.sct.commons.api.base.ToString;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
public class BlackListQueryForm extends ToString {

    private String account;

    private String name;

    private String reason;

    private int pageNo;

    private int pageSize;

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

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
