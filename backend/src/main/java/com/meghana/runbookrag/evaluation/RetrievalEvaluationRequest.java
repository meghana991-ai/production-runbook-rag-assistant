package com.meghana.runbookrag.evaluation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RetrievalEvaluationRequest(
        @NotEmpty List<@Valid RetrievalEvaluationCase> cases,
        @Min(1) @Max(20) int topK
) {
}
