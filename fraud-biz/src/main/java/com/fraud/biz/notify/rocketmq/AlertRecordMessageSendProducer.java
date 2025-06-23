package com.fraud.biz.notify.rocketmq;

import com.fraud.biz.notify.AlertRecordMessageDTO;

import java.util.List;

public interface AlertRecordMessageSendProducer {

    boolean send(List<AlertRecordMessageDTO> alerts);


}
