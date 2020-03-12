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
    Paint erasePaint;
    boolean eraseFlag = false;
    boolean isModified = false;

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public void clear(){
        drawContaioner.mBitmapUnUse();
        drawContaioner = null;
        eraseFlag = false;
        erasePaint = null;
    }

    public boolean isCleared(){
        if(drawContaioner == null && erasePaint == null)
            return true;
        return false;
    }

    public void setupToUse(Context context){
        erasePaint = new Paint();
        erasePaint.setAntiAlias(true);
        erasePaint.setDither(true);
        erasePaint.setColor(context.getColor(R.color.lightGray));
        erasePaint.setStyle(Paint.Style.STROKE);
        erasePaint.setStrokeJoin(Paint.Join.ROUND);
        erasePaint.setStrokeCap(Paint.Cap.ROUND);
        erasePaint.setStrokeWidth(20);
    }

    public MyPaintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

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
        if(drawContaioner != null && drawContaioner.getmBit() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
            drawContaioner.setTempColor(drawContaioner.getmPaint().getColor());
            drawContaioner.setTempStroke(drawContaioner.getmPaint().getStrokeWidth());
            int i = 0;
            for (Path p : drawContaioner.getPaths()) {
                Log.e("onDraw", "있던 거 그림");
                drawContaioner.getmPaint().setColor(drawContaioner.getPaths().get(i).color);
                drawContaioner.getmPaint().setStrokeWidth(drawContaioner.getPaths().get(i).stroke);
                canvas.drawPath(p, drawContaioner.getmPaint());
                i++;
            }
            drawContaioner.getmPaint().setColor(drawContaioner.getTempColor());
            drawContaioner.getmPaint().setStrokeWidth(drawContaioner.getTempStroke());
//            if(eraseFlag){
//                canvas.drawPath(drawContaioner.getErasePath(),erasePaint);
//            }
            canvas.drawPath(drawContaioner.getmPath(), drawContaioner.getmPaint());
        }
    }

    public void onClickUndo () {
        if (drawContaioner.getPaths().size()>0) {
            drawContaioner.getUndonePaths().add(drawContaioner.getPaths().remove(drawContaioner.getPaths().size()-1));
            invalidate();
        }else{
        }

    }

    public void setEraseMode(){
        eraseFlag = !eraseFlag;
        if(eraseFlag == true) {
            drawContaioner.setmPaint(new Paint(Paint.DEV_KERN_TEXT_FLAG));
//            drawContaioner.getmPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            drawContaioner.getmPaint().setAntiAlias(true);
            drawContaioner.getmPaint().setDither(true);
            drawContaioner.getmPaint().setColor(Color.TRANSPARENT);
            drawContaioner.getmPaint().setStyle(Paint.Style.STROKE);
            drawContaioner.getmPaint().setStrokeJoin(Paint.Join.ROUND);
            drawContaioner.getmPaint().setStrokeCap(Paint.Cap.ROUND);
            drawContaioner.getmPaint().setStrokeWidth(20);
        }
    }

    public void onClickRedo (){
        if (drawContaioner.getUndonePaths().size()>0){
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


        if(event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER)
        {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("down" , " exe");
                        drawContaioner.getUndonePaths().clear();
                        drawContaioner.getmPath().reset();
                        drawContaioner.getmPath().moveTo(x, y);
                        drawContaioner.getmPath().x.add(x);
                        drawContaioner.getmPath().y.add(y);
                        drawContaioner.setmX(x);
                        drawContaioner.setmY(y);
                        invalidate();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.e("move" , " exe");
                        float dx = Math.abs(x - drawContaioner.getmX());
                        float dy = Math.abs(y - drawContaioner.getmY());
                        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                           drawContaioner.getmPath().quadTo(drawContaioner.getmX(), drawContaioner.getmY(), (x + drawContaioner.getmX()) / 2, (y + drawContaioner.getmY()) / 2);
                           drawContaioner.setmX(x);
                            drawContaioner.setmY(y);
                        }
                       drawContaioner.getmPath().x.add(drawContaioner.getmX());
                        drawContaioner.getmPath().y.add(drawContaioner.getmY());
                        drawContaioner.getmPath().x.add((x + drawContaioner.getmX()) / 2);
                        drawContaioner.getmPath().y.add((y + drawContaioner.getmY()) / 2);
                        invalidate();
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.e("up" , " exe");
                       drawContaioner.getmPath().lineTo(drawContaioner.getmX(), drawContaioner.getmY());
                       drawContaioner.getmPath().color = drawContaioner.getmPaint().getColor();
                       drawContaioner.getmPath().stroke = drawContaioner.getmPaint().getStrokeWidth();
                        drawContaioner.getPaths().add(drawContaioner.getmPath());
                       drawContaioner.setmPath(new CusmtomPath());
                       drawContaioner.getmPath().x.add(x);
                       drawContaioner.getmPath().y.add(y);
                        invalidate();
                        break;
                }


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
//                        drawContaioner.getErasePath().x.add(drawContaioner.getmX());
//                        drawContaioner.getErasePath().y.add(drawContaioner.getmY());
//                        drawContaioner.getErasePath().x.add((x + drawContaioner.getmX()) / 2);
//                        drawContaioner.getErasePath().y.add((y + drawContaioner.getmY()) / 2);
//                        invalidate();
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        drawContaioner.getErasePath().lineTo(drawContaioner.getmX(), drawContaioner.getmY());
//                        drawContaioner.getErasePath().x.add(x);
//                        drawContaioner.getErasePath().y.add(y);
//                        erase();
//                        invalidate();
//                        invalidate();
//                        break;
//                }
//            }
            return true;
        }
        return true;
    }
    public void setStrokeWidth(int width){
        drawContaioner.getmPaint().setStrokeWidth(width);
    }

    public void erase(){

        for(int i = 0 ; i < drawContaioner.getPaths().size() ; i++){
            CusmtomPath path = drawContaioner.getPaths().get(i);
            for(int j = 0 ; j < drawContaioner.getErasePath().x.size() ; j++){
                float x = drawContaioner.getErasePath().x.get(j);
                float y = drawContaioner.getErasePath().y.get(j);
                for(int z = 0 ; z < path.x.size() ; z++){
                    float exist_x = path.x.get(z);
                    float exist_y = path.y.get(z);
                    if((x - 20 <= exist_x && exist_x <= x+20)&& (y -20 <= exist_y && exist_y <= y +20))
                        drawContaioner.getPaths().remove(path);
                    Log.e("exist_X", exist_x + " ");
                    Log.e("exist_Y", exist_y + " ");
                    Log.e("x", x + "");
                    Log.e("y", y + "");
                }
            }
        }
    }


    public void setColor(int color){
        drawContaioner.getmPaint().setColor(color);

    }

    public void saveCanvas(){


    }

    public SerialBitmap getSerialBitMap(){
        return drawContaioner.getmBit();
    }

    public DrawContaioner getDrawContaioner() {
        return drawContaioner;
    }

    public void setDrawContaioner(DrawContaioner drawContaioner) {
        this.drawContaioner = drawContaioner;
    }

}

class CusmtomPath extends Path implements Serializable{

    Vector<Float> x;
    Vector<Float> y;
    int color;
    float stroke;
    private ArrayList<PathAction> actions = new ArrayList<PathAction>();

    CusmtomPath() {
        super();
        x = new Vector<Float>();
        y = new Vector<Float>();
    }

    CusmtomPath(CusmtomPath cusmtomPath){
        super(cusmtomPath);
        this.x = cusmtomPath.x;
        this.y = cusmtomPath.y;
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



