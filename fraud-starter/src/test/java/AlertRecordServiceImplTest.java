import com.fraud.biz.service.impl.AlertRecordServiceImpl;
import com.fraud.repository.app.mapper.AlertRecordDOMapper;
import com.fraud.repository.app.model.AlertRecordDO;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.page.Pages;
import com.sct.commons.api.result.vo.PageResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertRecordServiceImplTest {

    private AlertRecordDOMapper alertRecordDOMapper;
    private AlertRecordServiceImpl alertRecordService;

    @BeforeEach
    void setUp() {
        alertRecordDOMapper = mock(AlertRecordDOMapper.class);
        alertRecordService = new AlertRecordServiceImpl(alertRecordDOMapper);
    }

    @Test
    void testCreateAlertRecord() {
        AlertRecordDO record = new AlertRecordDO();
        record.setType("SMS");
        record.setContent("Alert content");
        record.setDestination("admin@example.com");

        when(alertRecordDOMapper.insertSelective(any())).thenReturn(1);

        boolean result = alertRecordService.createAlertRecord(record);
        assertTrue(result);
    }

    @Test
    void testFindAlertRecordById() {
        AlertRecordDO expected = new AlertRecordDO();
        expected.setId(1L);
        when(alertRecordDOMapper.selectByPrimaryKey(1L)).thenReturn(expected);

        AlertRecordDO actual = alertRecordService.findAlertRecordById(1L);
        assertEquals(expected, actual);
    }

    @Test
    void testFindAlertRecordByPage() {
        String user = "test_user";
        Page page = Pages.page(1, 10);

        when(alertRecordDOMapper.selectAlertDOByUser(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(alertRecordDOMapper.countAlertDOByUser(anyString()))
                .thenReturn(0);

        PageResultVO<AlertRecordDO> result = alertRecordService.findAlertRecordByPage(user, page);
        assertNotNull(result);
        assertEquals(0, result.getPage().getTotalItems());
    }
}
