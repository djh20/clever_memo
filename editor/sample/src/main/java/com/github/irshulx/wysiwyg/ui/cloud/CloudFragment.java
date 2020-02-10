package com.github.irshulx.wysiwyg.ui.cloud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.github.irshulx.wysiwyg.R;


public class CloudFragment extends Fragment {


    private CloudViewModel cloudViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cloudViewModel =
                ViewModelProviders.of(this).get(CloudViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cloud, container, false);
        final TextView textView = root.findViewById(R.id.cloud);
        cloudViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        return root;
    }
}