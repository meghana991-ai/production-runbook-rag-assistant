package com.meghana.runbookrag.ingestion;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryChunkStore implements ChunkStore {

    private final List<DocumentChunk> chunks = new CopyOnWriteArrayList<>();

    @Override
    public void saveAll(List<DocumentChunk> chunks) {
        this.chunks.addAll(chunks);
    }

    public List<DocumentChunk> findAll() {
        return List.copyOf(chunks);
    }
}
