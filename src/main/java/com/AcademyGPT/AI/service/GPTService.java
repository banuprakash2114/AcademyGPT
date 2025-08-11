package com.AcademyGPT.AI.service;

import com.AcademyGPT.AI.model.Chunk;
import com.AcademyGPT.AI.vectorstore.FaissStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GPTService {
    private final EmbeddingService embeddingService;
    private final FaissStore faissStore;

    // Replace with your actual Gemini API key
    private static final String GEMINI_API_KEY = "AIzaSyAy7EyWGidvSqguAD8ZaJusmTCWEa01rgU";
    private static final String GEMINI_MODEL = "gemini-2.5-flash"; // ✅ valid for v1beta
    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key=";

    public Map<String, Object> answer(String question) throws IOException {
        // Step 1: Get embedding for the question
        List<Double> qEmbedding = embeddingService.getEmbedding(question);

        // Step 2: Search FAISS for top relevant chunks
        List<Chunk> topChunks = faissStore.search(qEmbedding, 5);

        // Step 3: Build context from chunks
        String context = topChunks.stream()
                .map(Chunk::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        // Step 4: Get answer from Gemini API
        String answer = callGemini(question, context);

        // Step 5: Return answer + sources
        Map<String, Object> response = new HashMap<>();
        response.put("answer", answer);
        response.put("sources", topChunks.stream().map(Chunk::getText).toList());
        return response;
    }

    private String callGemini(String question, String context) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> request = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", """
                                            You are an academic assistant. Use the context below to answer the question.
                                            If the answer is not in the context, say you don’t know.

                                            Context:
                                            """ + context + "\n\nQuestion: " + question)
                            ))
                    )
            );

            Map<String, Object> response = restTemplate.postForObject(
                    GEMINI_API_URL + GEMINI_API_KEY.trim(),
                    request,
                    Map.class
            );

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    if (content != null && content.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (!parts.isEmpty()) {
                            return parts.get(0).get("text").toString();
                        }
                    }
                }
            }
            return "No answer from Gemini.";
        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
