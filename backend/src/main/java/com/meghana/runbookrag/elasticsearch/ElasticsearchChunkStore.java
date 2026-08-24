package com.meghana.runbookrag.elasticsearch;

import com.meghana.runbookrag.ingestion.ChunkStore;
import com.meghana.runbookrag.ingestion.EmbeddedChunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Repository
@Profile("elasticsearch")
public class ElasticsearchChunkStore implements ChunkStore {

    private final RestClient client;
    private final String index;
    private final int dimensions;
    private final AtomicBoolean initialized = new AtomicBoolean();

    public ElasticsearchChunkStore(
            RestClient elasticsearchRestClient,
            @Value("${rag.elasticsearch.index:runbook-chunks}") String index,
            @Value("${rag.embedding.dimensions:256}") int dimensions
    ) {
        this.client = elasticsearchRestClient;
        this.index = index;
        this.dimensions = dimensions;
    }

    @Override
    public void saveAll(List<EmbeddedChunk> chunks) {
        ensureIndex();
        for (EmbeddedChunk stored : chunks) {
            var chunk = stored.chunk();
            String id = chunk.documentId() + "-" + chunk.chunkIndex();
            client.put()
                    .uri("/{index}/_doc/{id}", index, id)
                    .body(Map.of(
                            "documentId", chunk.documentId().toString(),
                            "documentName", chunk.documentName(),
                            "pageNumber", chunk.pageNumber(),
                            "chunkIndex", chunk.chunkIndex(),
                            "content", chunk.content(),
                            "embedding", stored.embedding()))
                    .retrieve()
                    .toBodilessEntity();
        }
        client.post().uri("/{index}/_refresh", index).retrieve().toBodilessEntity();
    }

    private void ensureIndex() {
        if (!initialized.compareAndSet(false, true)) return;
        try {
            client.put()
                    .uri("/{index}", index)
                    .body(Map.of("mappings", Map.of("properties", Map.of(
                            "documentId", Map.of("type", "keyword"),
                            "documentName", Map.of("type", "keyword"),
                            "pageNumber", Map.of("type", "integer"),
                            "chunkIndex", Map.of("type", "integer"),
                            "content", Map.of("type", "text"),
                            "embedding", Map.of(
                                    "type", "dense_vector",
                                    "dims", dimensions,
                                    "index", true,
                                    "similarity", "cosine")))))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest alreadyExists) {
            if (!alreadyExists.getResponseBodyAsString().contains("resource_already_exists_exception")) {
                initialized.set(false);
                throw alreadyExists;
            }
        } catch (RuntimeException exception) {
            initialized.set(false);
            throw exception;
        }
    }
}
