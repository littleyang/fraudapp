
import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.biz.service.impl.RuleServiceImpl;
import com.fraud.commons.enums.RuleTypeEnum;
import com.fraud.repository.app.mapper.RuleDOMapper;
import com.fraud.repository.app.model.RuleDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceImplTest {

    private RuleDOMapper ruleDOMapper;
    private RedisUtilService redisUtilService;
    private RuleServiceImpl ruleService;

    @BeforeEach
    public void setUp() {
        ruleDOMapper = mock(RuleDOMapper.class);
        redisUtilService = mock(RedisUtilService.class);
        ruleService = new RuleServiceImpl(ruleDOMapper, redisUtilService);
    }

    @Test
    public void testCreateFraudRule() {
        when(ruleDOMapper.selectAllRule()).thenReturn(Collections.singletonList(new RuleDO()));
        boolean result = ruleService.createFraudRule("1000", "22:00-06:00", true);
        assertTrue(result);
        verify(ruleDOMapper, times(3)).insertSelective(any(RuleDO.class));
        verify(redisUtilService, atLeastOnce()).opsForSetObject(anyString(), any());
    }

    @Test
    public void testUpdateRule() {
        when(ruleDOMapper.selectAllRule()).thenReturn(Collections.singletonList(new RuleDO()));
        boolean result = ruleService.updateRule("2000", "23:00-07:00", false);
        assertTrue(result);
        verify(ruleDOMapper).removeCurrentRule();
        verify(redisUtilService).deleteCache("fraud:rule");
        verify(ruleDOMapper, times(3)).insertSelective(any(RuleDO.class));

    }

    @Test
    public void testGetAllRulesFromRedis() {
        Set<Object> redisSet = new HashSet<>();
        RuleDO rule = new RuleDO();
        redisSet.add(rule);
        when(redisUtilService.members(anyString())).thenReturn(redisSet);
        List<RuleDO> rules = ruleService.getAllRules();
        assertEquals(1, rules.size());
    }

    @Test
    public void testGetAllRulesFromDatabaseFallback() {
        when(redisUtilService.members(anyString())).thenReturn(Collections.emptySet());
        when(ruleDOMapper.selectAllRule()).thenReturn(Collections.singletonList(new RuleDO()));
        List<RuleDO> rules = ruleService.getAllRules();
        assertEquals(1, rules.size());
    }
}