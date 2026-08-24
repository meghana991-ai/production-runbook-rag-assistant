package com.meghana.runbookrag.ingestion;

import java.io.IOException;
import java.util.List;

public interface TextExtractor {
    boolean supports(String contentType, String filename);

    List<DocumentPage> extract(byte[] content) throws IOException;
}
