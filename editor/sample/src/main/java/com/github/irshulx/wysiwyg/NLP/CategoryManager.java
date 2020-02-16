package com.github.irshulx.wysiwyg.NLP;

import com.github.irshulx.wysiwyg.Model.Category;

import java.io.Serializable;
import java.util.Vector;

public class CategoryManager implements Serializable {
    Vector<Category> categortPool;

    public CategoryManager() {
        this.categortPool = new Vector<Category>();
    }

    public void saveCategoryInDatabase() {
    }
}
