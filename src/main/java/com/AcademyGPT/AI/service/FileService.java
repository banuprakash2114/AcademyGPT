package com.AcademyGPT.AI.service;

import com.AcademyGPT.AI.model.Chunk;
import com.AcademyGPT.AI.util.TextChunker;
import com.AcademyGPT.AI.vectorstore.FaissStore;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final EmbeddingService embeddingService;
    private final FaissStore faissStore;

    public void processUploadedPdf(MultipartFile file) throws IOException {
        String text = extractTextFromPdf(file);
        List<String> chunks = TextChunker.chunkText(text, 500);
        for (String chunk : chunks) {
            List<Double> embedding = embeddingService.getEmbedding(chunk);
            faissStore.addChunk(new Chunk(UUID.randomUUID().toString(), chunk, embedding));
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public void processUploadedText(String text) throws IOException {
        List<String> chunks = TextChunker.chunkText(text, 500);
        for (String chunk : chunks) {
            List<Double> embedding = embeddingService.getEmbedding(chunk);
            faissStore.addChunk(new Chunk(UUID.randomUUID().toString(), chunk, embedding));
        }
    }
}

