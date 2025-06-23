package com.fraud.api.feign;

import com.fraud.api.reqest.fraud.FraudTxEvaReqDTO;
import com.sct.commons.api.result.vo.PageResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */
@FeignClient(value = "fraud")
public interface FraudFeignAPI {

    @PostMapping("/fraud/feign/tx/evaluate")
    String fraudTxEvaluate(@RequestBody FraudTxEvaReqDTO fraudTxEvaReqDTO);

}
