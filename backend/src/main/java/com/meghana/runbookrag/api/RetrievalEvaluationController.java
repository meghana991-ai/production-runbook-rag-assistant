package com.meghana.runbookrag.api;

import com.meghana.runbookrag.evaluation.RetrievalEvaluationReport;
import com.meghana.runbookrag.evaluation.RetrievalEvaluationRequest;
import com.meghana.runbookrag.evaluation.RetrievalEvaluator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluations/retrieval")
public class RetrievalEvaluationController {

    private final RetrievalEvaluator evaluator;

    public RetrievalEvaluationController(RetrievalEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @PostMapping
    public ResponseEntity<RetrievalEvaluationReport> evaluate(
            @Valid @RequestBody RetrievalEvaluationRequest request) {
        return ResponseEntity.ok(evaluator.evaluate(request));
    }
}
