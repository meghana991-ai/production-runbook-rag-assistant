package com.meghana.runbookrag.api;

import com.meghana.runbookrag.core.RagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RagService ragService;

    @Test
    void rejectsBlankQuestion() throws Exception {
        mockMvc.perform(post("/api/v1/questions")
                        .contentType("application/json")
                        .content("{\"question\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsGroundedAnswer() throws Exception {
        when(ragService.answer(anyString())).thenReturn(new QuestionResponse(
                "Restart the deployment.",
                List.of(new Citation("checkout-runbook.pdf", 3, "Restart the deployment.")),
                true));

        mockMvc.perform(post("/api/v1/questions")
                        .contentType("application/json")
                        .content("{\"question\":\"How do I restart checkout?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grounded").value(true))
                .andExpect(jsonPath("$.citations[0].pageNumber").value(3));
    }
}
