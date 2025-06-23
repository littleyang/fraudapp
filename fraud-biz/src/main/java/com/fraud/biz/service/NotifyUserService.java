package com.fraud.biz.service;

import com.fraud.repository.app.model.NotifyUserDO;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.result.vo.PageResultVO;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public interface NotifyUserService {

    boolean createNotifyUser(NotifyUserDO notifyUserDO);

    boolean updateNotifyUser(NotifyUserDO notifyUserDO);

    boolean deleteNotifyUser(long id);

    NotifyUserDO getNotifyUserByID(long id);

    List<NotifyUserDO> selectAllNotifyUser();

    PageResultVO<NotifyUserDO> selectNotifyUser(String user, Page page);

}
