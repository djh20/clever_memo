package com.github.irshulx.wysiwyg.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;


    //테이블
    private static final String TABLE_MEMO = "MEMO";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("database", "생성중");

        db.execSQL("Create TABLE IF NOT EXISTS Category ("
                + "name CHAR(20) PRIMARY KEY NOT NULL,"
                + "parent CHAR(20),"
                + "cntMemo INT)"
        );

        db.execSQL("Create TABLE IF NOT EXISTS TempImage ("
                + "path CHAR(20) PRIMARY KEY NOT NULL)"
        );

        db.execSQL("Create TABLE IF NOT EXISTS Memo ("
                + "memoIndex INT PRIMARY KEY NOT NULL,"
                + "memmName CHAR(20) NOT NULL,"
                + "category CHAR(20),"
                + "updateDate CHAR(40),"
                + "addedDate CHAR(20),"
                +  "pageNum INT,"
                + "imagePath TEXT)"
        );

        db.execSQL("Create TABLE IF NOT EXISTS Tf ("
                + "memoIndex INT PRIMARY KEY NOT NULL,"
                + "word CHAR(20) NOT NULL,"
                + "tf INT)"
        );

        db.execSQL("Create TABLE IF NOT EXISTS Word ("
                + "word CHAR(20) PRIMARY KEY NOT NULL,"
                + "globalFrequency INT NOT NULL,"
                + "docFrequency INT,"
                + "idf DOUBLE)"
        );

    }

    public void insertSQL(String sql){
        getWritableDatabase().execSQL(sql);

    }

    public void selectSQL(String sql){
        Log.e("진입", "셀렉 진입");
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){
            Log.e("sql" , c.getString(0));
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}