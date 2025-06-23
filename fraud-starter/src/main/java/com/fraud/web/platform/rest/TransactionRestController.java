package com.fraud.web.platform.rest;

import cn.hutool.core.util.ObjectUtil;
import com.fraud.biz.service.AlertRecordService;
import com.fraud.biz.service.TransactionService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.repository.app.model.AlertRecordDO;
import com.fraud.repository.app.model.TransactionDO;
import com.fraud.web.platform.form.AlertQueryForm;
import com.fraud.web.platform.form.TXQueryForm;
import com.sct.bizmsg.exception.BusinessException;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.page.Pages;
import com.sct.commons.api.result.vo.PageResultVO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/21
 */
@RestController
@RequestMapping("/transaction")
public class TransactionRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionService transactionService;


    @GetMapping("/queryByPage")
    public Object queryByPage(HttpServletRequest request, TXQueryForm form){
        logger.info("TransactionRestController.queryByPage,form={}",form);
        //构造分页以及offset。默认20条数据一页
        Integer pageNo = form.getPageNo();
        Integer pageSize = form.getPageSize();
        Page page;
        if (pageNo == null || pageSize == null) {
            page = Pages.firstPage();
        } else {
            page = Pages.page(pageNo, pageSize);
        }
        TransactionDO transactionDO = new TransactionDO();
        transactionDO.setAccount(form.getAccount());
        transactionDO.setTransactionId(form.getTransactionID());

        PageResultVO<TransactionDO> pageResultVO = this.transactionService.queryTransaction(transactionDO, page);
        return pageResultVO;

    }


    @DeleteMapping("/delete")
    public Object deleteById(HttpServletRequest request,@Param("txId") String txId){

        logger.info("TransactionRestController.deleteById, txId={}", txId);

        if(ObjectUtil.isNull(txId)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        boolean result = this.transactionService.deleteTransaction(txId);
        return result;

    }


    @GetMapping("/detail")
    public Object detailAlertDO(HttpServletRequest request,@Param("id") Long id){

        logger.info("TransactionRestController.detailAlertDO, id={}", id);

        if(ObjectUtil.isNull(id)){
            throw new BusinessException(FraudRestResultCode.PARAMETER_ERROR,"参数不能为空");
        }

        TransactionDO result = this.transactionService.getTransactionById(id);
        return result;

    }
}
