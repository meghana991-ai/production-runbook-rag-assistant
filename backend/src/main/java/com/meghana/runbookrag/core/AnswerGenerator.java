package com.meghana.runbookrag.core;

import java.util.List;

public interface AnswerGenerator {
    String generate(String question, List<RetrievedChunk> context);
}
