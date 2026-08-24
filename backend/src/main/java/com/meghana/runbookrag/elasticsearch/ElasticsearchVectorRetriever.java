package com.meghana.runbookrag.elasticsearch;

import com.meghana.runbookrag.core.RetrievedChunk;
import com.meghana.runbookrag.core.Retriever;
import com.meghana.runbookrag.embedding.EmbeddingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Primary
@Profile("elasticsearch")
public class ElasticsearchVectorRetriever implements Retriever {

    private final RestClient client;
    private final EmbeddingClient embeddingClient;
    private final String index;

    public ElasticsearchVectorRetriever(
            RestClient elasticsearchRestClient,
            EmbeddingClient embeddingClient,
            @Value("${rag.elasticsearch.index:runbook-chunks}") String index
    ) {
        this.client = elasticsearchRestClient;
        this.embeddingClient = embeddingClient;
        this.index = index;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RetrievedChunk> retrieve(String question, int limit) {
        List<Double> vector = embeddingClient.embed(List.of(question)).get(0);
        Map<String, Object> response = client.post()
                .uri("/{index}/_search", index)
                .body(Map.of(
                        "knn", Map.of(
                                "field", "embedding",
                                "query_vector", vector,
                                "k", limit,
                                "num_candidates", Math.max(20, limit * 10)),
                        "_source", List.of("documentName", "pageNumber", "content")))
                .retrieve()
                .body(Map.class);

        if (response == null) return List.of();
        Map<String, Object> hitsContainer = (Map<String, Object>) response.get("hits");
        if (hitsContainer == null) return List.of();
        List<Map<String, Object>> hits = (List<Map<String, Object>>) hitsContainer.getOrDefault("hits", List.of());
        List<RetrievedChunk> results = new ArrayList<>();
        for (Map<String, Object> hit : hits) {
            Map<String, Object> source = (Map<String, Object>) hit.get("_source");
            if (source == null) continue;
            results.add(new RetrievedChunk(
                    String.valueOf(source.get("documentName")),
                    ((Number) source.get("pageNumber")).intValue(),
                    String.valueOf(source.get("content")),
                    ((Number) hit.getOrDefault("_score", 0.0)).doubleValue()));
        }
        return results;
    }
}
