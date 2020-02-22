package com.github.irshulx.wysiwyg.Model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class SimilariryResult  implements Comparable<SimilariryResult>, Serializable {
    private Object object;
    private Object compareObejct;
    private double similarity;
    private int isReverse;
    public SimilariryResult(Object object, Object compareObject, double similarity, int isReverse) {
        this.object = object;
        this.compareObejct = compareObject;
        this.similarity = similarity;
        this.isReverse = isReverse;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getCompareObejct() {
        return compareObejct;
    }

    public void setCompareObejct(Object compareObejct) {
        this.compareObejct = compareObejct;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public int getIsReverse() {
        return isReverse;
    }

    public void setIsReverse(int isReverse) {
        this.isReverse = isReverse;
    }

    @Override
    public int compareTo(@NonNull SimilariryResult similariryResult) {
        return Double.compare(similarity, similariryResult.similarity)*isReverse;
    }
}
