package com.AcademyGPT.AI.model;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Chunk {
    private String id;
    private String text;
    private float[] embedding;

    public Chunk() {
        this.id = UUID.randomUUID().toString();
    }

    public Chunk(String text, float[] embedding) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.embedding = embedding;
    }

    public Chunk(String id, String text, float[] embedding) {
        this.id = id;
        this.text = text;
        this.embedding = embedding;
    }

    // ✅ Updated constructor to convert List<Double> to float[]
    public Chunk(String id, String text, List<Double> embeddingList) {
        this.id = id;
        this.text = text;
        this.embedding = convertToFloatArray(embeddingList);
    }

    // Utility method to convert List<Double> to float[]
    private float[] convertToFloatArray(List<Double> list) {
        float[] floatArray = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            floatArray[i] = list.get(i).floatValue();
        }
        return floatArray;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", embedding=" + Arrays.toString(embedding) +
                '}';
    }
}
