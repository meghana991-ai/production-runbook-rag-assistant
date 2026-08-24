package com.meghana.runbookrag.core;

import java.util.List;

public interface Retriever {
    List<RetrievedChunk> retrieve(String question, int limit);
}
