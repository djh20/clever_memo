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
    int viewId;
    boolean highlightFlag = false;
    boolean undoFlag = false;
    boolean touchFlag = false;
    boolean isModified = false;

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

    @Override
    public boolean onTouchEvent(MotionEvent event){

        float x = event.getX();
        float y = event.getY();


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
//                       drawContaioner.getmPath().x.add(x);
//                       drawContaioner.getmPath().y.add(y);
                    invalidate();
                    break;
            }
        }
//
//            }
//            else{
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        drawContaioner.setErasePath( new CusmtomPath());
//                        drawContaioner.getErasePath().reset();
//                        drawContaioner.getErasePath().moveTo(x, y);
//                        drawContaioner.getErasePath().x.add(x);
//                        drawContaioner.getErasePath().y.add(y);
//                        drawContaioner.setmX(x);
//                        drawContaioner.setmY(y);
//                        invalidate();
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        float dx = Math.abs(x - drawContaioner.getmX());
//                        float dy = Math.abs(y - drawContaioner.getmY());
//                        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//                            drawContaioner.getErasePath().quadTo(drawContaioner.getmX(), drawContaioner.getmY(), (x + drawContaioner.getmX()) / 2, (y + drawContaioner.getmY()) / 2);
//                            drawContaioner.setmX(x);
//                            drawContaioner.setmY(y);
//                        }
//                        drawContaioner.getErasePath().x.add(x);
//                        drawContaioner.getErasePath().y.add(y);
//                        invalidate();
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        drawContaioner.getErasePath().lineTo(drawContaioner.getmX(), drawContaioner.getmY());
//                        drawContaioner.getErasePath().x.add(x);
//                        drawContaioner.getErasePath().y.add(y);
//                        erase();
//                        invalidate();
//                        drawContaioner.setEraseMode(false);
//                        invalidate();
//                        break;
//                }
//            }
//            return true;
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

class CusmtomPath extends Path implements Serializable{

//    Vector<Float> x;
//    Vector<Float> y;
    int color;
    float stroke;
    private ArrayList<PathAction> actions = new ArrayList<PathAction>();

    CusmtomPath() {
        super();
//        x = new Vector<Float>();
//        y = new Vector<Float>();
    }

    CusmtomPath(CusmtomPath cusmtomPath){
        super(cusmtomPath);
//        this.x = cusmtomPath.x;
//        this.y = cusmtomPath.y;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        drawThisPath();
    }


    @Override
    public void quadTo(float x1, float y1, float x2, float y2){
        actions.add(new ActionQuad(x1, y1, x2, y2));
        super.quadTo(x1, y1, x2, y2);
    }

    @Override
    public void moveTo(float x, float y) {
        actions.add(new ActionMove(x, y));
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y){
        actions.add(new ActionLine(x, y));
        super.lineTo(x, y);
    }


    private void drawThisPath(){
        for(PathAction p : actions){
            if(p.getType().equals(PathAction.PathActionType.MOVE_TO)){
                super.moveTo(p.getX(), p.getY());
            } else if(p.getType().equals(PathAction.PathActionType.LINE_TO)){
                super.lineTo(p.getX(), p.getY());
            } else if(p.getType().equals(PathAction.PathActionType.QUAD_TO)){
                ActionQuad aq = (ActionQuad) p;
                super.quadTo(aq.getX1(),aq.getY1(),aq.getX2(),aq.getY2());
            }
        }
    }

    public interface PathAction {
        public enum PathActionType {LINE_TO,MOVE_TO,QUAD_TO};
        public PathActionType getType();
        public float getX();
        public float getY();
    }

    public class ActionMove implements PathAction, Serializable{
        private static final long serialVersionUID = -7198142191254133295L;

        private float x,y;

        public ActionMove(float x, float y){
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.MOVE_TO;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

    }

    public class ActionLine implements PathAction, Serializable{
        private static final long serialVersionUID = 8307137961494172589L;

        private float x,y;

        public ActionLine(float x, float y){
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.LINE_TO;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

    }

    public class ActionQuad implements PathAction, Serializable{
        private static final long serialVersionUID = 8307137961494172589L;

        private float x1,y1,x2,y2;

        public ActionQuad(float x1, float y1, float x2, float y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.QUAD_TO;
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return 0;
        }

        public float getX1() {
            return x1;
        }

        public void setX1(float x1) {
            this.x1 = x1;
        }

        public float getY1() {
            return y1;
        }

        public void setY1(float y1) {
            this.y1 = y1;
        }

        public float getX2() {
            return x2;
        }

        public void setX2(float x2) {
            this.x2 = x2;
        }

        public float getY2() {
            return y2;
        }

        public void setY2(float y2) {
            this.y2 = y2;
        }
    }
}



