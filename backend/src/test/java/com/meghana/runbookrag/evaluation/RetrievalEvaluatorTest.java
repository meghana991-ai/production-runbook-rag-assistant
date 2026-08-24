package com.meghana.runbookrag.evaluation;

import com.meghana.runbookrag.core.RetrievedChunk;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RetrievalEvaluatorTest {

    @Test
    void calculatesHitRateAndMeanReciprocalRank() {
        RetrievalEvaluator evaluator = new RetrievalEvaluator((question, limit) -> switch (question) {
            case "restart" -> List.of(chunk("other.pdf", 1), chunk("checkout.pdf", 4));
            case "rollback" -> List.of(chunk("payments.pdf", 2));
            default -> List.of();
        });
        RetrievalEvaluationRequest request = new RetrievalEvaluationRequest(List.of(
                new RetrievalEvaluationCase("restart", "checkout.pdf", 4),
                new RetrievalEvaluationCase("rollback", "payments.pdf", null),
                new RetrievalEvaluationCase("scale", "scaling.pdf", 3)
        ), 5);

        RetrievalEvaluationReport report = evaluator.evaluate(request);

        assertThat(report.evaluatedQueries()).isEqualTo(3);
        assertThat(report.hitRateAtK()).isEqualTo(2.0 / 3.0);
        assertThat(report.meanReciprocalRank()).isEqualTo(0.5);
        assertThat(report.results()).extracting(RetrievalEvaluationResult::firstRelevantRank)
                .containsExactly(2, 1, null);
    }

    @Test
    void pageLabelPreventsWrongPageFromCountingAsRelevant() {
        RetrievalEvaluator evaluator = new RetrievalEvaluator((question, limit) ->
                List.of(chunk("checkout.pdf", 2)));
        RetrievalEvaluationRequest request = new RetrievalEvaluationRequest(List.of(
                new RetrievalEvaluationCase("restart", "checkout.pdf", 4)), 1);

        assertThat(evaluator.evaluate(request).hitRateAtK()).isZero();
    }

    private RetrievedChunk chunk(String documentName, int page) {
        return new RetrievedChunk(documentName, page, "content", 0.8);
    }
}
