package com.meghana.runbookrag.evaluation;

import com.meghana.runbookrag.core.RetrievedChunk;
import com.meghana.runbookrag.core.Retriever;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RetrievalEvaluator {

    private final Retriever retriever;

    public RetrievalEvaluator(Retriever retriever) {
        this.retriever = retriever;
    }

    public RetrievalEvaluationReport evaluate(RetrievalEvaluationRequest request) {
        List<RetrievalEvaluationResult> results = new ArrayList<>();
        double reciprocalRankSum = 0;
        int hits = 0;

        for (RetrievalEvaluationCase evaluationCase : request.cases()) {
            List<RetrievedChunk> retrieved = retriever.retrieve(evaluationCase.question(), request.topK());
            Integer rank = firstRelevantRank(retrieved, evaluationCase);
            if (rank != null) {
                hits++;
                reciprocalRankSum += 1.0 / rank;
            }
            results.add(new RetrievalEvaluationResult(
                    evaluationCase.question(), evaluationCase.expectedDocumentName(),
                    evaluationCase.expectedPageNumber(), rank != null, rank));
        }

        int queryCount = request.cases().size();
        return new RetrievalEvaluationReport(queryCount, request.topK(),
                hits / (double) queryCount, reciprocalRankSum / queryCount, List.copyOf(results));
    }

    private Integer firstRelevantRank(List<RetrievedChunk> chunks, RetrievalEvaluationCase evaluationCase) {
        for (int index = 0; index < chunks.size(); index++) {
            RetrievedChunk chunk = chunks.get(index);
            boolean documentMatches = chunk.documentName().equals(evaluationCase.expectedDocumentName());
            boolean pageMatches = evaluationCase.expectedPageNumber() == null
                    || chunk.pageNumber() == evaluationCase.expectedPageNumber();
            if (documentMatches && pageMatches) {
                return index + 1;
            }
        }
        return null;
    }
}
