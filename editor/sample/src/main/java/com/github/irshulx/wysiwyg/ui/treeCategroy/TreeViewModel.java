package com.github.irshulx.wysiwyg.ui.treeCategroy;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class TreeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TreeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}