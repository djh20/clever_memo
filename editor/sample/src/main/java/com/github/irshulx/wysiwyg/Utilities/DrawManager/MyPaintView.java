package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.github.irshulx.wysiwyg.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;


public class MyPaintView extends android.support.v7.widget.AppCompatImageView{
    private static final float TOUCH_TOLERANCE = 4;
    DrawContaioner drawContaioner;
    PaintConfig pc = PaintConfig.getInstance();
    boolean eraseTest = false;
    Paint drawPaint = new Paint();
    Canvas mCanvas;


    public boolean isZoomed() {
        return isZoomed;
    }

    public void setZoomed(boolean zoomed) {
        isZoomed = zoomed;
    }

    int viewId;
    boolean highlightFlag = false;
    boolean undoFlag = false;
    boolean touchFlag = false;
    boolean isModified = false;
    boolean isZoomed=  false;

    public boolean isModified() {
        return isModified;
    }
    public void clear(){
        touchFlag = false;
        mCanvas = null;
        highlightFlag = false;
        undoFlag = false;
        drawPaint = null;
        drawContaioner = null;
        eraseTest = false;
    }

    public void lastTouch(){
        touchFlag = true;
    }
    public void noTouch(){
        touchFlag = false;
    }

    public boolean isCleared(){
        if(drawContaioner == null && mCanvas == null)
            return true;
        return false;
    }

    public int getViewId() {
        return viewId;
    }

    public void setDrawContaioner(DrawContaioner drawContaioner) {
        this.drawContaioner = drawContaioner;
        if(drawContaioner.getCanvasBit() != null)
            mCanvas = new Canvas(drawContaioner.getCanvasBit().getBitmap());
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }


    public MyPaintView(Context context, AttributeSet attributeSet, int viewId) {
        super(context, attributeSet);
        this.viewId = viewId;
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        drawPaint = pc.getmPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(drawContaioner !=null)
            setMeasuredDimension(drawContaioner.getWidth(),drawContaioner.getHeight());

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("사이브변경?", w + " " + h + " " + oldw + " " + oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("끄림", "그림");
        if (drawContaioner != null && drawContaioner.getCanvasBit() != null && touchFlag) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if(undoFlag) {
                drawContaioner.setTempColor(drawPaint.getColor());
                drawContaioner.setTempStroke(drawPaint.getStrokeMiter());
                mCanvas.drawBitmap(drawContaioner.getCanvasBit().getBitmap(),0,0,pc.getErasePaint());
                int i = 0;
                for (Path p : drawContaioner.getPaths()) {
                    Log.e("onDraw", "있던 거 그림");
                    pc.mPaintSetColor(drawContaioner.getPaths().get(i).color);
                    pc.mPaintSetStroke(drawContaioner.getPaths().get(i).stroke);
                    canvas.drawPath(p, drawPaint);
                    mCanvas.drawPath(p, drawPaint);
                    i++;
                }
                pc.mPaintSetColor(drawContaioner.getTempColor());
                pc.mPaintSetStroke(drawContaioner.getTempStroke());
            }
            else{
                canvas.drawBitmap(drawContaioner.getCanvasBit().getBitmap(), 0, 0, null);
                canvas.drawPath(drawContaioner.getmPath(), drawPaint);
            }
        }
        undoFlag = false;
    }

    public void setAlpha(int alpha){
        pc.mPaintSetAlpha(alpha);
    }

    public void highlight(){
        highlightFlag = !highlightFlag;
        if(highlightFlag)
            pc.mPaintSetAlpha(60);
        else
            pc.mPaintSetAlpha(255);
    }

    public void onClickUndo () {
        Log.e("undosize",drawContaioner.getPaths().size()+"");
        if(eraseTest)
            setEraseMode();
        if (drawContaioner.getPaths().size()>0) {
            mCanvas.setBitmap(drawContaioner.getCanvasBit().getBitmap());
            undoFlag=true;
            drawContaioner.getUndonePaths().add(drawContaioner.getPaths().remove(drawContaioner.getPaths().size()-1));
            invalidate();

        }else{
        }
    }

    public void setEraseMode(){

        eraseTest = !eraseTest;
        if(eraseTest){
           drawPaint = pc.getErasePaint();
        }
        else {
            drawPaint = pc.getmPaint();
        }
    }

    public void onClickRedo (){
        if(eraseTest)
            setEraseMode();
        if (drawContaioner.getUndonePaths().size()>0){
//            drawContaioner.setCanvasBit(drawContaioner.getPureBit());
            mCanvas.setBitmap(drawContaioner.getCanvasBit().getBitmap());
            undoFlag=true;
            drawContaioner.getPaths().add(drawContaioner.getUndonePaths().remove(drawContaioner.getUndonePaths().size()-1));
            invalidate();
        }else {
        }
    }

    public void callDraw(){
        postInvalidate();
    }


    public static float getTouchTolerance() {
        return TOUCH_TOLERANCE;
    }

    public PaintConfig getPc() {
        return pc;
    }

    public void setPc(PaintConfig pc) {
        this.pc = pc;
    }

    public boolean isEraseTest() {
        return eraseTest;
    }

    public void setEraseTest(boolean eraseTest) {
        this.eraseTest = eraseTest;
    }

    public Paint getDrawPaint() {
        return drawPaint;
    }

    public void setDrawPaint(Paint drawPaint) {
        this.drawPaint = drawPaint;
    }

    public Canvas getmCanvas() {
        return mCanvas;
    }

    public void setmCanvas(Canvas mCanvas) {
        this.mCanvas = mCanvas;
    }

    public boolean isHighlightFlag() {
        return highlightFlag;
    }

    public void setHighlightFlag(boolean highlightFlag) {
        this.highlightFlag = highlightFlag;
    }

    public boolean isUndoFlag() {
        return undoFlag;
    }

    public void setUndoFlag(boolean undoFlag) {
        this.undoFlag = undoFlag;
    }

    public boolean isTouchFlag() {
        return touchFlag;
    }

    public void setTouchFlag(boolean touchFlag) {
        this.touchFlag = touchFlag;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.e("현재페이지", drawContaioner.getPageNum() + "");

        float x = event.getX();
        float y = event.getY();
        if(pc.isZoomed() == true){
            Log.e("줌인변경", "");
            x = event.getX() + getScaleX();
            y = event.getY() + getScaleY();
        }

        Log.e("x", event.getX() + " " + x);
        Log.e("x", event.getY() + " " + y);

        if(event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER){
        if(touchFlag && drawContaioner != null) {
            if(isModified == false)
                isModified = true;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.e("down", " exe");
                    drawContaioner.getUndonePaths().clear();
                    drawContaioner.getmPath().reset();
                    drawContaioner.getmPath().moveTo(x, y);
//                        drawContaioner.getmPath().x.add(x);
//                        drawContaioner.getmPath().y.add(y);
                    drawContaioner.setmX(x);
                    drawContaioner.setmY(y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.e("move", " exe");
                    float dx = Math.abs(x - drawContaioner.getmX());
                    float dy = Math.abs(y - drawContaioner.getmY());
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        drawContaioner.getmPath().quadTo(drawContaioner.getmX(), drawContaioner.getmY(), (x + drawContaioner.getmX()) / 2, (y + drawContaioner.getmY()) / 2);
                        drawContaioner.setmX(x);
                        drawContaioner.setmY(y);
                    }
//                       drawContaioner.getmPath().x.add(x);
//                       drawContaioner.getmPath().y.add(y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    Log.e("up", " exe");
                    drawContaioner.getmPath().lineTo(drawContaioner.getmX(), drawContaioner.getmY());
                    drawContaioner.getmPath().color = drawPaint.getColor();
                    drawContaioner.getmPath().stroke = drawPaint.getStrokeMiter();
                    if (!eraseTest)
                        drawContaioner.getPaths().add(drawContaioner.getmPath());
                    Log.e("사이즈", drawContaioner.getPaths().size() + "");
                    mCanvas.drawPath(drawContaioner.getmPath(), drawPaint);
                    drawContaioner.setmPath(new CusmtomPath());
                    invalidate();
                    break;
            }
        }
        }
        return true;
    }
    public void setStrokeWidth(int width){
        pc.mPaintSetStroke(width);
    }




    public void setColor(int color){
        pc.mPaintSetColor(color);

    }

    public void saveCanvas(){


    }

    public SerialBitmap getSerialBitMap(){
        return drawContaioner.getmBit();
    }

    public DrawContaioner getDrawContaioner() {
        return drawContaioner;
    }

}



