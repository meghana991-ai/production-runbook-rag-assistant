package com.meghana.runbookrag.adapters;

import com.meghana.runbookrag.core.AnswerGenerator;
import com.meghana.runbookrag.core.RetrievedChunk;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "rag.answer.provider", havingValue = "local", matchIfMissing = true)
public class ContextOnlyAnswerGenerator implements AnswerGenerator {
    @Override
    public String generate(String question, List<RetrievedChunk> context) {
        StringBuilder answer = new StringBuilder("Based on the indexed runbooks:\n");
        for (int index = 0; index < context.size(); index++) {
            RetrievedChunk chunk = context.get(index);
            answer.append("\n[").append(index + 1).append("] ")
                    .append(chunk.content().trim());
        }
        return answer.toString();
    }
}
