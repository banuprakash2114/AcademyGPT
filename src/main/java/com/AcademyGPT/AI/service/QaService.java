package com.AcademyGPT.AI.service;

import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.*;

@Service
public class QaService {
    private static final Logger logger = LoggerFactory.getLogger(QaService.class);
    private final RestTemplate restTemplate;

    public QaService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .errorHandler(new DefaultResponseErrorHandler() {
                    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) {
                        // Skip throwing exception to handle it ourselves
                    }
                })
                .build();
    }

    public List<String> getMatchingChunks(String userQuestion) {
        String[] possibleUrls = {
                "http://localhost:5000/search",
                "http://127.0.0.1:5000/search"
        };

        for (String flaskUrl : possibleUrls) {
            try {
                logger.info("Attempting connection to: {}", flaskUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                Map<String, String> request = Map.of("query", userQuestion);
                HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(
                        flaskUrl, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    List<String> chunks = (List<String>) response.getBody().get("chunks");
                    logger.info("Successfully retrieved {} chunks from {}",
                            chunks != null ? chunks.size() : 0, flaskUrl);
                    return chunks != null ? chunks : Collections.emptyList();
                }
            } catch (HttpClientErrorException e) {
                logger.error("HTTP error calling {}: {} - {}",
                        flaskUrl, e.getStatusCode(), e.getResponseBodyAsString());
            } catch (ResourceAccessException e) {
                logger.error("Connection failed to {}: {}", flaskUrl, e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error calling {}: {}", flaskUrl, e.getMessage());
            }
        }

        logger.warn("All connection attempts to Flask service failed");
        return Collections.emptyList();
    }
}