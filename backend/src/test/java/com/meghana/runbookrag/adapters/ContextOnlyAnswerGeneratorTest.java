package com.meghana.runbookrag.adapters;

import com.meghana.runbookrag.core.RetrievedChunk;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContextOnlyAnswerGeneratorTest {
    @Test
    void returnsAllRetrievedEvidenceWithCitationMarkers() {
        List<RetrievedChunk> context = List.of(
                new RetrievedChunk("runbook.pdf", 2, "Check pod health.", 0.9),
                new RetrievedChunk("runbook.pdf", 3, "Restart only unhealthy pods.", 0.8));

        String answer = new ContextOnlyAnswerGenerator().generate("How do I recover?", context);

        assertThat(answer).contains("[1] Check pod health.", "[2] Restart only unhealthy pods.");
    }
}
