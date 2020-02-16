package com.github.irshulx.wysiwyg.NLP;

import com.github.irshulx.wysiwyg.Model.Noun;
import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import scala.collection.Seq;

public class Twitter implements Serializable {
    final int MAX_COUNT = 40;
    public Twitter() {
        CharSequence loading = TwitterKoreanProcessorJava.normalize("로딩");
        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(loading);
        List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
    }

//    public List<KoreanPhraseExtractor.KoreanPhrase> analysis(String input){
//
//        String input2 = "D\n";
//        CharSequence normalized;
//        if(input.equals("")) {
//            normalized = TwitterKoreanProcessorJava.normalize(input2);
//        }else{
//            normalized = TwitterKoreanProcessorJava.normalize(input);
//        }
//        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
//        List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
//
//        return phrases;
//    }

    public Vector<Noun> getNounsWithFrequency(String text) {
        CharSequence normalized = TwitterKoreanProcessorJava.normalize(text);
        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
        Vector<Noun> nouns = new Vector<Noun>();
        List<KoreanTokenJava> kts = TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tokens);
        for (int i = 0; i < kts.size(); i++) {
            KoreanTokenJava kt = kts.get(i);
            if (kt.getPos().name().equals("Noun")) {
                if (kt.getText().length() > 1)
                    insertNoun(nouns, kt.getText());
            }
        }
        Collections.sort(nouns);
        int cntNoun = 0;
        for (int i = 0; i < nouns.size(); i++) {
            if(cntNoun < MAX_COUNT) {
                Noun n = nouns.get(i);
                if (n.getCnt() == 1) {
                    nouns.remove(i);
                    i--;
                } else
                    cntNoun++;
            }
            else{
                nouns.remove(i);
                i--;
            }
        }
        return nouns;
    }

    public static void insertNoun(Vector<Noun> nouns, String value){
        boolean existFlag = false;
        for (int i = 0; i < nouns.size(); i++) {
            Noun noun = nouns.get(i);
            if (noun.getName().equals(value)) {
                noun.increaseCnt();
                existFlag = true;
                break;
            }
        }
        if (existFlag == false) {
            nouns.add(new Noun(value));
        }
    }
}
