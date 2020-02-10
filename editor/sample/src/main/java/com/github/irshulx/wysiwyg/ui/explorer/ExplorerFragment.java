package com.github.irshulx.wysiwyg.ui.explorer;

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

public class ExplorerFragment extends Fragment {

    private ExplorerViewModel explorerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        explorerViewModel =
                ViewModelProviders.of(this).get(ExplorerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_explorer, container, false);
        final TextView textView = root.findViewById(R.id.explorer);
        explorerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}