package com.meghana.runbookrag.evaluation;

public record RetrievalEvaluationResult(
        String question,
        String expectedDocumentName,
        Integer expectedPageNumber,
        boolean hit,
        Integer firstRelevantRank
) {
}
