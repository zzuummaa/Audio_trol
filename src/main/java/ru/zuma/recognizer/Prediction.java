package ru.zuma.recognizer;

public class Prediction {
    private int label;
    private double confidence;

    public Prediction() {
    }

    public Prediction(int label, double confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
