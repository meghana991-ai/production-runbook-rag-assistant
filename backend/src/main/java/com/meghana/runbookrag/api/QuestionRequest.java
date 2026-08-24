package com.meghana.runbookrag.api;

import jakarta.validation.constraints.NotBlank;

public record QuestionRequest(@NotBlank(message = "question is required") String question) {
}
