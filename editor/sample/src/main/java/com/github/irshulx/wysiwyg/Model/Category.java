package com.github.irshulx.wysiwyg.Model;

public class Category {
    private String categoryName;
    private Category parent;
    private int numMemo;

    public Category(String categoryName, Category parent, int numMemo) {
        this.categoryName = categoryName;
        this.parent = parent;
        this.numMemo = numMemo;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public int getNumMemo() {
        return numMemo;
    }

    public void setNumMemo(int numMemo) {
        this.numMemo = numMemo;
    }
}
