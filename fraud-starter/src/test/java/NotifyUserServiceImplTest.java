import com.fraud.biz.service.impl.NotifyUserServiceImpl;
import com.fraud.repository.app.mapper.NotifyUserDOMapper;
import com.fraud.repository.app.model.NotifyUserDO;
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
public class NotifyUserServiceImplTest {

    private NotifyUserDOMapper notifyUserDOMapper;
    private NotifyUserServiceImpl notifyUserService;

    @BeforeEach
    public void setUp() {
        notifyUserDOMapper = mock(NotifyUserDOMapper.class);
        notifyUserService = new NotifyUserServiceImpl(notifyUserDOMapper);
    }

    @Test
    public void testCreateNotifyUser() {
        NotifyUserDO notifyUserDO = new NotifyUserDO();
        when(notifyUserDOMapper.insertSelective(notifyUserDO)).thenReturn(1);

        boolean result = notifyUserService.createNotifyUser(notifyUserDO);
        assertTrue(result);
    }

    @Test
    public void testUpdateNotifyUser() {
        NotifyUserDO notifyUserDO = new NotifyUserDO();
        when(notifyUserDOMapper.updateByPrimaryKeySelective(notifyUserDO)).thenReturn(1);

        boolean result = notifyUserService.updateNotifyUser(notifyUserDO);
        assertTrue(result);
    }

    @Test
    public void testDeleteNotifyUser() {
        NotifyUserDO notifyUserDO = new NotifyUserDO();
        notifyUserDO.setId(1L);
        when(notifyUserDOMapper.deleteByPrimaryKey(1L)).thenReturn(1);

        boolean result = notifyUserService.deleteNotifyUser(notifyUserDO.getId());
        assertTrue(result);
    }

    @Test
    public void testSelectAllNotifyUser() {
        List<NotifyUserDO> mockList = Collections.singletonList(new NotifyUserDO());
        when(notifyUserDOMapper.selectAllNotifyUser()).thenReturn(mockList);

        List<NotifyUserDO> result = notifyUserService.selectAllNotifyUser();
        assertEquals(1, result.size());
    }

    @Test
    public void testSelectNotifyUserPage() {
        String user = "test";
        Page page = Pages.page(1, 10);
        when(notifyUserDOMapper.selectNotifyUserAccount(user, 0, 10))
                .thenReturn(Collections.singletonList(new NotifyUserDO()));
        when(notifyUserDOMapper.countNotifyUserAccount(user)).thenReturn(1);

        PageResultVO<NotifyUserDO> result = notifyUserService.selectNotifyUser(user, page);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getPage().getTotalItems());
    }
}