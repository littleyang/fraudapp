import com.fraud.biz.notify.AlertRecordMessageDTO;
import com.fraud.biz.notify.impl.NotifyServiceImpl;
import com.fraud.biz.notify.rocketmq.AlertRecordMessageSendProducer;
import com.fraud.biz.service.NotifyUserService;
import com.fraud.repository.app.model.NotifyUserDO;
import com.fraud.repository.app.model.TransactionDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotifyServiceImplTest {

    @Mock
    private NotifyUserService notifyUserService;

    @Mock
    private AlertRecordMessageSendProducer alertRecordMessageSendProducer;

    @InjectMocks
    private NotifyServiceImpl notifyService;

    private TransactionDO mockTransaction;

    @BeforeEach
    void setUp() {
        mockTransaction = new TransactionDO();
        mockTransaction.setTransactionId("TX123");
        mockTransaction.setAmount("1234.56");
        mockTransaction.setAccount("acct001");
        mockTransaction.setGmtCreator("sys");
        mockTransaction.setGmtUpdater("sys");
    }

    @Test
    void testSendTransactionCheckNotify_Success() {
        NotifyUserDO userDO = new NotifyUserDO();
        userDO.setUser("admin");
        userDO.setDestination("admin@example.com");
        userDO.setType("EMAIL");

        when(notifyUserService.selectAllNotifyUser()).thenReturn(List.of(userDO));

        boolean result = notifyService.sendTransactionCheckNotify(mockTransaction, List.of("Rule triggered"));

        assertTrue(result);
        ArgumentCaptor<List<AlertRecordMessageDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(alertRecordMessageSendProducer).send(captor.capture());

        List<AlertRecordMessageDTO> messages = captor.getValue();
        assertEquals(1, messages.size());
        assertEquals("admin", messages.get(0).getNotifyUser());
    }

    @Test
    void testSendTransactionCheckNotify_EmptyUserList() {
        when(notifyUserService.selectAllNotifyUser()).thenReturn(List.of());

        boolean result = notifyService.sendTransactionCheckNotify(mockTransaction, List.of("Rule triggered"));

        assertTrue(result);
        verify(alertRecordMessageSendProducer, never()).send(any());
    }

    @Test
    void testSendTransactionCheckNotify_ExceptionHandling() {
        when(notifyUserService.selectAllNotifyUser()).thenThrow(new RuntimeException("DB error"));

        boolean result = notifyService.sendTransactionCheckNotify(mockTransaction, List.of("Rule triggered"));

        assertFalse(result);
        verify(alertRecordMessageSendProducer, never()).send(any());
    }
}
