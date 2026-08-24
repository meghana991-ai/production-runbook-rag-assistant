package com.meghana.runbookrag.embedding;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HashingEmbeddingClientTest {

    private final HashingEmbeddingClient client = new HashingEmbeddingClient();

    @Test
    void createsStableNormalizedVectors() {
        List<Double> first = client.embed(List.of("restart checkout service")).get(0);
        List<Double> second = client.embed(List.of("restart checkout service")).get(0);

        assertThat(first).hasSize(HashingEmbeddingClient.DIMENSIONS).isEqualTo(second);
        double norm = Math.sqrt(first.stream().mapToDouble(value -> value * value).sum());
        assertThat(norm).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.000001));
    }
}
