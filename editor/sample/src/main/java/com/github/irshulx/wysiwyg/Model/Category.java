package com.github.irshulx.wysiwyg.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class Category implements Serializable {
    private String categoryName;
    private ArrayList<Memo> memoPool;
    private ArrayList<Category> childCategoryPool;
    private Category parent;

    public ArrayList<Memo> getMemoPool() {
        return memoPool;
    }

    public void setMemoPool(ArrayList<Memo> memoPool) {
        this.memoPool = memoPool;
    }

    private int numMemo;

    public Category(String categoryName, int numMemo) {
        this.categoryName = categoryName;
        this.numMemo = numMemo;
        memoPool = new ArrayList<Memo>();
        childCategoryPool = new ArrayList<Category>();
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.numMemo = 0;
        memoPool = new ArrayList<Memo>();
        childCategoryPool = new ArrayList<Category>();
    }

    public void addChildCategory(Category category){
        childCategoryPool.add(category);
    }

    public void addMemo(Memo memo){
        memoPool.add(memo);
    }

    public ArrayList<Category> getChildCategoryPool() {
        return childCategoryPool;
    }

    public void setChildCategoryPool(ArrayList<Category> childCategoryPool) {
        this.childCategoryPool = childCategoryPool;
    }

    public boolean hasChild(){
        if(childCategoryPool.size() == 0)
            return false;
        else
            return true;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getNumMemo() {
        return numMemo;
    }

    public void setNumMemo(int numMemo) {
        this.numMemo = numMemo;
    }
}
