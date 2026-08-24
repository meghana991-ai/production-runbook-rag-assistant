package com.meghana.runbookrag.adapters;

import com.meghana.runbookrag.core.AnswerGenerator;
import com.meghana.runbookrag.core.RetrievedChunk;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContextOnlyAnswerGenerator implements AnswerGenerator {
    @Override
    public String generate(String question, List<RetrievedChunk> context) {
        return context.get(0).content();
    }
}
