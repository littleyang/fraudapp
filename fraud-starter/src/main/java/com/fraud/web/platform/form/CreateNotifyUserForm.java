package com.fraud.web.platform.form;

import com.sct.commons.api.base.ToString;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
public class CreateNotifyUserForm extends ToString {

    private String user;

    private String name;

    private String destination;

    private String type;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
