package com.github.irshulx.wysiwyg.NLP;

import com.github.irshulx.wysiwyg.Model.Category;
import com.github.irshulx.wysiwyg.Model.Memo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class CategoryManager implements Serializable {
    ArrayList<Category> categortPool;

    public CategoryManager() {
        this.categortPool = new ArrayList<Category>();
    }

    public void saveCategoryInDatabase() {
    }

    public ArrayList<Double> getCenterVector(Category category, MemoManager memoManager){
        ArrayList<Double> centerVector = new ArrayList<Double>();
        ArrayList<Memo> memosInCategory = category.getMemoPool();
        for(int i = 0 ; i < memosInCategory.size() ; i++) {
            Memo memoFromCategory = memosInCategory.get(i);
            ArrayList<Double> memoVector =  memoManager.getMemoVector(memoFromCategory);

            for(int j = 0 ; j < memoVector.size() ; j++) {
                try {
                    centerVector.set(j, centerVector.get(j) + memoVector.get(j));
                }
                catch(Exception e) {
                    centerVector.add(memoVector.get(j));
                }
            }
        }

        for(int i = 0 ; i < centerVector.size() ; i++) {
            centerVector.set(i, centerVector.get(i)/memosInCategory.size());
        }
        return centerVector;
    }

    public ArrayList<Category> getCategortPool(){return categortPool;}

    public Category addNewCategory(String categorytName) {
        boolean existFlag = false;
        for(int i = 0 ; i < categortPool.size() ; i++){
            Category category = categortPool.get(i);
            if(category.getCategoryName().equals(categorytName) == true) {
                existFlag = true;
                break;
            }
        }

        if(existFlag == true)
            return null;
        else
            return new Category(categorytName);


    }

    public boolean addMemoIntoCategory(Memo memo, String categoryName) {
        Category objectCategory = null;
        boolean existFlag = false;
        for(int i = 0 ; i < categortPool.size() ; i++){
            Category category = categortPool.get(i);
            if(category.getCategoryName().equals(categoryName) == true) {
                objectCategory = category;
                existFlag = true;
            }
        }
        if(existFlag == false)
            return false;
        else{
            objectCategory.addMemo(memo);
            objectCategory.setNumMemo(objectCategory.getNumMemo()+1);
            memo.setCategory(objectCategory);
            return true;
        }
    }

    public ArrayList<Memo> getMemoPoolInCategory(String title) {
        for (int i = 0; i < categortPool.size(); i++) {
            Category category = categortPool.get(i);
            if (category.getCategoryName().equals(title))
                return category.getMemoPool();
        }
        return null;
    }
}
