package com.github.irshulx.wysiwyg.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.irshulx.wysiwyg.NLP.MemoLoadManager;
import com.github.irshulx.wysiwyg.R;

public class toolFragment extends Fragment {

    public toolFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tool_layout, container, false);
        settingButton(view);
        return view;


    }
    private void settingButton(View view){
        ImageView black = (ImageView) view.findViewById(R.id.black);
        ImageView red = (ImageView) view.findViewById(R.id.red);
        ImageView blue = (ImageView) view.findViewById(R.id.blue);
        ImageView yellow = (ImageView) view.findViewById(R.id.yellow);
        ImageView pink = (ImageView) view.findViewById(R.id.pink);
        ImageView more = (ImageView) view.findViewById(R.id.more);
        final MemoLoadManager memoLoadManager = (MemoLoadManager)getActivity();

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoLoadManager.setDefaultPen(getResources().getColor(android.R.color.black), 10, 255);
                closeAll();
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoLoadManager.setDefaultPen(Color.parseColor("#d42222"),12, 255);
                closeAll();
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoLoadManager.setDefaultPen(Color.parseColor("#0d0acc"),12, 255);
                closeAll();
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoLoadManager.setDefaultPen(Color.parseColor("#e5ff00"),45, 100);
                closeAll();
            }
        });

        pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoLoadManager.setDefaultPen(Color.parseColor("#ff00c8"),45, 100);
                closeAll();

            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoLoadManager.showWriteSetup();

                closeAll();
            }
        });
    }
    private void closeAll(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.show_from_right, R.anim.close_to_right, R.anim.show_from_right, R.anim.close_to_right)
                .remove(toolFragment.this)
                .commit();
        fragmentManager.popBackStack();
    }
}
