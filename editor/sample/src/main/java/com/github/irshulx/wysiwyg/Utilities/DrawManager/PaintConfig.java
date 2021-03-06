package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class PaintConfig {

    private Paint erasePaint;
    private Paint mPaint;

    private static PaintConfig paintConfig = null;

    private PaintConfig(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        erasePaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        erasePaint.setAntiAlias(true);
        erasePaint.setDither(true);
        erasePaint.setColor(Color.TRANSPARENT);
        erasePaint.setStyle(Paint.Style.STROKE);
        erasePaint.setStrokeJoin(Paint.Join.ROUND);
        erasePaint.setStrokeCap(Paint.Cap.ROUND);
        erasePaint.setStrokeWidth(20);
    }

    public static PaintConfig getInstance(){
        if(paintConfig == null)
            paintConfig = new PaintConfig();
        return  paintConfig;
    }

    public Paint getErasePaint() {
        return erasePaint;
    }

    public void setErasePaint(Paint erasePaint) {
        this.erasePaint = erasePaint;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public void mPaintSetColor(int color){
        mPaint.setColor(color);
    }

    public void mPaintSetStroke(float stroke){
        mPaint.setStrokeWidth(stroke);
    }

    public void mPaintSetAlpha(int alpha){
        mPaint.setAlpha(alpha);
    }

    public void erasePaintSetStroke(float stroke){
        erasePaint.setStrokeWidth(stroke);
    }
}
