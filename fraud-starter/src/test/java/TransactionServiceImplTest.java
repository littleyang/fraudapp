
import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.biz.service.impl.TransactionServiceImpl;
import com.fraud.repository.app.mapper.AlertRecordDOMapper;
import com.fraud.repository.app.mapper.TransactionDOMapper;
import com.fraud.repository.app.model.AlertRecordDO;
import com.fraud.repository.app.model.TransactionDO;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.page.Pages;
import com.sct.commons.api.result.vo.PageResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    private TransactionDOMapper transactionDOMapper;
    private AlertRecordDOMapper alertRecordDOMapper;
    private TransactionServiceImpl transactionService;
    private RedisUtilService redisUtilService;

    @BeforeEach
    public void setUp() {
        transactionDOMapper = mock(TransactionDOMapper.class);
        alertRecordDOMapper = mock(AlertRecordDOMapper.class);
        redisUtilService = mock(RedisUtilService.class);
        transactionService = new TransactionServiceImpl(transactionDOMapper, alertRecordDOMapper,redisUtilService);
    }

    @Test
    public void testCreateTransaction() {
        TransactionDO tx = new TransactionDO();
        when(transactionDOMapper.insertSelective(tx)).thenReturn(1);
        assertTrue(transactionService.createTransaction(tx));
    }

    @Test
    public void testDeleteTransaction() {
        when(transactionDOMapper.deleteByTransactionID("tx123")).thenReturn(1);
        assertTrue(transactionService.deleteTransaction("tx123"));
    }

    @Test
    public void testDeleteTransactionById() {
        when(transactionDOMapper.deleteByPrimaryKey(1L)).thenReturn(1);
        assertTrue(transactionService.deleteTransactionById("1"));
    }

    @Test
    public void testGetTransactionByTxId() {
        TransactionDO tx = new TransactionDO();
        when(transactionDOMapper.selectByTxID("tx123")).thenReturn(tx);
        assertEquals(tx, transactionService.getTransactionByTxId("tx123"));
    }

    @Test
    public void testGetTransactionById() {
        TransactionDO tx = new TransactionDO();
        when(transactionDOMapper.selectByPrimaryKey(1L)).thenReturn(tx);
        assertEquals(tx, transactionService.getTransactionById(1));
    }

    @Test
    public void testQueryTransactionAlert() {
        List<AlertRecordDO> alerts = Collections.singletonList(new AlertRecordDO());
        when(alertRecordDOMapper.selectAlertDOByTxID("tx123")).thenReturn(alerts);
        assertEquals(1, transactionService.queryTransactionAlert("tx123").size());
    }

    @Test
    public void testQueryTransaction() {
        TransactionDO tx = new TransactionDO();
        Page page = Pages.page(1, 10);

        when(transactionDOMapper.selectByTransaction(null,null, 0, 10)).thenReturn(Collections.singletonList(tx));
        when(transactionDOMapper.countByTransaction(null,null)).thenReturn(1);

        PageResultVO<TransactionDO> result = transactionService.queryTransaction(tx, page);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getPage().getTotalItems());
    }
}