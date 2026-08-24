package com.meghana.runbookrag.ingestion;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentIngestionServiceTest {

    private final InMemoryChunkStore store = new InMemoryChunkStore();
    private final DocumentIngestionService service = new DocumentIngestionService(
            List.of(new PlainTextExtractor()), new DeterministicChunker(), store);

    @Test
    void ingestsTextAndRemovesPathFromFilename() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "../../checkout-runbook.txt", "text/plain",
                "Restart checkout after checking dependencies.".getBytes(StandardCharsets.UTF_8));

        IngestionResult result = service.ingest(file);

        assertThat(result.documentName()).isEqualTo("checkout-runbook.txt");
        assertThat(result.pages()).isEqualTo(1);
        assertThat(result.chunks()).isEqualTo(1);
        assertThat(store.findAll()).singleElement()
                .extracting(DocumentChunk::content)
                .isEqualTo("Restart checkout after checking dependencies.");
    }

    @Test
    void rejectsUnsupportedFiles() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "runbook.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                new byte[]{1, 2, 3});

        assertThatThrownBy(() -> service.ingest(file))
                .isInstanceOf(UnsupportedDocumentException.class);
    }
}
