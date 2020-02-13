package com.github.irshulx.wysiwyg.Model;

import java.util.Vector;

public class Memo {
    private int memoIndex;
    private String memoName;
    private String imagePath;
    private Category category;
    private String updateDate;
    private String addedDate;
    private Vector<WordBag> wordBagPool;
    private int numPage;

    public Memo(int memoIndex, String memoName, String imagePath, Category category, String updateDate, String addedDate, int numPage) {
        this.memoIndex = memoIndex;
        this.memoName = memoName;
        this.imagePath = imagePath;
        this.category = category;
        this.updateDate = updateDate;
        this.addedDate = addedDate;
        this.wordBagPool = new Vector<WordBag>();
        this.numPage = numPage;
    }

    public int getMemoIndex() {
        return memoIndex;
    }

    public void setMemoIndex(int memoIndex) {
        this.memoIndex = memoIndex;
    }

    public String getMemoName() {
        return memoName;
    }

    public void setMemoName(String memoName) {
        this.memoName = memoName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public Vector<WordBag> getWordBagPool() {
        return wordBagPool;
    }

    public void setWordBagPool(Vector<WordBag> wordBagPool) {
        this.wordBagPool = wordBagPool;
    }

    public int getNumPage() {
        return numPage;
    }

    public void setNumPage(int numPage) {
        this.numPage = numPage;
    }
}