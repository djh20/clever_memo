package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.DrawableContainer;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.irshulx.wysiwyg.NLP.MemoLoadManager;

import java.util.ArrayList;

public class PaintViewManager {
    int lastId = 0;
    RelativeLayout scroll;
    ArrayList<MyPaintView> myPaintViewPool = new ArrayList<MyPaintView>();


    public PaintViewManager(Context context, RelativeLayout scroll, int numPage ,int width, int height) {
        for(int i = 0 ; i < numPage ; i++) {
            MyPaintView myPaintView = new MyPaintView(context, null);
            myPaintView.setMinimumHeight(width);
            myPaintView.setMinimumWidth(height);
            myPaintView.setupToUse(context);
            myPaintView.setId(i+1);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if(i != 0)
                layoutParams.addRule(RelativeLayout.BELOW, i);
            lastId = i+1;
            myPaintView.setLayoutParams(layoutParams);
            this.scroll =scroll;
            scroll.addView(myPaintView);
            myPaintViewPool.add(myPaintView);
        }
    }

    public ArrayList<MyPaintView> getMyPaintViewPool() {
        return myPaintViewPool;
    }

    public void setBitmapItSelfById(int id){
        MyPaintView myPaintView = getMyPaintViewById(id);
        myPaintView.setImageBitmap(myPaintView.getDrawContaioner().getmBit().getBitmap());
    }

    public void setContainerById(int id, DrawContaioner drawContaioner){
        MyPaintView myPaintView = getMyPaintViewById(id);
        myPaintView.setDrawContaioner(drawContaioner);
    }

    public void setupToUseById(int id, Context context){
        MyPaintView myPaintView = getMyPaintViewById(id);
        myPaintView.setupToUse(context);
    }

    public MyPaintView getMyPaintViewById(int id){
        for(int i = 0 ; i < myPaintViewPool.size() ; i++) {
            MyPaintView myPaintView = myPaintViewPool.get(i);
            if(myPaintView.getId() == id)
                return myPaintView;
        }
        return null;
    }

    public void remove(MyPaintView myPaintView){
        myPaintViewPool.remove(myPaintView);
    }


    public MyPaintView getPaintViewByPageNum(int pageNum){
        MyPaintView myPaintView = null;
        for(int i = 0 ; i < myPaintViewPool.size() ; i++) {
            MyPaintView tmpMyPaintView = myPaintViewPool.get(i);
            if(tmpMyPaintView.getDrawContaioner() != null)
                Log.e("correct", pageNum +" " +tmpMyPaintView.getDrawContaioner().getPageNum());
                if(tmpMyPaintView.getDrawContaioner().getPageNum() == pageNum) {

                    myPaintView = tmpMyPaintView;
                }

        }

        return myPaintView;
    }


    public void replace(MyPaintView removeView, Context context) {
        MyPaintView myPaintView = new MyPaintView(context, null);
        myPaintView.setMinimumHeight(removeView.getHeight());
        myPaintView.setMinimumWidth(removeView.getWidth());
        myPaintView.setId(removeView.getId());
        myPaintView.setupToUse(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, removeView.getId()-1);
        myPaintView.setLayoutParams(layoutParams);
        scroll.addView(myPaintView);
        scroll.removeView(removeView);
        myPaintViewPool.remove(removeView);
        myPaintViewPool.add(myPaintView);

    }
}
