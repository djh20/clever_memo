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

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public SerialBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        isUsed = false;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void recycle() {
        bitmap.recycle();
    }
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        out.writeInt(byteArray.length);
        out.write(byteArray);
        stream.close();

    }

    //TODO  8888 or 565 확인하고 리드해야함
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
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
        SerialBitmap tmpBitmap = bitmapManager.getUnUsingBitmap_565();
        tmpBitmap.setBitmap(BitmapFactory.decodeByteArray(byteArray, 0, bufferLength));
        bitmap = tmpBitmap.getBitmap();
    }

    public boolean isRecycled(){
        return bitmap.isRecycled();
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
}