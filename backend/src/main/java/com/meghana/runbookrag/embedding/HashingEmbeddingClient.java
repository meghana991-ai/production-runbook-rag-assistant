package com.meghana.runbookrag.embedding;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class HashingEmbeddingClient implements EmbeddingClient {

    public static final int DIMENSIONS = 256;

    @Override
    public List<List<Double>> embed(List<String> inputs) {
        return inputs.stream().map(this::embedOne).toList();
    }

    private List<Double> embedOne(String input) {
        double[] vector = new double[DIMENSIONS];
        for (String token : input.toLowerCase().split("\\W+")) {
            if (token.isBlank()) continue;
            byte[] hash = sha256(token);
            int position = ((hash[0] & 0xff) << 8 | (hash[1] & 0xff)) % DIMENSIONS;
            vector[position] += (hash[2] & 1) == 0 ? 1.0 : -1.0;
        }
        double norm = Math.sqrt(java.util.Arrays.stream(vector).map(value -> value * value).sum());
        List<Double> result = new ArrayList<>(DIMENSIONS);
        for (double value : vector) result.add(norm == 0 ? 0.0 : value / norm);
        return result;
    }

    private byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
