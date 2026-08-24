package com.meghana.runbookrag.ingestion;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PlainTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String contentType, String filename) {
        return "text/plain".equalsIgnoreCase(contentType)
                || filename.toLowerCase().endsWith(".txt");
    }

    @Override
    public List<DocumentPage> extract(byte[] content) {
        return List.of(new DocumentPage(1, new String(content, StandardCharsets.UTF_8)));
    }
}
