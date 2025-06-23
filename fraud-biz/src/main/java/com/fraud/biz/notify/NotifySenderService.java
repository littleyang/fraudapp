package com.fraud.biz.notify;

import com.fraud.biz.notify.sender.EmailSender;
import com.fraud.biz.notify.sender.PhoneSender;
import com.fraud.biz.notify.sender.SMSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class NotifySenderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PhoneSender phoneSender;

    @Autowired
    private SMSSender smsSender;


    public void sendNotifyRecord(AlertRecordMessageDTO alertRecordMessageDTO){
        if(alertRecordMessageDTO!=null){
            switch (alertRecordMessageDTO.getType()){
                case "email":
                    emailSender.sendToEmail(alertRecordMessageDTO.getDestination(), alertRecordMessageDTO.getContent());
                    logger.info("emailSender,destination:{}",alertRecordMessageDTO.getDestination());
                    break;
                case "sms":
                    smsSender.sendSms(alertRecordMessageDTO.getDestination(), alertRecordMessageDTO.getContent());
                    logger.info("smsSender,destination:{}",alertRecordMessageDTO.getDestination());
                    break;
                case "phone":
                    phoneSender.callPhone(alertRecordMessageDTO.getDestination(), alertRecordMessageDTO.getContent());
                    logger.info("phoneSender,destination:{}",alertRecordMessageDTO.getDestination());
                    break;
                default:
                    logger.info("sendNotifyRecord,发送消息失败,destination:{}",alertRecordMessageDTO.getDestination());
                    break;
            }
        }
    }

}
