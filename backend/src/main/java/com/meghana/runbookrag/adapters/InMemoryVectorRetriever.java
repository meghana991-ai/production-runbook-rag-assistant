package com.meghana.runbookrag.adapters;

import com.meghana.runbookrag.core.RetrievedChunk;
import com.meghana.runbookrag.core.Retriever;
import com.meghana.runbookrag.embedding.EmbeddingClient;
import com.meghana.runbookrag.ingestion.EmbeddedChunk;
import com.meghana.runbookrag.ingestion.InMemoryChunkStore;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.Comparator;
import java.util.List;

@Primary
@Component
@Profile("!elasticsearch")
public class InMemoryVectorRetriever implements Retriever {

    private final InMemoryChunkStore store;
    private final EmbeddingClient embeddingClient;

    public InMemoryVectorRetriever(InMemoryChunkStore store, EmbeddingClient embeddingClient) {
        this.store = store;
        this.embeddingClient = embeddingClient;
    }

    @Override
    public List<RetrievedChunk> retrieve(String question, int limit) {
        List<Double> query = embeddingClient.embed(List.of(question)).get(0);
        return store.findAll().stream()
                .map(chunk -> toRetrieved(chunk, cosine(query, chunk.embedding())))
                .filter(chunk -> chunk.score() > 0.15)
                .sorted(Comparator.comparingDouble(RetrievedChunk::score).reversed())
                .limit(limit)
                .toList();
    }

    private RetrievedChunk toRetrieved(EmbeddedChunk stored, double score) {
        var chunk = stored.chunk();
        return new RetrievedChunk(chunk.documentName(), chunk.pageNumber(), chunk.content(), score);
    }

    private double cosine(List<Double> left, List<Double> right) {
        double dot = 0;
        for (int index = 0; index < left.size(); index++) dot += left.get(index) * right.get(index);
        return dot;
    }
}
