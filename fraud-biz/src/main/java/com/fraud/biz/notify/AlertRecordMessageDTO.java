package com.fraud.biz.notify;

import com.sct.commons.api.base.ToString;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public class AlertRecordMessageDTO extends ToString {

    private String traceId;

    private String notifyUser;

    private String destination;

    private String type;

    private String transactionId;

    private String account;

    private String amount;

    private String content;

    private String gmtCreator;

    private String gmtUpdater;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(String notifyUser) {
        this.notifyUser = notifyUser;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGmtCreator() {
        return gmtCreator;
    }

    public void setGmtCreator(String gmtCreator) {
        this.gmtCreator = gmtCreator;
    }

    public String getGmtUpdater() {
        return gmtUpdater;
    }

    public void setGmtUpdater(String gmtUpdater) {
        this.gmtUpdater = gmtUpdater;
    }
}
