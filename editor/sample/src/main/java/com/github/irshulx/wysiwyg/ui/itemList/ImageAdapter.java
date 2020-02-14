package com.github.irshulx.wysiwyg.ui.itemList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.irshulx.wysiwyg.R;

public class ImageAdapter extends ArrayAdapter<String> {

    ImageAdapter(Context context, String[] items){
        super(context, R.layout.frag_itemlist_item, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater imageInflater = LayoutInflater.from(getContext());
        View view = imageInflater.inflate(R.layout.frag_itemlist_item, parent, false);
        String item = getItem(position);
        TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
        TextView itemDetail = (TextView) view.findViewById(R.id.item_detail);
        ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
        itemTitle.setText(item);
        itemImage.setImageResource(R.drawable.symbol_pdf);
        return view;
    }
}
