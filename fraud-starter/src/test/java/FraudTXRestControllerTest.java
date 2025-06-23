
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.FraudApplication;
import com.fraud.biz.txengine.TXEvalEngine;
import com.fraud.web.platform.form.TransactionForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FraudApplication.class)
@AutoConfigureMockMvc
class FraudTXRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TXEvalEngine txEvalEngine;

    @Test
    void testCheckTxFraudAPI() throws Exception {
        TransactionForm form = new TransactionForm();
        form.setTransactionId("tx123");
        form.setAccount("acc001");
        form.setAmount("500000");
        form.setTransactionTime(new Date());

        List<String> fakeReasons = List.of("Amount exceeds limit");
        when(txEvalEngine.evaluateTx(any())).thenReturn(fakeReasons);

        mockMvc.perform(post("/tx/evaluate/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.checkResult").value(true));
    }

    @Test
    void testCheckTx_Success() throws Exception {
        String json = """
        {
             "transactionId": "22864539stdds",
             "account": "ew1474",
             "amount": "45659666",
             "transactionTime": 1750477432000,
             "description": "交易信454554息"
        }
        """;

        mockMvc.perform(post("/tx/evaluate/check").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
                .andExpect( MockMvcResultMatchers.jsonPath("$.data.checkResult").isBoolean());
    }
}
