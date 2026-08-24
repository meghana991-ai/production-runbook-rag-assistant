package com.meghana.runbookrag.ingestion;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeterministicChunkerTest {

    private final DeterministicChunker chunker = new DeterministicChunker();

    @Test
    void preservesPageMetadataAndCreatesOverlap() {
        String text = ("restart checkout service after checking health endpoint. ").repeat(30);

        List<DocumentChunk> chunks = chunker.chunk(
                UUID.fromString("c0a80101-0000-0000-0000-000000000001"),
                "checkout.txt",
                List.of(new DocumentPage(7, text)));

        assertThat(chunks).hasSizeGreaterThan(1);
        assertThat(chunks).allSatisfy(chunk -> {
            assertThat(chunk.documentName()).isEqualTo("checkout.txt");
            assertThat(chunk.pageNumber()).isEqualTo(7);
            assertThat(chunk.content()).isNotBlank();
        });
        String tail = chunks.get(0).content().substring(chunks.get(0).content().length() - 80);
        assertThat(chunks.get(1).content()).contains(tail.trim());
    }

    @Test
    void producesStableChunksForTheSameInput() {
        UUID id = UUID.fromString("c0a80101-0000-0000-0000-000000000002");
        List<DocumentPage> pages = List.of(new DocumentPage(1, ("alpha beta gamma ").repeat(100)));

        assertThat(chunker.chunk(id, "runbook.txt", pages))
                .isEqualTo(chunker.chunk(id, "runbook.txt", pages));
    }
}
