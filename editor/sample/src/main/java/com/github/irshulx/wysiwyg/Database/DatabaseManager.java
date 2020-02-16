package com.github.irshulx.wysiwyg.Database;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Serializable;

//싱글톤 패턴
public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager databaseManager = null;

    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseManager getInstance(){
        return databaseManager;
    }

    public static DatabaseManager getInstance(Context context){
        if(databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create TABLE IF NOT EXISTS Category ("
                + "name CHAR(20) PRIMARY KEY NOT NULL,"
                + "parent CHAR(20),"
                + "cntMemo INT)"
        );

        db.execSQL("Create TABLE IF NOT EXISTS Memo ("
                + "memoIndex INT PRIMARY KEY NOT NULL,"
                + "memoName CHAR(20) NOT NULL,"
                + "category CHAR(20),"
                + "updateDate CHAR(40),"
                + "addedDate CHAR(20),"
                +  "pageNum INT,"
                + "imagePath TEXT)"
        );

        db.execSQL("Create TABLE IF NOT EXISTS Tf ("
                + "memoIndex INT NOT NULL,"
                + "word CHAR(20) NOT NULL,"
                + "tf INT," +
                "  PRIMARY KEY (memoIndex, word))"

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

    public Cursor selectSQL(String sql){
        return getReadableDatabase().rawQuery(sql, null);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}