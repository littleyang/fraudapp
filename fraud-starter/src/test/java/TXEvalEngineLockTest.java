
import com.fraud.biz.notify.NotifyService;
import com.fraud.biz.service.BlackListService;
import com.fraud.biz.service.TransactionService;
import com.fraud.biz.service.impl.LockService;
import com.fraud.biz.txengine.TxRuleLoader;
import com.fraud.biz.txengine.TXEvalEngine;
import com.fraud.repository.app.model.RuleDO;
import com.fraud.repository.app.model.TransactionDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 正常执行流程
 * 锁竞争失败抛出异常
 * 并发环境下仅一个线程成功执行业务逻辑的关键验证
 * 测试方法说明:
 * | 测试方法                                            | 说明                         |
 * | ----------------------------------------------- | -------------------------- |
 * | `testEvaluateTx_NormalFlow`                     | 正常命中所有规则，触发通知。             |
 * | `testEvaluateTx_ConcurrentLockPrevented`        | 模拟锁获取失败，触发重复提交异常。          |
 * | `testConcurrentLocking_OnlyOneThreadShouldPass` | 并发模拟下，确保只有一个线程执行业务，其余被锁拦截。 |
 */

/**
 * 并发控制单元测试
 */
@ExtendWith(MockitoExtension.class)
public class TXEvalEngineLockTest {

    private TXEvalEngine txEvalEngine;
    private BlackListService blackListService;
    private TxRuleLoader txRuleLoader;
    private TransactionService transactionService;
    private NotifyService notifyService;
    private LockService lockService;

    @BeforeEach
    void setUp() {
        blackListService = mock(BlackListService.class);
        txRuleLoader = mock(TxRuleLoader.class);
        transactionService = mock(TransactionService.class);
        notifyService = mock(NotifyService.class);
        lockService = mock(LockService.class);

        txEvalEngine = new TXEvalEngine(blackListService, txRuleLoader, transactionService, notifyService, lockService);

        // 默认规则设置
        RuleDO amountRule = new RuleDO(); amountRule.setValue("1000");
        RuleDO blackRule = new RuleDO(); blackRule.setValue("true");
        RuleDO timeRule = new RuleDO(); timeRule.setValue("0 - 23");

        lenient().when(txRuleLoader.loadRule("amount")).thenReturn(amountRule);
        lenient().when(txRuleLoader.loadRule("blacklist")).thenReturn(blackRule);
        lenient().when(txRuleLoader.loadRule("off_hours")).thenReturn(timeRule);
    }

    @Test
    void testEvaluateTx_NormalFlow() {
        TransactionDO tx = buildTx("T123", "acct1", "2000");

        when(blackListService.isBlackListExist("acct1")).thenReturn(true);
        when(lockService.tryLock(eq("T123"))).thenReturn(true);
        doNothing().when(lockService).unlock("T123");

        List<String> result = txEvalEngine.evaluateTx(tx);

        assertFalse(result.isEmpty());
        verify(transactionService).createTransaction(tx);
        verify(notifyService).sendTransactionCheckNotify(eq(tx), anyList());
        verify(lockService).unlock("T123");
    }

    @Test
    void testEvaluateTx_ConcurrentLockPrevented() {
        TransactionDO tx = buildTx("T999", "acctX", "2000");

        when(lockService.tryLock(eq("T999"))).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            txEvalEngine.evaluateTx(tx);
        });

        assertTrue(ex.getMessage().contains("重复请求") || ex instanceof com.sct.bizmsg.exception.BusinessException);
    }

    @Test
    void testConcurrentLocking_OnlyOneThreadShouldPass() throws InterruptedException {
        TransactionDO tx = buildTx("T888", "acctY", "999");

        AtomicInteger attemptCount = new AtomicInteger(0);
        when(lockService.tryLock(eq("T888"))).thenAnswer(invocation -> attemptCount.incrementAndGet() == 1);
        doNothing().when(lockService).unlock("T888");

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    txEvalEngine.evaluateTx(tx);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdownNow();

        verify(lockService, times(threadCount)).tryLock(eq("T888"));
        verify(lockService, times(1)).unlock("T888");
        verify(transactionService, times(1)).createTransaction(any());
    }

    private TransactionDO buildTx(String id, String account, String amount) {
        TransactionDO tx = new TransactionDO();
        tx.setTransactionId(id);
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setTransactionTime(new Date());
        tx.setDescription("测试交易");
        return tx;
    }
}