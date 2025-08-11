package com.AcademyGPT.AI.util;

import java.util.*;

public class TextChunker {
    public static List<String> chunkText(String content, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < content.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, content.length());
            chunks.add(content.substring(i, end));
        }
        return chunks;
    }
}
