package com.meghana.runbookrag.core;

import com.meghana.runbookrag.api.QuestionResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RagServiceTest {

    @Test
    void refusesToInventAnAnswerWhenRetrievalReturnsNoEvidence() {
        RagService service = new RagService((question, limit) -> List.of(), (question, context) -> "unused");

        QuestionResponse response = service.answer("How do I restart checkout-api?");

        assertThat(response.grounded()).isFalse();
        assertThat(response.citations()).isEmpty();
        assertThat(response.answer()).contains("could not find enough evidence");
    }

    @Test
    void returnsGeneratedAnswerWithSourceCitation() {
        RetrievedChunk chunk = new RetrievedChunk("checkout-runbook.pdf", 3, "Restart the deployment.", 0.91);
        RagService service = new RagService((question, limit) -> List.of(chunk),
                (question, context) -> "Restart the checkout deployment.");

        QuestionResponse response = service.answer("How do I restart checkout-api?");

        assertThat(response.grounded()).isTrue();
        assertThat(response.answer()).isEqualTo("Restart the checkout deployment.");
        assertThat(response.citations()).singleElement().satisfies(citation -> {
            assertThat(citation.documentName()).isEqualTo("checkout-runbook.pdf");
            assertThat(citation.pageNumber()).isEqualTo(3);
        });
    }
}
