package com.AcademyGPT.AI.embedding;

import com.AcademyGPT.AI.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HuggingFaceEmbedding implements EmbeddingService {

    // Local Flask API endpoint
    private static final String LOCAL_MODEL_URL = "http://127.0.0.1:5000/embed";

    @Override
    public List<Double> getEmbedding(String text) {
        return tryGetEmbedding(LOCAL_MODEL_URL, text);
    }

    private List<Double> tryGetEmbedding(String url, String text) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("text", text);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Expecting a JSON object like: { "embedding": [ ... ] }
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object embeddingObj = response.getBody().get("embedding");

                if (embeddingObj instanceof List) {
                    return ((List<?>) embeddingObj).stream()
                            .map(val -> ((Number) val).doubleValue())
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling local embedding model: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
