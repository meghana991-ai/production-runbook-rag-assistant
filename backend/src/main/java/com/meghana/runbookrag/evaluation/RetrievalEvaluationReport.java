package com.meghana.runbookrag.evaluation;

import java.util.List;

public record RetrievalEvaluationReport(
        int evaluatedQueries,
        int topK,
        double hitRateAtK,
        double meanReciprocalRank,
        List<RetrievalEvaluationResult> results
) {
}
