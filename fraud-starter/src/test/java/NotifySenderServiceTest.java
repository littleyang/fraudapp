import com.fraud.biz.notify.AlertRecordMessageDTO;
import com.fraud.biz.notify.NotifySenderService;
import com.fraud.biz.notify.sender.EmailSender;
import com.fraud.biz.notify.sender.PhoneSender;
import com.fraud.biz.notify.sender.SMSSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * 正确调用 emailSender, smsSender, phoneSender。
 * ✅ 不支持的类型不触发任何发送器。
 * ✅ 传入 null 时不抛异常。
 * ✅ 使用 Mockito.verify 严格校验调用次数和参数。
 */
@ExtendWith(MockitoExtension.class)
public class NotifySenderServiceTest {

    private NotifySenderService notifySenderService;
    private EmailSender emailSender;
    private PhoneSender phoneSender;
    private SMSSender smsSender;

    @BeforeEach
    void setUp() {
        emailSender = mock(EmailSender.class);
        phoneSender = mock(PhoneSender.class);
        smsSender = mock(SMSSender.class);

        notifySenderService = new NotifySenderService();
        // 注入 mocks
        injectMocks();
    }

    private void injectMocks() {
        try {
            java.lang.reflect.Field emailField = NotifySenderService.class.getDeclaredField("emailSender");
            emailField.setAccessible(true);
            emailField.set(notifySenderService, emailSender);

            java.lang.reflect.Field phoneField = NotifySenderService.class.getDeclaredField("phoneSender");
            phoneField.setAccessible(true);
            phoneField.set(notifySenderService, phoneSender);

            java.lang.reflect.Field smsField = NotifySenderService.class.getDeclaredField("smsSender");
            smsField.setAccessible(true);
            smsField.set(notifySenderService, smsSender);
        } catch (Exception e) {
            throw new RuntimeException("Mock 注入失败", e);
        }
    }

    @Test
    public void testSendEmailNotify() {
        AlertRecordMessageDTO dto = new AlertRecordMessageDTO();
        dto.setType("email");
        dto.setDestination("test@example.com");
        dto.setContent("Test Email");

        notifySenderService.sendNotifyRecord(dto);

        verify(emailSender, times(1)).sendToEmail("test@example.com", "Test Email");
        verifyNoInteractions(smsSender, phoneSender);
    }

    @Test
    public void testSendSmsNotify() {
        AlertRecordMessageDTO dto = new AlertRecordMessageDTO();
        dto.setType("sms");
        dto.setDestination("123456789");
        dto.setContent("Test SMS");

        notifySenderService.sendNotifyRecord(dto);

        verify(smsSender, times(1)).sendSms("123456789", "Test SMS");
        verifyNoInteractions(emailSender, phoneSender);
    }

    @Test
    public void testSendPhoneNotify() {
        AlertRecordMessageDTO dto = new AlertRecordMessageDTO();
        dto.setType("phone");
        dto.setDestination("987654321");
        dto.setContent("Test Call");

        notifySenderService.sendNotifyRecord(dto);

        verify(phoneSender, times(1)).callPhone("987654321", "Test Call");
        verifyNoInteractions(emailSender, smsSender);
    }

    @Test
    public void testSendInvalidType() {
        AlertRecordMessageDTO dto = new AlertRecordMessageDTO();
        dto.setType("weixin"); // unsupported type
        dto.setDestination("unknown");
        dto.setContent("Test Unknown");

        notifySenderService.sendNotifyRecord(dto);

        verifyNoInteractions(emailSender, smsSender, phoneSender);
    }

    @Test
    public void testSendNullDto() {
        notifySenderService.sendNotifyRecord(null);
        verifyNoInteractions(emailSender, smsSender, phoneSender);
    }
}