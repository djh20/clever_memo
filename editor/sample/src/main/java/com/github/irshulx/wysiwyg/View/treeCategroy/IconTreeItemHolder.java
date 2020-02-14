package com.github.irshulx.wysiwyg.View.treeCategroy;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.irshulx.wysiwyg.R;
import com.unnamed.b.atv.model.TreeNode;

public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    public TextView tvValue;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.arrowdown);
        if(!value.isArrow)
            tvValue.setCompoundDrawables(null,null,null,null);

        tvValue.setText(value.text);
        tvValue.setTypeface(null, Typeface.BOLD);


        return view;
    }


    public static class IconTreeItem {
        public String text;
        public boolean isArrow;

        //여기를 커스텀해서 이미지나 기타등등을 넣을 수 있다..
        public IconTreeItem(String text,boolean isArrow) {
            this.text = text;
            this.isArrow = isArrow;
        }
    }
}
