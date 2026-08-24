package com.meghana.runbookrag.ingestion;

import java.util.List;

public interface ChunkStore {
    void saveAll(List<DocumentChunk> chunks);
}
