package com.fraud.biz.service;

import com.fraud.repository.app.model.BlacklistDO;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.result.vo.PageResultVO;

import java.util.List;
import java.util.Set;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
public interface BlackListService {

    public boolean createBlackList(BlacklistDO blacklistDO);

    public boolean updateBlackList(BlacklistDO blacklistDO);

    public boolean deleteBlackList(long id);

    public BlacklistDO getBlacklistDOById(String id);

    public List<BlacklistDO> getAllBlacklists();

    public PageResultVO<BlacklistDO> getBlacklistsByPage(String account ,
                                                         Page page);

    boolean isBlackListExist(String account);

    Set<String> getAllBlackListedAccount();
}
