package com.meghana.runbookrag.ingestion;

import com.meghana.runbookrag.embedding.EmbeddingClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentIngestionService {

    private final List<TextExtractor> extractors;
    private final DeterministicChunker chunker;
    private final ChunkStore chunkStore;
    private final EmbeddingClient embeddingClient;

    public DocumentIngestionService(
            List<TextExtractor> extractors,
            DeterministicChunker chunker,
            ChunkStore chunkStore,
            EmbeddingClient embeddingClient
    ) {
        this.extractors = extractors;
        this.chunker = chunker;
        this.chunkStore = chunkStore;
        this.embeddingClient = embeddingClient;
    }

    public IngestionResult ingest(MultipartFile file) {
        String filename = sanitizeFilename(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new IllegalArgumentException("file must not be empty");
        }

        TextExtractor extractor = extractors.stream()
                .filter(candidate -> candidate.supports(file.getContentType(), filename))
                .findFirst()
                .orElseThrow(() -> new UnsupportedDocumentException(
                        "Only PDF and UTF-8 text documents are supported"));

        try {
            UUID documentId = UUID.randomUUID();
            List<DocumentPage> pages = extractor.extract(file.getBytes());
            List<DocumentChunk> chunks = chunker.chunk(documentId, filename, pages);
            if (chunks.isEmpty()) {
                throw new IllegalArgumentException("document contains no extractable text");
            }
            List<List<Double>> embeddings = embeddingClient.embed(
                    chunks.stream().map(DocumentChunk::content).toList());
            if (embeddings.size() != chunks.size()) {
                throw new IllegalStateException("Embedding count does not match chunk count");
            }
            List<EmbeddedChunk> embeddedChunks = java.util.stream.IntStream.range(0, chunks.size())
                    .mapToObj(index -> new EmbeddedChunk(chunks.get(index), embeddings.get(index)))
                    .toList();
            chunkStore.saveAll(embeddedChunks);
            return new IngestionResult(documentId, filename, pages.size(), chunks.size());
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read document", exception);
        }
    }

    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "document";
        }
        String normalized = originalFilename.replace('\\', '/');
        return normalized.substring(normalized.lastIndexOf('/') + 1);
    }
}
