package com.github.irshulx.wysiwyg.Model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;

public class Noun implements Comparable<Noun>, Serializable {
    String name;
    int cnt;

    public Noun(String name) {
        super();
        this.name = name;
        this.cnt = 1;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCnt() {
        return cnt;
    }
    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
    public void increaseCnt() {
        cnt++;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int compareTo(Noun o) {
        return Integer.compare(cnt, o.cnt)*-1;
    }
}