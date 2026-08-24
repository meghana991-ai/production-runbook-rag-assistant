package com.meghana.runbookrag.evaluation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RetrievalEvaluationCase(
        @NotBlank String question,
        @NotBlank String expectedDocumentName,
        @Positive Integer expectedPageNumber
) {
}
