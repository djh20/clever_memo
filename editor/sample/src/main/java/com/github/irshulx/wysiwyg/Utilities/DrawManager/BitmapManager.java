package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

public class BitmapManager {
    private  final int NUM_565_BITMAP = 200;
    private  final int NUM_8888_BITMAP = 200;
    private static BitmapManager bitmapManager = null;
    private Vector<SerialBitmap> bitmapPool_565;
    private Vector<SerialBitmap> bitmapPool_8888;
    int num = 0;
    private BitmapManager(){
        bitmapPool_565 = new Vector<SerialBitmap>();
        bitmapPool_8888 = new Vector<SerialBitmap>();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        for ( int i = 0 ; i < NUM_565_BITMAP ; i++) {
            bitmapPool_565.add(new SerialBitmap(null));
        }
        for ( int i = 0 ; i < NUM_8888_BITMAP ; i++) {
            bitmapPool_8888.add(new SerialBitmap(null));
        }
    }

    static public BitmapManager getInstance(){
        if(bitmapManager == null)
            bitmapManager = new BitmapManager();

        return bitmapManager;
    }

    public int isExist(SerialBitmap bitmap){
        return bitmapPool_565.indexOf(bitmap);
    }

    public SerialBitmap getUnUsingBitmap_565(){
        synchronized (bitmapPool_565) {
            while (true) {
                try {
                    for (int i = 0; i < bitmapPool_565.size(); i++) {
                        SerialBitmap bitmap = bitmapPool_565.get(i);
                        if (bitmap.getBitmap() == null && bitmap.isUsed() == false) {
//                            Log.e("비트맵 부여", "ㅇㅁㄴㅇㄴ");
                            bitmap.setUsed(true);num++;
//                            Log.e("부여횟수", num+"");
                            return bitmap;
                        }
//                        Log.e("bitmaptest", i + "");
                    }
                } catch (Exception e) {
                    Log.e("zz", e.getMessage());
                }
            }
        }
    }

    public SerialBitmap getUnUsingBitmap_8888(){
        synchronized (bitmapPool_565) {
            while (true) {
                try {
                    for (int i = 0; i < bitmapPool_8888.size(); i++) {
                        SerialBitmap bitmap = bitmapPool_8888.get(i);
                        if (bitmap.getBitmap() == null && bitmap.isUsed() == false) {
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

    public void setUnUse_8888(Bitmap bitmap){
        SerialBitmap serialBitmap = null;
        for(int i = 0 ; i < bitmapPool_8888.size() ; i++){
            serialBitmap= bitmapPool_8888.get(i);
            if(serialBitmap.getBitmap() == bitmap)
                break;
        }
        setUnUse_565(serialBitmap);
    }

    public void setUnUse_8888(SerialBitmap bitmap){
        if(bitmap != null) {
            if (!bitmap.isRecycled())
                bitmap.recycle();
            bitmap.setBitmap(null);
            bitmap.setUsed(false);
            Log.e("사용안함 설정", "설정");
        }
    }

    public void clearAll_8888(){
        for(int i = 0 ; i <bitmapPool_8888.size() ; i++){
            SerialBitmap serialBitmap = bitmapPool_8888.get(i);
            if(serialBitmap.getBitmap() != null)
                setUnUse_8888(serialBitmap);
        }
    }









    public void setUnUse_565(Bitmap bitmap){
        SerialBitmap serialBitmap = null;
        for(int i = 0 ; i < bitmapPool_565.size() ; i++){
            serialBitmap= bitmapPool_565.get(i);
            if(serialBitmap.getBitmap() == bitmap)
                break;
        }
        setUnUse_565(serialBitmap);
    }

    public void setUnUse_565(SerialBitmap bitmap){
        if(bitmap != null) {
            if (!bitmap.isRecycled())
                bitmap.recycle();
            bitmap.setBitmap(null);
            bitmap.setUsed(false);
            Log.e("사용안함 설정", "설정");
        }
    }

    public void clearAll_565(){
        for(int i = 0 ; i <bitmapPool_565.size() ; i++){
            SerialBitmap serialBitmap = bitmapPool_565.get(i);
            if(serialBitmap.getBitmap() != null)
                setUnUse_565(serialBitmap);
        }
    }
}
