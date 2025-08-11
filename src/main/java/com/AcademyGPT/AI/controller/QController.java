package com.AcademyGPT.AI.controller;

import com.AcademyGPT.AI.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/qa")
@RequiredArgsConstructor
public class QController {

    private final GPTService gptService;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, String> payload) throws IOException {
        String question = payload.get("question");

        // Get answer + sources from GPTService (Gemini integration)
        Map<String, Object> result = gptService.answer(question);

        return ResponseEntity.ok(result);
    }
}
