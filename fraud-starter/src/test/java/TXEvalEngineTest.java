
import com.fraud.biz.notify.NotifyService;
import com.fraud.biz.service.BlackListService;
import com.fraud.biz.service.TransactionService;
import com.fraud.biz.service.impl.LockService;
import com.fraud.biz.txengine.TXEvalEngine;
import com.fraud.biz.txengine.TxRuleLoader;
import com.fraud.commons.enums.RuleTypeEnum;
import com.fraud.repository.app.model.RuleDO;
import com.fraud.repository.app.model.TransactionDO;
import com.sct.bizmsg.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TXEvalEngineTest {

    private BlackListService blackListService;
    private TxRuleLoader txRuleLoader;
    private TransactionService transactionService;
    private NotifyService notifyService;
    private LockService lockService;
    private TXEvalEngine txEvalEngine;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {
        blackListService = mock(BlackListService.class, withSettings().lenient());
        txRuleLoader = mock(TxRuleLoader.class, withSettings().lenient());
        transactionService = mock(TransactionService.class, withSettings().lenient());
        notifyService = mock(NotifyService.class, withSettings().lenient());
        lockService = mock(LockService.class, withSettings().lenient());

        txEvalEngine = new TXEvalEngine(blackListService, txRuleLoader, transactionService, notifyService, lockService);

        // 默认规则 stub
        when(txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType())).thenReturn(null);
        when(txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType())).thenReturn(null);
        when(txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType())).thenReturn(null);

        // 默认锁逻辑 stub
        when(lockService.tryLock(anyString())).thenReturn(true);
        doNothing().when(lockService).unlock(anyString());
    }

    private TransactionDO buildTx(String id, String account, String amount, String time) throws Exception {
        TransactionDO tx = new TransactionDO();
        tx.setTransactionId(id);
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setTransactionTime(sdf.parse(time));
        tx.setDescription("测试交易");
        return tx;
    }

    @Test
    public void testEvaluateTx_AllRulesTriggered() throws Exception {
        RuleDO amountRule = new RuleDO();
        amountRule.setRuleKey(RuleTypeEnum.AMOUNT.getType());
        amountRule.setValue("1000");

        RuleDO blackRule = new RuleDO();
        blackRule.setRuleKey(RuleTypeEnum.BLACKLIST.getType());
        blackRule.setValue("true");

        RuleDO timeRule = new RuleDO();
        timeRule.setRuleKey(RuleTypeEnum.OFF_HOURS.getType());
        timeRule.setValue("0-6");

        when(txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType())).thenReturn(amountRule);
        when(txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType())).thenReturn(blackRule);
        when(txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType())).thenReturn(timeRule);
        when(blackListService.isBlackListExist("user123")).thenReturn(true);

        TransactionDO tx = buildTx("txid1", "user123", "1500", "2025-06-20 01:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);

        assertEquals(3, reasons.size());
        verify(transactionService).createTransaction(tx);
        verify(notifyService).sendTransactionCheckNotify(eq(tx), anyList());
    }

    @Test
    public void testEvaluateTx_NoViolation() throws Exception {
        TransactionDO tx = buildTx("txid2", "user123", "500", "2025-06-20 12:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);

        assertTrue(reasons.isEmpty());
        verify(transactionService, never()).createTransaction(any());
        verify(notifyService, never()).sendTransactionCheckNotify(any(), any());
    }

    @Test
    public void testEvaluateTx_AmountEqualThreshold_NotTriggered() throws Exception {
        RuleDO amountRule = new RuleDO();
        amountRule.setRuleKey(RuleTypeEnum.AMOUNT.getType());
        amountRule.setValue("1000");

        when(txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType())).thenReturn(amountRule);

        TransactionDO tx = buildTx("txid3", "user123", "1000", "2025-06-20 02:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);

        assertTrue(reasons.isEmpty());
    }

    @Test
    public void testEvaluateTx_OffHoursParsingError_IgnoredGracefully() throws Exception {
        RuleDO timeRule = new RuleDO();
        timeRule.setRuleKey(RuleTypeEnum.OFF_HOURS.getType());
        timeRule.setValue("invalid-format");

        when(txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType())).thenReturn(timeRule);

        TransactionDO tx = buildTx("txid4", "user123", "500", "2025-06-20 01:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);
        assertTrue(reasons.isEmpty());
    }

    @Test
    public void testEvaluateTx_NullTransactionTime_HandledGracefully() {
        RuleDO timeRule = new RuleDO();
        timeRule.setRuleKey(RuleTypeEnum.OFF_HOURS.getType());
        timeRule.setValue("0-6");

        when(txRuleLoader.loadRule(RuleTypeEnum.OFF_HOURS.getType())).thenReturn(timeRule);

        TransactionDO tx = new TransactionDO();
        tx.setTransactionId("txid5");
        tx.setAccount("user123");
        tx.setAmount("500");
        tx.setTransactionTime(null);

        List<String> reasons = txEvalEngine.evaluateTx(tx);
        assertTrue(reasons.isEmpty());
    }

    @Test
    public void testEvaluateTx_NullAmount_DefaultsToNoViolation() throws Exception {
        RuleDO amountRule = new RuleDO();
        amountRule.setRuleKey(RuleTypeEnum.AMOUNT.getType());
        amountRule.setValue("1000");

        when(txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType())).thenReturn(amountRule);

        TransactionDO tx = buildTx("txid6", "user123", null, "2025-06-20 01:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);
        assertTrue(reasons.isEmpty());
    }

    @Test
    public void testEvaluateTx_BlacklistCheckFalse_NotTriggered() throws Exception {
        RuleDO blackRule = new RuleDO();
        blackRule.setRuleKey(RuleTypeEnum.BLACKLIST.getType());
        blackRule.setValue("false");

        when(txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType())).thenReturn(blackRule);

        TransactionDO tx = buildTx("txid7", "user123", "500", "2025-06-20 12:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);

        assertTrue(reasons.isEmpty());
        verify(blackListService, never()).isBlackListExist(any());
    }

    @Test
    public void testEvaluateTx_BlacklistServiceThrowsException_IgnoredGracefully() throws Exception {
        RuleDO blackRule = new RuleDO();
        blackRule.setRuleKey(RuleTypeEnum.BLACKLIST.getType());
        blackRule.setValue("true");

        when(txRuleLoader.loadRule(RuleTypeEnum.BLACKLIST.getType())).thenReturn(blackRule);
        when(blackListService.isBlackListExist(any())).thenThrow(new RuntimeException("Redis down"));

        TransactionDO tx = buildTx("txid8", "user123", "500", "2025-06-20 02:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);
        assertTrue(reasons.isEmpty());
    }

    @Test
    public void testEvaluateTx_NotificationServiceThrowsException_TransactionStillRecorded() throws Exception {
        RuleDO amountRule = new RuleDO();
        amountRule.setRuleKey(RuleTypeEnum.AMOUNT.getType());
        amountRule.setValue("1000");

        when(txRuleLoader.loadRule(RuleTypeEnum.AMOUNT.getType())).thenReturn(amountRule);
        doThrow(new RuntimeException("Notification failed")).when(notifyService)
                .sendTransactionCheckNotify(any(), any());

        TransactionDO tx = buildTx("txid9", "user123", "2000", "2025-06-20 02:00:00");

        List<String> reasons = txEvalEngine.evaluateTx(tx);
        assertEquals(1, reasons.size());
        verify(transactionService).createTransaction(tx);
    }

    @Test
    public void testEvaluateTx_LockFailed_ShouldThrowBusinessException() throws Exception {
        when(lockService.tryLock(anyString())).thenReturn(false);

        TransactionDO tx = buildTx("txid10", "user123", "1000", "2025-06-20 01:00:00");

        BusinessException ex = assertThrows(BusinessException.class, () -> txEvalEngine.evaluateTx(tx));
        assertEquals("2003", ex.getCode()); // DATA_DUPLICATION_ERROR
    }
}
