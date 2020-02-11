package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PdfFileParser {

    public String PdfFileParser(String pdffilePath) // txt로 변환
    {
        try {

            String content;
            System.out.println(pdffilePath.toString());
            FileInputStream fi = new FileInputStream(new File(pdffilePath));
            PDFParser parser = new PDFParser(fi);
            parser.parse();
            COSDocument cd = parser.getDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            content = stripper.getText(new PDDocument(cd));
            cd.close();
            System.out.println(content);
            return content;
        } catch (Exception e ){
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String args[]) throws FileNotFoundException, IOException
//    {
//        String filepath = "C:\\Users\\user\\Desktop\\2019-운영체제-강의노트-3. Process.pdf"; // 파일 경로 입력할 곳 -> 파일 로드시킨거랑 연결 필요
//
//
//        try {
//            OutputStream output = new FileOutputStream("C:\\Users\\user\\Desktop\\Output.txt"); // txt로 저장
//            String str =new PdfFileParser().PdfFileParser(filepath);
//            byte[] by=str.getBytes();
//            output.write(by);
//
//        } catch (Exception e) {
//            e.getStackTrace();
//        }
//    }
}
