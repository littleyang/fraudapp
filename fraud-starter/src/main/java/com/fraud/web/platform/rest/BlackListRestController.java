package com.fraud.web.platform.rest;

import cn.hutool.core.util.ObjectUtil;
import com.fraud.biz.service.BlackListService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.model.BlacklistDO;
import com.fraud.web.platform.form.BlackListCreateForm;
import com.fraud.web.platform.form.BlackListQueryForm;
import com.fraud.web.platform.form.BlackListUpdateForm;
import com.fraud.web.platform.form.RuleCreateForm;
import com.sct.bizmsg.exception.BusinessException;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.page.Pages;
import com.sct.commons.api.result.vo.PageResultVO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
@RestController
@RequestMapping("/blacklist")
public class BlackListRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private BlackListService blackListService;

    @GetMapping("/queryByPage")
    public Object queryByPage(HttpServletRequest request, BlackListQueryForm form){
        logger.info("BlackListRestController.queryByPage,form={}",form);
        //构造分页以及offset。默认20条数据一页
        Integer pageNo = form.getPageNo();
        Integer pageSize = form.getPageSize();
        Page page;
        if (pageNo == null || pageSize == null) {
            page = Pages.firstPage();
        } else {
            page = Pages.page(pageNo, pageSize);
        }
        PageResultVO<BlacklistDO> pageResultVO = this.blackListService.getBlacklistsByPage(form.getAccount(), page);
        return pageResultVO;

    }

    @PostMapping("/create")
    public Object createBlackList(HttpServletRequest request,
                             @RequestBody BlackListCreateForm createForm) {

        logger.info("BlackListRestController.createBlackList, createForm={}", createForm);

        if(ObjectUtil.isNull(createForm)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(createForm.getAccount())
                ||ObjectUtil.isNull(createForm.getName())
                ||ObjectUtil.isNull(createForm.getReason())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }
        BlacklistDO blacklistDO = new BlacklistDO();
        blacklistDO.setAccount(createForm.getAccount());
        blacklistDO.setName(createForm.getName());
        blacklistDO.setReason(createForm.getReason());
        // FIXME: 获取用户信息写入
        blacklistDO.setGmtCreator("t");
        blacklistDO.setGmtUpdater("t");
        boolean result = this.blackListService.createBlackList(blacklistDO);
        return result;

    }

    @PutMapping("/update")
    public Object updateBlackList(HttpServletRequest request,
                             @RequestBody BlackListUpdateForm updateForm) {

        logger.info("BlackListRestController.updateBlackList, updateForm={}", updateForm);

        if(ObjectUtil.isNull(updateForm)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(updateForm.getAccount())
                ||ObjectUtil.isNull(updateForm.getId())
                ||ObjectUtil.isNull(updateForm.getName())
                ||ObjectUtil.isNull(updateForm.getReason())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        BlacklistDO blacklistDO = new BlacklistDO();
        blacklistDO.setAccount(updateForm.getAccount());
        blacklistDO.setId(updateForm.getId());
        blacklistDO.setName(updateForm.getName());
        blacklistDO.setReason(updateForm.getReason());
        blacklistDO.setStatus(updateForm.getStatus());
        // FIXME: 获取用户信息写入
        blacklistDO.setGmtUpdater("t");
        boolean result = this.blackListService.updateBlackList(blacklistDO);
        return result;
    }

    @DeleteMapping("/delete")
    public Object deleteById(HttpServletRequest request,@Param("id") Long id){

        logger.info("BlackListRestController.deleteById, id={}", id);

        if(ObjectUtil.isNull(id)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }
        //BlacklistDO existDO = this.blackListService.getBlacklistDOById(String.valueOf(id));
        boolean result = this.blackListService.deleteBlackList(id);
        return result;

    }


    @GetMapping("/detail")
    public Object detailBlackListDO(HttpServletRequest request,@Param("id") Long id){

        logger.info("BlackListRestController.detailBlackListDO, id={}", id);

        if(ObjectUtil.isNull(id)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        BlacklistDO result = this.blackListService.getBlacklistDOById(String.valueOf(id));
        return result;

    }

}
