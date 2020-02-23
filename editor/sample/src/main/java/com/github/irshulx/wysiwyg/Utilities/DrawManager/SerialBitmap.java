package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SerialBitmap implements Serializable {

    private Bitmap bitmap;
    transient boolean isUsed;
    int id;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public SerialBitmap(Bitmap bitmap, int id)
    {
        this.bitmap = bitmap;
        isUsed = false;
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void recycle() {
        bitmap.recycle();
    }
    private void writeObject(java.io.ObjectOutputStream out){
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            out.writeInt(byteArray.length);
            out.write(byteArray);
            stream.close();
        }
        catch (Exception e){
            Log.e("writeObject",e.getMessage());
        }

    }

    private void readObject(java.io.ObjectInputStream in)  {
        try {
            int bufferLength = in.readInt();
            byte[] byteArray = new byte[bufferLength];
            int pos = 0;
            do {
                int read = in.read(byteArray, pos, bufferLength - pos);

                if (read != -1) {
                    pos += read;
                } else {
                    break;
                }

            } while (pos < bufferLength);


            BitmapManager bitmapManager = BitmapManager.getInstance();
            Log.e("여긴가", bitmapManager.num + "");
            SerialBitmap tmpBitmap = bitmapManager.getUnUsingBitmap();
            Log.e("여긴가", bitmapManager.num + "?????");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inBitmap = tmpBitmap.getBitmap();
            tmpBitmap.setBitmap(BitmapFactory.decodeByteArray(byteArray, 0, bufferLength, options));
            bitmap = tmpBitmap.getBitmap();
        }
        catch(Exception e){
            Log.e("readObject", e.getMessage());
        }
    }

    public boolean isRecycled(){
        return bitmap.isRecycled();
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
}