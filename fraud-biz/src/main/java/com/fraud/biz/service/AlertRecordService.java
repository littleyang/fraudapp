package com.fraud.biz.service;

import com.fraud.repository.app.model.AlertRecordDO;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.result.vo.PageResultVO;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public interface AlertRecordService {

    boolean createAlertRecord(AlertRecordDO alertRecordDO);

    AlertRecordDO findAlertRecordById(Long id);

    PageResultVO<AlertRecordDO> findAlertRecordByPage(String user, Page page);

    boolean deleteById(long id);

}
