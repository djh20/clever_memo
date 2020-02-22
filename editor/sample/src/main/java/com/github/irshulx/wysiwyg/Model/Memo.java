package com.github.irshulx.wysiwyg.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class Memo implements Serializable {
    private int memoIndex;
    private String memoName;
    private String imagePath;
    private Category category;
    private String updateDate;
    private String addedDate;
    private ArrayList<WordBag> wordBagPool;
    private int numPage;

    public Memo(int memoIndex, String memoName, String imagePath, Category category, String updateDate, String addedDate, int numPage) {
        this.memoIndex = memoIndex;
        this.memoName = memoName;
        this.imagePath = imagePath;
        this.category = category;
        this.updateDate = updateDate;
        this.addedDate = addedDate;
        this.wordBagPool = new ArrayList<WordBag>();
        this.numPage = numPage;
    }

    public Memo(int memoIndex, String memoName, String imagePath, int numPage){
        SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyy년 MM월dd일 HH시mm분ss초");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        this.memoIndex = memoIndex;
        this.memoName = memoName;
        this.imagePath = imagePath;
        category = null;
        updateDate = dateString;
        addedDate = dateString;
        wordBagPool = new ArrayList<WordBag>();
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

    public ArrayList<WordBag> getWordBagPool() {
        return wordBagPool;
    }

    public void setWordBagPool(ArrayList<WordBag> wordBagPool) {
        this.wordBagPool = wordBagPool;
    }

    public int getNumPage() {
        return numPage;
    }

    public void setNumPage(int numPage) {
        this.numPage = numPage;
    }

    public void addWordBag(Word word, int frequency){
        wordBagPool.add(new WordBag(word, frequency));
    }
}