package com.meghana.runbookrag.ingestion;

import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("!elasticsearch")
public class InMemoryChunkStore implements ChunkStore {

    private final List<EmbeddedChunk> chunks = new CopyOnWriteArrayList<>();

    @Override
    public void saveAll(List<EmbeddedChunk> chunks) {
        this.chunks.addAll(chunks);
    }

    public List<EmbeddedChunk> findAll() {
        return List.copyOf(chunks);
    }
}
