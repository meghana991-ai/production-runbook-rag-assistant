package com.meghana.runbookrag.adapters;

import com.meghana.runbookrag.core.RetrievedChunk;
import com.meghana.runbookrag.core.Retriever;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmptyRetriever implements Retriever {
    @Override
    public List<RetrievedChunk> retrieve(String question, int limit) {
        return List.of();
    }
}
