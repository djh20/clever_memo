package com.github.irshulx.wysiwyg.Model;

import java.util.Vector;

public class Category {
    private String categoryName;
    private Vector<Memo> memoPool;
    private Vector<Category> childCategoryPool;
    private int numMemo;

    public Category(String categoryName, int numMemo) {
        this.categoryName = categoryName;
        this.numMemo = numMemo;
        memoPool = new Vector<Memo>();
        childCategoryPool = new Vector<Category>();
    }

    public void addChildCategory(Category category){
        childCategoryPool.add(category);
    }

    public void addMemo(Memo memo){
        memoPool.add(memo);
    }

    public Vector<Category> getChildCategoryPool() {
        return childCategoryPool;
    }

    public void setChildCategoryPool(Vector<Category> childCategoryPool) {
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
