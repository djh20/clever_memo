package com.github.irshulx.wysiwyg.Model;

public class WordBag {
    private Word word;
    private int termFrequency;

    public WordBag(Word word, int termFrequency) {
        this.word = word;
        this.termFrequency = termFrequency;
    }

    public int getTermFrequency() {
        return termFrequency;
    }

    public Word getWord() {
        return word;
    }
}
