package com.meghana.runbookrag.embedding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@Primary
@ConditionalOnProperty(name = "rag.embedding.provider", havingValue = "openai")
public class OpenAiEmbeddingClient implements EmbeddingClient {

    private final RestClient client;
    private final String model;
    private final int dimensions;

    public OpenAiEmbeddingClient(
            @Value("${OPENAI_API_KEY}") String apiKey,
            @Value("${rag.embedding.model:text-embedding-3-small}") String model,
            @Value("${rag.embedding.dimensions:256}") int dimensions
    ) {
        this.client = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.model = model;
        this.dimensions = dimensions;
    }

    @Override
    public List<List<Double>> embed(List<String> inputs) {
        EmbeddingResponse response = client.post()
                .uri("/embeddings")
                .body(Map.of("model", model, "input", inputs, "dimensions", dimensions))
                .retrieve()
                .body(EmbeddingResponse.class);
        if (response == null || response.data() == null || response.data().size() != inputs.size()) {
            throw new IllegalStateException("Embedding API returned an unexpected response");
        }
        return response.data().stream().map(EmbeddingData::embedding).toList();
    }

    record EmbeddingResponse(List<EmbeddingData> data) {}
    record EmbeddingData(List<Double> embedding, int index) {}
}
