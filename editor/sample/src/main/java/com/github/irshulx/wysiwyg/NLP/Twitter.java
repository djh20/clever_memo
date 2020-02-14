package com.github.irshulx.wysiwyg.NLP;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import java.io.Serializable;
import java.util.List;

import scala.collection.Seq;

public class Twitter implements Serializable {

    public Twitter(){
        CharSequence loading = TwitterKoreanProcessorJava.normalize("로딩");
        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(loading);
        List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
    }

    public List<KoreanPhraseExtractor.KoreanPhrase> analysis(String input){

        String input2 = "D\n";
        CharSequence normalized;
        if(input.equals("")) {
            normalized = TwitterKoreanProcessorJava.normalize(input2);
        }else{
            normalized = TwitterKoreanProcessorJava.normalize(input);
        }
        System.out.println(normalized);
        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
        List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
        System.out.println(phrases);

        return phrases;
    }

}
