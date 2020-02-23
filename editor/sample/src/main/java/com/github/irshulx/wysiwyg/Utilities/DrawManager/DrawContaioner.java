package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class DrawContaioner implements Serializable {



    private CusmtomPath mPath;
    private SerialBitmap mBit;
    private SerialBitmap canvasBit;
    private ArrayList<CusmtomPath> paths = new ArrayList<CusmtomPath>();
    private ArrayList<CusmtomPath> undonePaths = new ArrayList<CusmtomPath>();
    private float mX, mY;
    private int pageNum;
    private int height;
    private int width;
    private boolean eraseMode = false;
    private CusmtomPath erasePath;
    private int tempColor;
    private float tempStroke;

    public void setDetail(SerialBitmap mBit, int pageNum, int pagew, int pageh){
        eraseMode = false;
        this.mBit = mBit;
        mPath = new CusmtomPath();
        width = pagew;
        height = pageh;
        this.pageNum = pageNum;
    }

    public DrawContaioner(){
    }



    public SerialBitmap getCanvasBit() {
        return canvasBit;
    }

    public void setCanvasBit(SerialBitmap canvasBit) {
        this.canvasBit = canvasBit;
    }

    public CusmtomPath getmPath() {
        return mPath;
    }

    public void setmPath(CusmtomPath mPath) {
        this.mPath = mPath;
    }


    public SerialBitmap getmBit() {
        return mBit;
    }

    public void setmBit(SerialBitmap mBit) {
        this.mBit = mBit;
    }

    public ArrayList<CusmtomPath> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<CusmtomPath> paths) {
        this.paths = paths;
    }

    public ArrayList<CusmtomPath> getUndonePaths() {
        return undonePaths;
    }

    public void setUndonePaths(ArrayList<CusmtomPath> undonePaths) {
        this.undonePaths = undonePaths;
    }

    public float getmX() {
        return mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
    }

    public float getmY() {
        return mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isEraseMode() {
        return eraseMode;
    }

    public void setEraseMode(boolean eraseMode) {
        this.eraseMode = eraseMode;
    }

    public CusmtomPath getErasePath() {
        return erasePath;
    }

    public void setErasePath(CusmtomPath erasePath) {
        this.erasePath = erasePath;
    }

    public int getTempColor() {
        return tempColor;
    }

    public void setTempColor(int tempColor) {
        this.tempColor = tempColor;
    }

    public float getTempStroke() {
        return tempStroke;
    }

    public void setTempStroke(float tempStroke) {
        this.tempStroke = tempStroke;
    }

}