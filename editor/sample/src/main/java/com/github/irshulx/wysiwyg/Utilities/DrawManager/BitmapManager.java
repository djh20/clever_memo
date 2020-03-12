package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

public class BitmapManager {
    private  final int NUM_BITMAP = 30;
    private static BitmapManager bitmapManager = null;
    private Vector<SerialBitmap> bitmapPool;
    int num = 0;
    private BitmapManager(){
        bitmapPool = new Vector<SerialBitmap>();
        for ( int i = 0 ; i < NUM_BITMAP ; i++) {
            SerialBitmap serialBitmap = new SerialBitmap(null, i);
            bitmapPool.add(serialBitmap);
        }
    }

    static public BitmapManager getInstance(){
        if(bitmapManager == null)
            bitmapManager = new BitmapManager();

        return bitmapManager;
    }

    public int isExist(SerialBitmap bitmap){
        return bitmapPool.indexOf(bitmap);
    }

    public SerialBitmap getUnUsingBitmap(){
        synchronized (bitmapPool) {
            while (true) {
                try {
                    for (int i = 0; i < bitmapPool.size(); i++) {
                        SerialBitmap bitmap = bitmapPool.get(i);
                        if (bitmap.isUsed() == false) {
                            bitmap.setUsed(true);num++;
                            return bitmap;
                        }
                    }
                } catch (Exception e) {
                    Log.e("zz", e.getMessage());
                }
            }
        }
    }

    public void setUnUse(Bitmap bitmap,boolean isReUseMode ){
        SerialBitmap serialBitmap = null;
        for(int i = 0 ; i < bitmapPool.size() ; i++){
            serialBitmap= bitmapPool.get(i);
            if(serialBitmap.getBitmap() == bitmap)
                break;

            Log.e("Dasdd", "dsadasd");
        }
        setUnUse(serialBitmap, isReUseMode);
    }

    public void setUnUse(SerialBitmap bitmap, boolean isReUseMode){
        if(isReUseMode == true)
            bitmap.setUsed(false);
        else if(isReUseMode == false) {
            if (bitmap.getBitmap() != null && !bitmap.isRecycled())
                bitmap.recycle();
            bitmap.setBitmap(null);
            bitmap.setUsed(false);
            Log.e("사용안함 설정", "설정");
        }

        Log.e("사용안함 설정", bitmap.id +"의 사용안함설정을 실시 reuseMode = "  + isReUseMode);
    }

    public void clearAll(){
        for(int i = 0 ; i <bitmapPool.size() ; i++){
            SerialBitmap serialBitmap = bitmapPool.get(i);
                setUnUse(serialBitmap, false);
        }
    }
}
