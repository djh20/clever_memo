package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.DrawableContainer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.github.irshulx.wysiwyg.NLP.MemoLoadManager;
import com.github.irshulx.wysiwyg.R;

import java.util.ArrayList;



public class PaintViewManager {

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }



    int lastId = 0;
    int LastTouchViewID;
    RelativeLayout scroll;
    ArrayList<MyPaintView> myPaintViewPool = new ArrayList<MyPaintView>();


    public int getLastTouchViewID() {
        return LastTouchViewID;
    }

    public void setLastTouchViewID(int lastTouchViewID) {
        LastTouchViewID = lastTouchViewID;
    }

    public PaintViewManager(Context context, RelativeLayout scroll, int numPage ,int width, int height) {
        for(int i = 0 ; i < numPage ; i++) {
            final MyPaintView myPaintView = new MyPaintView(context, null, i+1);
            myPaintView.setMinimumHeight(width);
            myPaintView.setMinimumWidth(height);
            myPaintView.setId(i +1);
            myPaintView.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e("touchtouch","tocuh");
                    LastTouchViewID = myPaintView.getViewId();
                    for(MyPaintView p : myPaintViewPool){
                        if(LastTouchViewID != p.getViewId()){
                            p.noTouch();
                        }
                        else
                            p.lastTouch();
                    }
                    return false;
                }
            });
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            if(i != 0)
                layoutParams.addRule(RelativeLayout.BELOW, i);
            lastId = i+1;
            myPaintView.setLayoutParams(layoutParams);
            this.scroll =scroll;
            scroll.addView(myPaintView);
            myPaintViewPool.add(myPaintView);
        }


    }

    public void invalidateAll(){
        for(int i = 0 ; i < myPaintViewPool.size() ; i++){
            MyPaintView myPaintView = myPaintViewPool.get(i);
            if(myPaintView.getDrawContaioner() != null){
                myPaintView.invalidate();
            }
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
    }

    public MyPaintView getMyPaintViewById(int id){
        for(int i = 0 ; i < myPaintViewPool.size() ; i++) {
            MyPaintView myPaintView = myPaintViewPool.get(i);
            if(myPaintView.getId() == id)
                return myPaintView;
        }
        return null;
    }


    public void replace(MyPaintView removeView, Context context) {
        final MyPaintView myPaintView = new MyPaintView(context, null, removeView.getViewId());
        myPaintView.setMinimumHeight(removeView.getHeight());
        myPaintView.setMinimumWidth(removeView.getWidth());
        myPaintView.setId(removeView.getId());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, removeView.getId()-1);
        myPaintView.setLayoutParams(layoutParams);
        scroll.addView(myPaintView);
        scroll.removeView(removeView);
        myPaintViewPool.remove(removeView);
        myPaintViewPool.add(myPaintView);
        myPaintView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("touchtouch","tocuh");
                LastTouchViewID = myPaintView.getViewId();
                for(MyPaintView p : myPaintViewPool){
                    if(LastTouchViewID != p.getViewId()){
                        p.noTouch();
                    }
                    else
                        p.lastTouch();
                }
                return false;
            }
        });

    }
}
