package com.meghana.runbookrag.core;

import com.meghana.runbookrag.api.Citation;
import com.meghana.runbookrag.api.QuestionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private static final int RETRIEVAL_LIMIT = 5;
    private static final String NO_EVIDENCE =
            "I could not find enough evidence in the indexed runbooks to answer that question.";

    private final Retriever retriever;
    private final AnswerGenerator answerGenerator;

    public RagService(Retriever retriever, AnswerGenerator answerGenerator) {
        this.retriever = retriever;
        this.answerGenerator = answerGenerator;
    }

    public QuestionResponse answer(String question) {
        List<RetrievedChunk> chunks = retriever.retrieve(question, RETRIEVAL_LIMIT);
        if (chunks.isEmpty()) {
            return new QuestionResponse(NO_EVIDENCE, List.of(), false);
        }

        List<Citation> citations = chunks.stream()
                .map(chunk -> new Citation(chunk.documentName(), chunk.pageNumber(), chunk.content()))
                .toList();

        return new QuestionResponse(answerGenerator.generate(question, chunks), citations, true);
    }
}
