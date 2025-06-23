package com.fraud.web.platform.form;

import com.sct.commons.api.base.ToString;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
public class AlertQueryForm extends ToString {

    private String user;

    private String transactionID;

    private int pageNo;

    private int pageSize;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }
}
