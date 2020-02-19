package com.github.irshulx.wysiwyg.NLP;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.Model.Noun;
import com.github.irshulx.wysiwyg.NLP.Twitter;
import com.github.irshulx.wysiwyg.R;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.DrawContaioner;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.MyPaintView;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.SerialBitmap;
import com.github.irshulx.wysiwyg.Utilities.RealPathUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import scala.util.control.TailCalls;
import yuku.ambilwarna.AmbilWarnaDialog;


public class MemoLoadManager extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int FILE_SELECT_CODE = 0;
    String imageFilePath;
    Uri uri;
    int pagew, pageh;
    MyPaintView paintArr[];
    int pdfCount;
    int lastTouch;
    String memoName;
    int tColor = 0;
    NLPManager nlpManager;
    static final int LOAD_PAGE = 10;
    int loadedPageIndex;
    File memoFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcanvas);
        nlpManager = NLPManager.getInstance();
        imageFilePath = getFilesDir().toString() + "/memoImage";
        showFileChooser();
        Log.e("끝남", "ㅇㅁㄴㅇㄴㅁ");
    }


    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File"),
                    FILE_SELECT_CODE);

        } catch (Exception e) {
            Log.e("error", e.getMessage());
//            Toast.makeText(this, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    File file = new File(RealPathUtil.getRealPath(getApplicationContext(), uri));
                    memoName = file.getName();
                    if (isExistMemo()) {
                        Log.e("dd11" , " ??");
                        Toast.makeText(getApplicationContext(), "이미 존재하는 메모입니다, 저장된 메모를 로드합니다", Toast.LENGTH_SHORT).show();
                        loadExistMemoObject();
                    } else {
                        makeFolder();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Vector<Noun> nouns = null;
                                try {
                                    nouns = nlpManager.getResultFrequencyDataFromPdfFilePath(RealPathUtil.getRealPath(getApplicationContext(), uri));
//                                    nlpManager.pushNewMemoAndResultFrequency(memoName, nouns, pdfCount);
//                                    nlpManager.saveMemoInDatabase();
//                                    nlpManager.saveWordInDatabase();
//                                    nlpManager.printWordDB();
//                                    nlpManager.printTfDB();
                                } catch (FileNotFoundException e) {
                                    Log.e("Error", e.getMessage());
                                }
                            }
                        }).start();
                        try {
                            openNewMemoThread();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                System.gc();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    boolean isExistMemo() {
        String path = imageFilePath + "/" + memoName;
        File file = new File(path);
        if (file.exists() && file.isDirectory())
            return true;
        return false;
    }

    void makeFolder() {
        memoFolder = new File(imageFilePath, memoName);
        if (!memoFolder.exists()) {
            memoFolder.mkdirs();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("저장")
                .setMessage("수정 내용을 저장하시겠습니까?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0 ; i < paintArr.length ; i++){
                            saveObject(paintArr[i],i); //수정된 이미지 저장
                        }
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    void openNewMemoThread() throws FileNotFoundException {
        Log.e("mem", ((ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() + "");
        Log.e("mem", Runtime.getRuntime().maxMemory() + "");
        final RelativeLayout scroll = findViewById(R.id.scroll);
        final int NUM_THREAD = 1;

        final ParcelFileDescriptor fd = this.getContentResolver().openFileDescriptor(uri, "r");
        final PdfiumCore pdfiumCore = new PdfiumCore(this);


        try {
            final PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfCount = pdfiumCore.getPageCount(pdfDocument) -1;
            paintArr = new MyPaintView[pdfCount];

            for (int i = 0; i < pdfCount; i++) {
                paintArr[i] = new MyPaintView(getApplicationContext(), null,new DrawContaioner());
                paintArr[i].setId(i);
//                paintArr[i].setAdjustViewBounds(true);
                scroll.addView(paintArr[i]);
                loadedPageIndex = i;
            }
            Log.e("pdfCount", pdfCount+"");


                    for(int i = 0 ; i <= NUM_THREAD-1 ; i++){
                        final int index = i;
                        final int firstPage = (pdfCount/NUM_THREAD)*i;
                        Log.e("index" , i+ "");
                        Log.e("firstPage" , firstPage + " ");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {


                                DrawContaioner drawContaioner;
                                ByteArrayOutputStream stream;

                                for (int j = firstPage; j <= firstPage + (pdfCount/NUM_THREAD); j++) {
                                    Bitmap tmpBitmap;
                                    Bitmap bitmap;
                                    Log.e("exe", j + " ");
                                    pdfiumCore.openPage(pdfDocument, j);
                                    final int width = pdfiumCore.getPageWidthPoint(pdfDocument, j);
                                    final int height = pdfiumCore.getPageHeightPoint(pdfDocument, j);
                                    Log.e("page", pdfiumCore.getPageCount(pdfDocument) +"");
                                    Log.e("width", width +"");
                                    final int pageNum = j;
                                    if (width > 0 && height > 0) {
                                        pagew = scroll.getMeasuredWidth();
                                        pageh = Math.round (height*((float)pagew / width));
                                        tmpBitmap = Bitmap.createBitmap(pagew, pageh, Bitmap.Config.RGB_565);
                                        pdfiumCore.renderPageBitmap(pdfDocument, tmpBitmap, pageNum, 0, 0,
                                        pagew, pageh);
                                        tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap,(int)(pagew/0.8) , (int) (pageh/0.8), true);
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        tmpBitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
                                        tmpBitmap.recycle();
                                        tmpBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(),0 , baos.size());

                                        final SerialBitmap finalBitmap = new SerialBitmap(tmpBitmap);
                                        try {
                                            baos.flush();
                                            baos.close();
                                            drawContaioner = paintArr[pageNum].getDrawContaioner();
                                            drawContaioner.setDetail(finalBitmap, pageNum, pagew, pageh);
                                            paintArr[pageNum].setDrawContaioner(drawContaioner);
                                            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                                    paintArr[pageNum].setAdjustViewBounds(true);
                                                    layoutParams.addRule(RelativeLayout.BELOW, pageNum-1);
//                                                    paintArr[pageNum].setScaleType(ImageView.ScaleType.FIT_XY);
                                                    paintArr[pageNum].setLayoutParams(layoutParams);
                                                    paintArr[pageNum].measure(pagew,pageh);
                                                    paintArr[pageNum].setImageBitmap(finalBitmap.getBitmap());
                                                    scroll.requestLayout();
                                                }
                                            });

                                        }
                                        catch (Exception e){
                                            Log.e("dasdsada", e.getMessage());
                                        }
                                    }

                                }
                            }
                        }).start();
                    }

        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }







//    void openNewMemo() {
//        final RelativeLayout scroll = findViewById(R.id.scroll);
//        ParcelFileDescriptor fd = null;
//        try {
//            fd = this.getContentResolver().openFileDescriptor(uri, "r");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        ;
//
//        PdfiumCore pdfiumCore = new PdfiumCore(this);
//        try {
//            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
//            pdfCount = pdfiumCore.getPageCount(pdfDocument);
//            paintArr = new MyPaintView[pdfCount];
//            int pageNum = 0;
//            for (int i = 0; i < pdfCount; i++) {
//                pdfiumCore.openPage(pdfDocument, pageNum);
//
//                int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
//                int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);
//
//                if (width > 0 && height > 0) {
//                    pagew = scroll.getMeasuredWidth();
//                    pageh = height * pagew / width;
//                    final Bitmap tmpBitmap = Bitmap.createBitmap(pagew, pageh, Bitmap.Config.RGB_565);
//
//                    pdfiumCore.renderPageBitmap(pdfDocument, tmpBitmap, pageNum, 0, 0,
//                            scroll.getMeasuredWidth(), height * scroll.getMeasuredWidth() / width);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    tmpBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteStream = stream.toByteArray();
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteStream,0,byteStream.length);
//
//
//                    final int index = i;
//                    MyPaintView img = new MyPaintView(getApplicationContext(), null, new DrawContaioner(new SerialBitmap(bitmap), i, pagew, pageh));
//                    img.setId(i);
//                    paintArr[i] = img;
//                    final int nowTouch = i;
//
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    layoutParams.addRule(RelativeLayout.BELOW, i - 1);
//                    img.setLayoutParams(layoutParams);
//                    scroll.addView(img);
//                    pageNum++;
//                    loadedPageIndex = i;
//                }
//            }
//            pdfiumCore.closeDocument(pdfDocument);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

//    void loadExistMemo(){
//        String path = imageFilePath + "/" + memoName;
//        RelativeLayout scroll = findViewById(R.id.scroll);
//        File file = new File(path);
//        int pageNum = file.list().length;
//        Log.e("PageNum", pageNum + "");
//
//        try {
//            pdfCount = pageNum;
//            paintArr = new MyPaintView[pageNum];
//            for(int i = 0 ; i < pageNum; i++){
//                BitmapFactory.Options option = new BitmapFactory.Options();
//                option.inPreferredConfig = Bitmap.Config.RGB_565;
//                Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + i + ".dat", option);
//                pageh = bitmap.getHeight();
//                pagew = bitmap.getWidth();
//
//                MyPaintView img = new MyPaintView(getApplicationContext(), null, new DrawContaioner(new SerialBitmap(bitmap), i, pagew, pageh));
//                img.setId(i);
//                paintArr[i] = img;
//                final int lastNum = i;
//                img.setOnTouchListener(new View.OnTouchListener(){
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        lastTouch = lastNum;
//                        return false;
//                    }
//                });
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.addRule(RelativeLayout.BELOW, i-1);
//                img.setLayoutParams(layoutParams);
//                scroll.addView(img);
//            }
//        } catch(Exception e) {
//            Log.e("Error",e.getMessage());
//        }
//    }

    void loadExistMemoObject(){
        String path = imageFilePath + "/" + memoName;
        RelativeLayout scroll = findViewById(R.id.scroll);
        File file = new File(path);
        int pageNum = file.list().length;
        Log.e("PageNum", pageNum + "");

        try {
            pdfCount = pageNum;
            paintArr = new MyPaintView[pageNum];

            for(int i = 0 ; i < pageNum; i++){
                File objectFile = new File(path + "/" + i + ".dat");
                FileInputStream fis = new FileInputStream(objectFile);
                ObjectInputStream ios = new ObjectInputStream(fis);
                DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();
                MyPaintView myPaintView = new MyPaintView(getApplicationContext(),null,drawContaioner);
                paintArr[i] = myPaintView;
                myPaintView.setId(i);
                pagew= myPaintView.getDrawContaioner().getHeight();
                pagew= myPaintView.getDrawContaioner().getWidth();
                Log.e("loadInfo", myPaintView.getDrawContaioner().getHeight() + "");
                Log.e("loadInfo", myPaintView.getDrawContaioner().getWidth() + "");
                final int lastNum = i;
                myPaintView.setOnTouchListener(new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastTouch = lastNum;
                        return false;
                    }
                });
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, i-1);
                myPaintView.setLayoutParams(layoutParams);
                scroll.addView(myPaintView);
            }
        } catch(Exception e) {
            Log.e("Error",e.getMessage());
        }
    }

    void saveObject(MyPaintView myPaintView, int pageNum) {
        try {
            String path = imageFilePath + "/" + memoName;
//            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); // 외부저장소로 저장테스트용
            File file = new File(path ,pageNum+".dat");
            Log.e("save", file.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(myPaintView.getDrawContaioner());
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.e("Error_Object", e.getMessage());
        }
    }


    void saveImage(Bitmap bitmap, int pageNum) {
        try {
            String path = imageFilePath + "/" + memoName;
//            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); // 외부저장소로 저장테스트용
            File file = new File(path ,pageNum+".png");
            Log.e("save", file.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdfmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stroke:
                showWriteSetup();
                break;
            case R.id.colorPick:
                openColorPicker();
                break;
            case R.id.undo:
                paintArr[lastTouch].onClickUndo();
                break;
            case R.id.redo:
                paintArr[lastTouch].onClickRedo();
                break;
            case R.id.erase:
                int i = 0;
                for(MyPaintView p : paintArr){
                    Log.e("터지는 부분", i + "");
                    p.setEraseMode();
                    i++;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showWriteSetup() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setMessage("굵기 입력");
        builder.setView(editText);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < pdfCount; i++) {
                            paintArr[i].setStrokeWidth(Integer.parseInt(editText.getText().toString()));
                        }

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();

    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, tColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                for (int i = 0; i < pdfCount; i++) {
                        paintArr[i].setColor(color);
                }
            }
        });
        colorPicker.show();
    }
}


////TODO 형태소분석 클래스에 합병
//    void analisysPDF(Intent data){
//        final Intent pdfData = data;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                uri = pdfData.getData();
//                String filePath = RealPathUtil.getRealPath(getApplicationContext(), uri);
//                File file = new File(filePath);
//                String text = "";
//                try {
//                    PdfReader reader = new PdfReader(new FileInputStream(file));
//                    int n = reader.getNumberOfPages();
//                    for(int i =0 ; i < n ; i++) {
//                        text += PdfTextExtractor.getTextFromPage(reader, i + 1) + "\n";
//                    }
//
//                }
//                catch(Exception e){
//                }
//            }
//        }).start();
//    }



