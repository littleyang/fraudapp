
import com.fraud.FraudApplication;
import com.fraud.biz.notify.NotifyService;
import com.fraud.biz.service.BlackListService;
import com.fraud.biz.service.TransactionService;
import com.fraud.biz.service.impl.LockService;
import com.fraud.biz.txengine.TxRuleLoader;
import com.fraud.biz.txengine.TXEvalEngine;
import com.fraud.commons.enums.FraudRestResultCode;
import com.fraud.commons.enums.RuleTypeEnum;
import com.fraud.repository.app.model.RuleDO;
import com.fraud.repository.app.model.TransactionDO;
import com.sct.bizmsg.exception.BusinessException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = FraudApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@MockitoSettings(strictness = Strictness.LENIENT)
class TXEvalEngineIntegrationTest {

    @MockBean
    private BlackListService blackListService;

    @MockBean
    private TxRuleLoader txRuleLoader;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private NotifyService notifyService;

    @MockBean
    private LockService lockService;

    @Autowired
    private TXEvalEngine txEvalEngine;

    private TransactionDO createSampleTx() throws Exception {
        TransactionDO tx = new TransactionDO();
        tx.setTransactionId("tx_" + RandomStringUtils.randomAlphanumeric(8));
        tx.setAccount("user001");
        tx.setAmount("500000");
        tx.setTransactionTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse("2025-06-21 12:00:00")); // 白天，不触发 off-hours
        return tx;
    }

    private RuleDO rule(String type, String value) {
        RuleDO r = new RuleDO();
        r.setRuleType(type);
        r.setValue(value);
        return r;
    }

    @Test
    void testEvaluateTx_AllRulesTriggered() throws Exception {
        TransactionDO tx = createSampleTx();
        // 🟢 显式设置锁成功
        when(lockService.tryLock(anyString())).thenReturn(true);
        doNothing().when(lockService).unlock(anyString());

        when(txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType()))
                .thenReturn(rule(RuleTypeEnum.AMOUNT.getType(), "1000")); // 金额触发
        when(txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType()))
                .thenReturn(rule(RuleTypeEnum.BLACKLIST.getType(), "true")); // 黑名单启用
        when(txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType()))
                .thenReturn(rule(RuleTypeEnum.OFF_HOURS.getType(), "0-23")); // 全时段触发
        when(blackListService.isBlackListExist(anyString())).thenReturn(true);

        List<String> reasons = txEvalEngine.evaluateTx(tx);

        assertEquals(3, reasons.size());
        verify(transactionService, times(1)).createTransaction(any());
        verify(notifyService, times(1)).sendTransactionCheckNotify(any(), anyList());
        verify(lockService).unlock(tx.getTransactionId());
    }

    @Test
    void testEvaluateTx_NoFraud() throws Exception {
        TransactionDO tx = createSampleTx();

        when(txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType()))
                .thenReturn(rule(RuleTypeEnum.AMOUNT.getType(), "999999")); // 不触发
        when(txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType()))
                .thenReturn(rule(RuleTypeEnum.BLACKLIST.getType(), "false")); // 不启用黑名单
        when(txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType()))
                .thenReturn(rule(RuleTypeEnum.OFF_HOURS.getType(), "0-0")); // 不在时间内
        when(blackListService.isBlackListExist(anyString())).thenReturn(false);
        // 🟢 显式设置锁成功
        when(lockService.tryLock(tx.getTransactionId())).thenReturn(true);

        List<String> reasons = txEvalEngine.evaluateTx(tx);

        doNothing().when(lockService).unlock(tx.getTransactionId());

        assertTrue(reasons.isEmpty());
        verify(transactionService, never()).createTransaction(any());
        verify(notifyService, never()).sendTransactionCheckNotify(any(), anyList());
        verify(lockService).unlock(tx.getTransactionId());
    }

    @Test
    void testEvaluateTx_LockFailed_ShouldThrowDuplicationError() throws Exception {
        TransactionDO tx = createSampleTx();
        // 🟢 显式设置锁成功
        when(lockService.tryLock(anyString())).thenReturn(true);
        doNothing().when(lockService).unlock(anyString());

        when(lockService.tryLock(anyString())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> txEvalEngine.evaluateTx(tx));
        assertEquals(FraudRestResultCode.DATA_DUPLICATION_ERROR.getCode(), ex.getCode());
        verify(lockService, never()).unlock(anyString());
        verify(transactionService, never()).createTransaction(any());
        verify(notifyService, never()).sendTransactionCheckNotify(any(), any());
    }
}
