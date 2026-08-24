package com.meghana.runbookrag.api;

import com.meghana.runbookrag.core.RagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
@Import(QuestionControllerTest.StubConfiguration.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RagService ragService;

    @TestConfiguration
    static class StubConfiguration {
        @Bean
        RagService ragService() {
            return new RagService((question, limit) -> List.of(
                    new com.meghana.runbookrag.core.RetrievedChunk(
                            "checkout-runbook.pdf", 3, "Restart the deployment.", 0.9)),
                    (question, context) -> "Restart the deployment.");
        }
    }

    @Test
    void rejectsBlankQuestion() throws Exception {
        mockMvc.perform(post("/api/v1/questions")
                        .contentType("application/json")
                        .content("{\"question\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsGroundedAnswer() throws Exception {
        mockMvc.perform(post("/api/v1/questions")
                        .contentType("application/json")
                        .content("{\"question\":\"How do I restart checkout?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grounded").value(true))
                .andExpect(jsonPath("$.citations[0].pageNumber").value(3));
    }
}
