package com.meghana.runbookrag.ingestion;

import java.util.UUID;

public record IngestionResult(UUID documentId, String documentName, int pages, int chunks) {
}
