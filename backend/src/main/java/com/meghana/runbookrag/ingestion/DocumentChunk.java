package com.meghana.runbookrag.ingestion;

import java.util.UUID;

public record DocumentChunk(
        UUID documentId,
        String documentName,
        int pageNumber,
        int chunkIndex,
        String content
) {
}
