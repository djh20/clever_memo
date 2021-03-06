package com.github.irshulx.wysiwyg.View.cloud;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class CloudViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CloudViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}