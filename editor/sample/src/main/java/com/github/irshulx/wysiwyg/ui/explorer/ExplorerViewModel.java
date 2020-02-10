package com.github.irshulx.wysiwyg.ui.explorer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class ExplorerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ExplorerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}