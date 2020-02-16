package com.github.irshulx.wysiwyg.Model;

import java.util.ArrayList;
import java.util.Locale;

public class CategoryStructure {
    private String name; // Category name
    private ArrayList<String> detailCategory;

    public CategoryStructure(String name, ArrayList<String> detailCategory){
        this.name = name;
        this.detailCategory = detailCategory;
    }

    public CategoryStructure(String name){
        this(name , null);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getDetailCategory() {
        return detailCategory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetailCategory(ArrayList<String> detailCategory) {
        this.detailCategory = detailCategory;
    }

    public void add(String detail){
        detailCategory.add(detail);
    }

    public boolean isEmpty(){
        return detailCategory==null;
    }

    public int getDetailSize(){
        return detailCategory.size();
    }

}
