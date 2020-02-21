package com.github.irshulx.wysiwyg.ui.itemList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.irshulx.wysiwyg.R;

public class ItemAdapter extends ArrayAdapter<String> {

    public ItemAdapter(Context context, String[] items){
        super(context, R.layout.frag_itemlist_item, items);
    }


    //TODO 기존 메모 로드하는 코드 짜기
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater itemInflater = LayoutInflater.from(getContext());
        View view = itemInflater.inflate(R.layout.frag_itemlist_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("d", "dsadasdsa");
            }
        });
        String item = getItem(position);
        TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
        itemTitle.setMaxWidth(itemTitle.getWidth());
        ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
        itemTitle.setText(item);
        itemImage.setImageResource(R.drawable.symbol_pdf);
        return view;
    }
}
