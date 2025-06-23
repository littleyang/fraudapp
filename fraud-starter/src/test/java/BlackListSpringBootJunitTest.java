import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.biz.service.impl.BlackListServiceImpl;
import com.fraud.repository.app.mapper.BlacklistDOMapper;
import com.fraud.repository.app.model.BlacklistDO;
import com.sct.commons.api.page.Page;
import com.sct.commons.api.page.Pages;
import com.sct.commons.api.result.vo.PageResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2025/6/20
 */

//@SpringBootTest(classes = FraudApplication.class)
@ExtendWith(MockitoExtension.class)
public class BlackListSpringBootJunitTest {

    private BlackListServiceImpl blackListService;

    private BlacklistDOMapper blacklistDOMapper;
    private RedisUtilService redisUtilService;

    @BeforeEach
    public void setUp() {
        blacklistDOMapper = mock(BlacklistDOMapper.class);
        redisUtilService = mock(RedisUtilService.class);
        blacklistDOMapper = mock(BlacklistDOMapper.class);
        redisUtilService = mock(RedisUtilService.class);
        blackListService = new BlackListServiceImpl(blacklistDOMapper, redisUtilService);
    }

    @Test
    public void testCreateBlackList() {
        BlacklistDO blacklistDO = new BlacklistDO();
        blacklistDO.setAccount("test_account");
        when(blacklistDOMapper.insertSelective(any())).thenReturn(1);

        boolean result = blackListService.createBlackList(blacklistDO);
        assertTrue(result);
        verify(redisUtilService).opsForSet("fraud:blacklist", "test_account");
    }

    @Test
    public void testDeleteBlackList() {
        BlacklistDO blacklistDO = new BlacklistDO();
        blacklistDO.setId(1L);
        blacklistDO.setAccount("test_account");

        when(blacklistDOMapper.selectByPrimaryKey(1L)).thenReturn(blacklistDO);
        when(blacklistDOMapper.deleteByPrimaryKey(1L)).thenReturn(1);
        boolean result = blackListService.deleteBlackList(blacklistDO.getId());
        assertTrue(result);
        verify(redisUtilService).opsForSetRemove("fraud:blacklist", "test_account");
    }

    @Test
    public void testIsBlackListExist() {
        when(redisUtilService.isMember("fraud:blacklist", "target_account")).thenReturn(true);
        boolean result = blackListService.isBlackListExist("target_account");
        assertTrue(result);
    }

    @Test
    public void testGetAllBlackListedAccount() {
        Set<String> expected = new HashSet<>(Collections.singletonList("acct1"));
        when(redisUtilService.membersString("fraud:blacklist")).thenReturn(expected);
        Set<String> actual = blackListService.getAllBlackListedAccount();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllBlacklists() {
        when(blacklistDOMapper.queryAllValidBlackList()).thenReturn(Collections.emptyList());
        List<BlacklistDO> result = blackListService.getAllBlacklists();
        assertNotNull(result);
    }

    @Test
    public void testUpdateBlackList() {
        BlacklistDO blacklistDO = new BlacklistDO();
        when(blacklistDOMapper.updateByPrimaryKeySelective(any())).thenReturn(1);

        boolean result = blackListService.updateBlackList(blacklistDO);

        assertTrue(result);
        verify(blacklistDOMapper).updateByPrimaryKeySelective(any());
        verifyNoInteractions(redisUtilService);
    }

    @Test
    public void testGetBlacklistsByPage() {
        Page page = Pages.firstPage();
        BlacklistDO blacklistDO = new BlacklistDO();
        List<BlacklistDO> data = Collections.singletonList(blacklistDO);

        when(blacklistDOMapper.queryAllValidBlackListByAccountLike(eq("acc"), anyInt(), anyInt())).thenReturn(data);
        when(blacklistDOMapper.countValidBlackListByAccountLike("acc")).thenReturn(1);

        PageResultVO<BlacklistDO> result = blackListService.getBlacklistsByPage("acc", page);

        assertNotNull(result);
        verify(blacklistDOMapper).queryAllValidBlackListByAccountLike(eq("acc"), anyInt(), anyInt());
        verify(blacklistDOMapper).countValidBlackListByAccountLike("acc");
    }

    @Test
    public void testPreload() {
        BlacklistDO b1 = new BlacklistDO();
        b1.setAccount("a1");
        BlacklistDO b2 = new BlacklistDO();
        b2.setAccount("a2");
        when(blacklistDOMapper.queryAllValidBlackList()).thenReturn(List.of(b1, b2));

        blackListService.preload();

        verify(redisUtilService).opsForSet("fraud:blacklist", "a1");
        verify(redisUtilService).opsForSet("fraud:blacklist", "a2");
    }

}
