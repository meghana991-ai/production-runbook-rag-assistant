package com.meghana.runbookrag.ingestion;

import java.util.List;

public record EmbeddedChunk(DocumentChunk chunk, List<Double> embedding) {
}
