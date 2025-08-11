package com.AcademyGPT.AI.vectorstore;


import com.AcademyGPT.AI.model.Chunk;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class FaissStore {
    private List<Chunk> chunks = new ArrayList<>();

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
    }

    public List<Chunk> search(List<Double> queryEmbedding, int topK) {
        // Dummy: return top K based on dummy logic
        return chunks.subList(0, Math.min(topK, chunks.size()));
    }
}
