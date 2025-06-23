
import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.biz.service.impl.LockService;
import com.fraud.biz.service.impl.LockServiceTestHelper;
import com.fraud.commons.enums.FraudRestResultCode;
import com.sct.bizmsg.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {

    private RedisUtilService redisUtilService;
    private LockService lockService;


    @BeforeEach
    void setUp() {
        redisUtilService = mock(RedisUtilService.class);
        lockService = new LockService(redisUtilService);
    }


    @Test
    void testTryLock_FailureDueToExistingKey() {
        String TX_ID = "tx123" + new Random().nextInt(10000);
        String LOCK_KEY = "tx:lock:" + TX_ID;
        when(redisUtilService.setIfAbsentAndTime(eq(LOCK_KEY), anyString(), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(false);

        boolean result = lockService.tryLock(TX_ID);
        assertFalse(result);
    }

    @Test
    void testTryLock_ThrowsBusinessExceptionOnRedisError() {
        String TX_ID = "tx123" + new Random().nextInt(10000);
        String LOCK_KEY = "tx:lock:" + TX_ID;
        when(redisUtilService.setIfAbsentAndTime(anyString(), anyString(), anyLong(), any()))
                .thenThrow(new RuntimeException("Redis connection error"));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            lockService.tryLock(TX_ID);
        });

        assertEquals("锁定交易失败", ex.getAlertMessage());
        assertEquals(FraudRestResultCode.ERROR.getCode(), ex.getCode());
    }

    @Test
    void testUnlock_Success() {
        String transactionId = "tx123";
        String lockKey = "tx:lock:" + transactionId;
        String lockId = "mock-lock-id";

        LockServiceTestHelper.setThreadLocalValue(lockService, lockId);
        when(redisUtilService.get(lockKey)).thenReturn(lockId);
        lenient().when(redisUtilService.deleteValue(lockKey)).thenReturn(true);

        lockService.unlock(transactionId);
    }

}
