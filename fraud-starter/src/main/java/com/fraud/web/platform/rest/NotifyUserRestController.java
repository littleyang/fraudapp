package com.fraud.web.platform.rest;

import cn.hutool.core.util.ObjectUtil;
import com.fraud.biz.service.NotifyUserService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.model.BlacklistDO;
import com.fraud.repository.app.model.NotifyUserDO;
import com.fraud.web.platform.form.*;
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

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
@RestController
@RequestMapping("/notify/user")
public class NotifyUserRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotifyUserService notifyUserService;

    @GetMapping("/queryByPage")
    public Object queryByPage(HttpServletRequest request, QueryNotifyUserForm form){
        logger.info("NotifyUserRestController.queryByPage,form={}",form);
        //构造分页以及offset。默认20条数据一页
        Integer pageNo = form.getPageNo();
        Integer pageSize = form.getPageSize();
        Page page;
        if (pageNo == null || pageSize == null) {
            page = Pages.firstPage();
        } else {
            page = Pages.page(pageNo, pageSize);
        }
        PageResultVO<NotifyUserDO> pageResultVO = this.notifyUserService.selectNotifyUser(form.getUser(), page);
        return pageResultVO;

    }

    @PostMapping("/create")
    public Object createNotifyUser(HttpServletRequest request,
                                  @RequestBody CreateNotifyUserForm createForm) {

        logger.info("NotifyUserRestController.createNotifyUser, createForm={}", createForm);

        if(ObjectUtil.isNull(createForm)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(createForm.getUser())
                ||ObjectUtil.isNull(createForm.getName())
                ||ObjectUtil.isNull(createForm.getDestination())
                ||ObjectUtil.isNull(createForm.getType())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        // FIXME:参数判断也检测，必然说类型，邮件格式，手机格式等
        NotifyUserDO notifyUserDO = new NotifyUserDO();
        notifyUserDO.setUser(createForm.getUser());
        notifyUserDO.setName(createForm.getName());
        notifyUserDO.setDestination(createForm.getDestination());
        notifyUserDO.setType(createForm.getType());
        // FIXME: 获取用户信息写入
        notifyUserDO.setGmtCreator("t");
        notifyUserDO.setGmtUpdater("t");
        boolean result = this.notifyUserService.createNotifyUser(notifyUserDO);
        return result;

    }

    @PutMapping("/update")
    public Object updateNotifyUser(HttpServletRequest request,
                                  @RequestBody UpdateNotifyUserForm updateForm) {

        logger.info("NotifyUserRestController.updateNotifyUser, updateForm={}", updateForm);

        if(ObjectUtil.isNull(updateForm)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        if(ObjectUtil.isNull(updateForm.getUser())
                ||ObjectUtil.isNull(updateForm.getName())
                ||ObjectUtil.isNull(updateForm.getDestination())
                ||ObjectUtil.isNull(updateForm.getType())
                ||ObjectUtil.isNull(updateForm.getId())){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        NotifyUserDO notifyUserDO = new NotifyUserDO();
        notifyUserDO.setId(updateForm.getId());
        notifyUserDO.setUser(updateForm.getUser());
        notifyUserDO.setName(updateForm.getName());
        notifyUserDO.setDestination(updateForm.getDestination());
        notifyUserDO.setType(updateForm.getType());
        // FIXME: 获取用户信息写入
        notifyUserDO.setGmtUpdater("t");
        boolean result = this.notifyUserService.updateNotifyUser(notifyUserDO);
        return result;
    }

    @DeleteMapping("/delete")
    public Object deleteById(HttpServletRequest request,@Param("id") Long id){

        logger.info("NotifyUserRestController.deleteById, id={}", id);

        if(ObjectUtil.isNull(id)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        boolean result = this.notifyUserService.deleteNotifyUser(id);
        return result;

    }


    @GetMapping("/detail")
    public Object detailBlackListDO(HttpServletRequest request,@Param("id") Long id){

        logger.info("NotifyUserRestController.detailBlackListDO, id={}", id);

        if(ObjectUtil.isNull(id)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        NotifyUserDO result = this.notifyUserService.getNotifyUserByID(id);
        return result;

    }
}
