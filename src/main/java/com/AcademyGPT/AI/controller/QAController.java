package com.AcademyGPT.AI.controller;


import com.AcademyGPT.AI.service.FileService;
import com.AcademyGPT.AI.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf") // Base path
@RequiredArgsConstructor
public class QAController {
    private final FileService fileService;
    private final GPTService gptService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            fileService.processUploadedPdf(file);
            return "PDF uploaded and processed.";
        } catch (IOException e) {
            return "Failed to process PDF: " + e.getMessage();
        }
    }

    @PostMapping("/ask")
    public String ask(@RequestBody Map<String, String> body) throws IOException {
        String question = body.get("question");
        return gptService.answer(question).toString();
    }
}

