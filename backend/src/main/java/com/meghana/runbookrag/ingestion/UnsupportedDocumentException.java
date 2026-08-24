package com.meghana.runbookrag.ingestion;

public class UnsupportedDocumentException extends RuntimeException {
    public UnsupportedDocumentException(String message) {
        super(message);
    }
}
