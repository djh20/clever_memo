package com.github.irshulx.wysiwyg.Model;

import java.io.Serializable;

public class Word implements Serializable {
    private String word;
    private int documecntFrequency;
    private int globalFrequency;
    private double idf;

    public Word(String word, int documecntFrequency, int globalFrequency, double idf) {
        this.word = word;
        this.documecntFrequency = documecntFrequency;
        this.globalFrequency = globalFrequency;
        this.idf = idf;
    }

    public Word(String word, int documecntFrequency, int globalFrequency) {
        this.word = word;
        this.documecntFrequency = documecntFrequency;
        this.globalFrequency = globalFrequency;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getDocumecntFrequency() {
        return documecntFrequency;
    }

    public void setDocumecntFrequency(int documecntFrequency) {
        this.documecntFrequency = documecntFrequency;
    }

    public int getGlobalFrequency() {
        return globalFrequency;
    }

    public void setGlobalFrequency(int globalFrequency) {
        this.globalFrequency = globalFrequency;
    }

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public void increaseDocumentFrequency(){ documecntFrequency++;};
    public void addGlobalFrequency(int frequency){ globalFrequency += frequency;};


}
