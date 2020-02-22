package com.github.irshulx.wysiwyg.NLP;

import android.util.Log;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.Model.Memo;
import com.github.irshulx.wysiwyg.Model.Noun;
import com.github.irshulx.wysiwyg.Model.Word;
import com.github.irshulx.wysiwyg.Model.WordBag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class MemoManager implements Serializable {
    ArrayList<Memo> memoPool;
    ArrayList<Word> wordPool;
    int lastMemoIndex;

    public MemoManager() {
        this.memoPool = new ArrayList<Memo>();
        this.wordPool = new ArrayList<Word>();
        lastMemoIndex = 0;
    }

    public ArrayList<Memo> getMemoPool() {
        return memoPool;
    }

    public void setMemoPool(ArrayList<Memo> memoPool) {
        this.memoPool = memoPool;
    }

    public ArrayList<Word> getWordPool() {
        return wordPool;
    }

    public void setWordPool(ArrayList<Word> wordPool) {
        this.wordPool = wordPool;
    }

    public int getLastMemoIndex() {
        return lastMemoIndex;
    }

    public void setLastMemoIndex(int lastMemoIndex) {
        this.lastMemoIndex = lastMemoIndex;
    }

    public Memo createNewMemoAndReturn(String memoName, String imagePath, int numPage){
        Log.e("add" , memoName);
        Log.e("add" , lastMemoIndex + "");
        Memo memo = new Memo(lastMemoIndex, memoName, imagePath, numPage);
        memoPool.add(memo);
        lastMemoIndex++;
        return memo;
    }

    public void pushWordAndMemoWordBag(ArrayList<Noun> nouns, Memo memo){
        for(int i = 0; i < nouns.size() ; i++){
            boolean existFlag = false;
            String word = nouns.get(i).getName();
            int frequency = nouns.get(i).getCnt();
            for(int j = 0 ; j < wordPool.size() ; j++){
                Word existWord = wordPool.get(j);
                if(existWord.getWord().equals(word) == true){
                    existFlag = true;
                    existWord.increaseDocumentFrequency();
                    existWord.addGlobalFrequency(frequency);
                    memo.addWordBag(existWord, frequency);
                    updateIdf(existWord);
                    saveTfInDatabase(memo, existWord, frequency);
                    break;
                }
            }
            if(existFlag == false){
                Word newWord = new Word(word, 1, frequency);
                wordPool.add(newWord);
                memo.addWordBag(newWord, frequency);
                updateIdf(newWord);
            }
        }
    }


    public void saveMemoInDatabase() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        for(int i = 0 ; i < memoPool.size() ; i++){
            Memo memo = memoPool.get(i);
            Log.e("memoIndex" , memo.getMemoIndex() + " ");
            Log.e("memoIndex" , memo.getMemoName() + " ");

            databaseManager.insertSQL("INSERT INTO Memo (memoIndex, memoName, category, updateDate, addedDate, pageNum, imagePath) "
                            + "VALUES(" + memo.getMemoIndex() + ", '"
                            + memo.getMemoName() + "', '"
                            + memo.getCategory() + "', '"
                            + memo.getUpdateDate() + "', '"
                            + memo.getAddedDate() + "', "
                            + memo.getNumPage() + ", '"
                            + memo.getImagePath() + "')");
        }
    }

    public void saveWordInDatabase() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        for(int i = 0 ; i < wordPool.size() ; i++){
            Word word = wordPool.get(i);
            databaseManager.insertSQL("INSERT INTO Word (word, globalFrequency, docFrequency, idf) "
                    + "VALUES('" + word.getWord() + "', "
                    + word.getGlobalFrequency() + ", "
                    + word.getDocumecntFrequency() + ", "
                    + word.getIdf() +")");
        }
    }

    public void saveTfInDatabase(Memo memo, Word word, int frequency) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.insertSQL("INSERT INTO TF (memoIndex, word, tf) "
                + "VALUES(" + memo.getMemoIndex() + ", '"
                + word.getWord() + "', "
                + frequency + ")");
    }

    public void updateIdf(Word word) {
            word.setIdf(NLP_math.baseLog(((memoPool.size()) /(1 + (double)word.getDocumecntFrequency())),2));
    }


    public ArrayList<Double> getMemoVector(Memo memo) {
        ArrayList<Double> vector = new ArrayList<Double>();
        ArrayList<WordBag> wordBagPool = memo.getWordBagPool();
        for(int j = 0 ; j < wordPool.size() ; j++) {
            boolean existFlag = false;
            Word wordInWords = wordPool.get(j);
            for(int k = 0 ; k < wordBagPool.size() ; k++) {
                WordBag wordBagInMemoWordBag = wordBagPool.get(k);
                Word wordInMemoWordBag = wordBagInMemoWordBag.getWord();
                if(wordInWords.equals(wordInMemoWordBag)) {
                    vector.add(wordBagInMemoWordBag.getTfIdf());
                    existFlag = true;
                    break;
                }
            }
            if(existFlag == false)
                vector.add(0.0);
        }
        return vector;
    }
}
