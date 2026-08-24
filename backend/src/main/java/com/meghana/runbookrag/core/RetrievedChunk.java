package com.meghana.runbookrag.core;

public record RetrievedChunk(String documentName, int pageNumber, String content, double score) {
}
