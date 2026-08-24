package com.meghana.runbookrag.api;

import com.meghana.runbookrag.core.RagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final RagService ragService;

    public QuestionController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> ask(@Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(ragService.answer(request.question()));
    }
}
