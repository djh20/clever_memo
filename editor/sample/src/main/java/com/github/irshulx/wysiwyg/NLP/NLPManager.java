package com.github.irshulx.wysiwyg.NLP;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.Model.Memo;
import com.github.irshulx.wysiwyg.Model.Noun;
import com.github.irshulx.wysiwyg.Model.Word;
import com.github.irshulx.wysiwyg.Utilities.RealPathUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

public class NLPManager extends AppCompatActivity implements Serializable {
    private static NLPManager nlpManager;
    private String imagePath;
    private MemoManager memoManager;
    private CategoryManager categoryManager;
    private Twitter twitter;

    private NLPManager(Twitter twitter) {
        memoManager = new MemoManager();
        categoryManager = new CategoryManager();
        this.twitter = twitter;
//        imagePath = getFilesDir().toString()+"/" + "memoImage/";
        imagePath = "test";
    }

    public static NLPManager getInstance(){
        return nlpManager;
    }

    public static NLPManager getInstance(Twitter twitter){
            if(nlpManager == null){
                nlpManager = new NLPManager(twitter);
            }
            return nlpManager;
    }

    public Vector<Noun> getResultFrequencyDataFromPdfFilePath(String filePath) throws FileNotFoundException {
            File file = new File(filePath);
            String result = "";
        PdfReader reader = null;
        try {
            reader = new PdfReader(new FileInputStream(file));
            int n = reader.getNumberOfPages();
            for(int i =0 ; i < n ; i++) {
                result += PdfTextExtractor.getTextFromPage(reader, i + 1) + "\n";
            }
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }

        return twitter.getNounsWithFrequency(result);
    }

    public boolean pushNewMemoAndResultFrequency(String memoName, Vector<Noun> nouns, int numPage){
        try{
            Memo memo = memoManager.createNewMemoAndReturn(memoName, imagePath, numPage);
            memoManager.pushWordAndMemoWordBag(nouns, memo);

            return true;
        }
        catch(Exception e){
            Log.e("Error", e.getMessage());
//            Toast.makeText(getApplicationContext(), "새로운 메모추가를 실패했습니다.", Toast.LENGTH_SHORT);
        }
        return false;
    }

    public void printMemoDB(){
           DatabaseManager databaseManager = DatabaseManager.getInstance();
           Cursor c = databaseManager.selectSQL("SELECT * FROM Memo");
           while(c.moveToNext()){
               Log.e("memo ",c.getInt(0) + "");
               Log.e("memo ",c.getString(1) + "");
               Log.e("memo ",c.getString(2) + "");
               Log.e("memo ",c.getString(3) + "");
               Log.e("memo ",c.getString(4) + "");
               Log.e("memo ",c.getInt(5) + "");
               Log.e("memo ",c.getString(6) + "");
           }
    }
    public void printWordDB(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Cursor c = databaseManager.selectSQL("SELECT * FROM Word");
        while(c.moveToNext()){
            Log.e("word ",c.getString(0) + "");
            Log.e("word ",c.getInt(1) + "");
            Log.e("word ",c.getInt(2) + "");
            Log.e("word ",c.getDouble(3) + "");
        }
    }

    public void printTfDB(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Cursor c = databaseManager.selectSQL("SELECT * FROM Tf");
        while(c.moveToNext()){
            Log.e("tf ",c.getInt(0) + "");
            Log.e("tf ",c.getString(1) + "");
            Log.e("tf ",c.getInt(2) + "");
        }
    }
    

    public void saveAllNlpData(){
        this.saveCategoryInDatabase();;
        this.saveMemoInDatabase();
        this.saveWordInDatabase();
    }
    public void saveMemoInDatabase(){
        memoManager.saveMemoInDatabase();
    }
    public void saveWordInDatabase(){
        memoManager.saveWordInDatabase();
    }
    public void saveCategoryInDatabase(){
        categoryManager.saveCategoryInDatabase();
    }
    public CategoryManager getCategoryManager() {return categoryManager;}
}
