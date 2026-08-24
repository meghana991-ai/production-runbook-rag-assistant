package com.meghana.runbookrag.adapters;

import com.meghana.runbookrag.core.RetrievedChunk;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiAnswerGeneratorTest {
    @Test
    void labelsContextSoModelCanProduceMatchingCitations() {
        String input = OpenAiAnswerGenerator.buildInput("How do I restart checkout?",
                List.of(new RetrievedChunk("checkout.pdf", 4,
                        "Run kubectl rollout restart.", 0.92)));

        assertThat(input).contains("Question:\nHow do I restart checkout?")
                .contains("[1] Source: checkout.pdf, page 4")
                .contains("Run kubectl rollout restart.");
    }
}
