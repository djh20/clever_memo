package com.github.irshulx.wysiwyg.ui.itemList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.irshulx.wysiwyg.FirstActivity;
import com.github.irshulx.wysiwyg.R;

public class ItemListFragment extends Fragment {

    private String[] items;

    public ItemListFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itemlist, container, false);
        if(getActivity() != null && getActivity() instanceof FirstActivity){
            items = ((FirstActivity)getActivity()).getData();
        }
        ListAdapter adapter = new ItemAdapter(getContext(), items);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        return view;
    }

}