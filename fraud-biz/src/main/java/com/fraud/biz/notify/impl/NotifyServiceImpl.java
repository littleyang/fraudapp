package com.fraud.biz.notify.impl;

import cn.hutool.json.JSONUtil;
import com.fraud.biz.notify.AlertRecordMessageDTO;
import com.fraud.biz.notify.NotifyService;
import com.fraud.biz.notify.rocketmq.AlertRecordMessageSendProducer;
import com.fraud.biz.service.NotifyUserService;
import com.fraud.repository.app.model.NotifyUserDO;
import com.fraud.repository.app.model.TransactionDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotifyUserService notifyUserService;

    @Autowired
    private AlertRecordMessageSendProducer alertRecordMessageSendProducer;

    @Override
    public boolean sendTransactionCheckNotify(TransactionDO transactionDO, List<String> reasons) {

        logger.info("NotifyServiceImpl.sendTransactionCheckNotify,transactionDO={},reasons={}",
                transactionDO, reasons);

       try {
           // 查询需要通知的人员
           List<NotifyUserDO> notifyUserDOList = this.notifyUserService.selectAllNotifyUser();
           String content = "交易信息：" + JSONUtil.toJsonStr(transactionDO) + "触发反欺诈规则\n"
                   + "触发原因如下: " + JSONUtil.toJsonStr(reasons);

           List<AlertRecordMessageDTO> alertRecordMessageDTOS = new ArrayList<>();
           for(NotifyUserDO notifyUserDO : notifyUserDOList){
               AlertRecordMessageDTO alertRecord= new AlertRecordMessageDTO();
               alertRecord.setTransactionId(transactionDO.getTransactionId());
               alertRecord.setGmtCreator(transactionDO.getGmtCreator());
               alertRecord.setGmtUpdater(transactionDO.getGmtUpdater());
               alertRecord.setAmount(transactionDO.getAmount());
               alertRecord.setAccount(transactionDO.getAccount());
               alertRecord.setNotifyUser(notifyUserDO.getUser());
               alertRecord.setDestination(notifyUserDO.getDestination());
               alertRecord.setType(notifyUserDO.getType());

               alertRecord.setContent(content);
               alertRecordMessageDTOS.add(alertRecord);
           }
           // 发送消息
           if(!alertRecordMessageDTOS.isEmpty()){
               alertRecordMessageSendProducer.send(alertRecordMessageDTOS);
           }
           return true;
       }catch ( Exception exception){
           logger.error("NotifyServiceImpl.sendTransactionCheckNotify,exception={}", exception);
       }
        return false;
    }
}
