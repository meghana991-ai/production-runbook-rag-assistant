package com.meghana.runbookrag.elasticsearch;

import com.meghana.runbookrag.core.Retriever;
import com.meghana.runbookrag.ingestion.ChunkStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("elasticsearch")
class ElasticsearchProfileTest {

    @Autowired
    private ChunkStore chunkStore;

    @Autowired
    private Retriever retriever;

    @Test
    void activatesElasticsearchAdapters() {
        assertThat(chunkStore).isInstanceOf(ElasticsearchChunkStore.class);
        assertThat(retriever).isInstanceOf(ElasticsearchVectorRetriever.class);
    }
}
