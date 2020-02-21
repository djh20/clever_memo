package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

public class BitmapManager {
    private  final int NUM_BITMAP = 20;
    private static BitmapManager bitmapManager = null;
    private Vector<SerialBitmap> bitmapPool;
    int num = 0;
    private BitmapManager(){
        bitmapPool = new Vector<SerialBitmap>();
        Bitmap.Config conf = Bitmap.Config.RGB_565;
        for ( int i = 0 ; i < NUM_BITMAP ; i++) {
            bitmapPool.add(new SerialBitmap(null));
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

    public void setUnUse(Bitmap bitmap){
        SerialBitmap serialBitmap = null;
        for(int i = 0 ; i < bitmapPool.size() ; i++){
            serialBitmap= bitmapPool.get(i);
            if(serialBitmap.getBitmap() == bitmap)
                break;
        }
        setUnUse(serialBitmap);
    }

    public void setUnUse(SerialBitmap bitmap){
        if(bitmap != null) {
            if (!bitmap.isRecycled())
                bitmap.recycle();
            bitmap.setBitmap(null);
            bitmap.setUsed(false);
            Log.e("사용안함 설정", "설정");
        }
    }

    public void clearAll(){
        for(int i = 0 ; i <bitmapPool.size() ; i++){
            SerialBitmap serialBitmap = bitmapPool.get(i);
            if(serialBitmap.getBitmap() != null)
                setUnUse(serialBitmap);
        }
    }
}
