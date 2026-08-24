package com.meghana.runbookrag.ingestion;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DeterministicChunker {

    static final int CHUNK_SIZE = 1_000;
    static final int OVERLAP = 150;

    public List<DocumentChunk> chunk(UUID documentId, String documentName, List<DocumentPage> pages) {
        List<DocumentChunk> chunks = new ArrayList<>();
        int chunkIndex = 0;

        for (DocumentPage page : pages) {
            String text = normalize(page.text());
            int start = 0;
            while (start < text.length()) {
                int end = findBoundary(text, start, Math.min(start + CHUNK_SIZE, text.length()));
                String content = text.substring(start, end).trim();
                if (!content.isBlank()) {
                    chunks.add(new DocumentChunk(
                            documentId, documentName, page.pageNumber(), chunkIndex++, content));
                }
                if (end == text.length()) {
                    break;
                }
                start = Math.max(start + 1, end - OVERLAP);
            }
        }
        return chunks;
    }

    private int findBoundary(String text, int start, int proposedEnd) {
        if (proposedEnd == text.length()) {
            return proposedEnd;
        }
        int whitespace = text.lastIndexOf(' ', proposedEnd);
        return whitespace > start ? whitespace : proposedEnd;
    }

    private String normalize(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }
}
