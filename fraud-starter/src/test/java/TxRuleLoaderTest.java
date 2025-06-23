import com.fraud.biz.service.RuleService;
import com.fraud.biz.txengine.TxRuleLoader;
import com.fraud.repository.app.model.RuleDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TxRuleLoaderTest {

    private RuleService ruleService;
    private TxRuleLoader txRuleLoader;

    @BeforeEach
    public void setUp() {
        ruleService = mock(RuleService.class);
        txRuleLoader = new TxRuleLoader(ruleService);
    }

    @Test
    public void testLoadRuleSuccess() {
        RuleDO rule = new RuleDO();
        rule.setRuleKey("AMOUNT");
        when(ruleService.getAllRules()).thenReturn(List.of(rule));

        RuleDO result = txRuleLoader.loadRule("AMOUNT");
        assertNotNull(result);
        assertEquals("AMOUNT", result.getRuleKey());
    }

    @Test
    public void testLoadRuleNotFound() {
        when(ruleService.getAllRules()).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            txRuleLoader.loadRule("UNKNOWN");
        });
        assertTrue(exception.getMessage().contains("未成查询到监控规则"));
    }
}