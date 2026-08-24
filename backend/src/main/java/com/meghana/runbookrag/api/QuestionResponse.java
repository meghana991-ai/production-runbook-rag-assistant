package com.meghana.runbookrag.api;

import java.util.List;

public record QuestionResponse(String answer, List<Citation> citations, boolean grounded) {
}
