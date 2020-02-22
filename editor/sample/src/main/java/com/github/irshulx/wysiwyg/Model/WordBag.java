package com.github.irshulx.wysiwyg.Model;

import java.io.Serializable;

public class WordBag implements Serializable {
    private Word word;
    private int termFrequency;

    public WordBag(Word word, int termFrequency) {
        this.word = word;
        this.termFrequency = termFrequency;
    }

    public int getTermFrequency() {
        return termFrequency;
    }
    public double getTfIdf(){
        return word.getIdf() * termFrequency;
    }
    public Word getWord() {
        return word;
    }
}
