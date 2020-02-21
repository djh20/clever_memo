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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.Model.Category;
import com.github.irshulx.wysiwyg.Model.Memo;
import com.github.irshulx.wysiwyg.Model.Noun;
import com.github.irshulx.wysiwyg.NLP.Twitter;
import com.github.irshulx.wysiwyg.R;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.BitmapManager;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.DrawContaioner;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.MyPaintView;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.PaintViewManager;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.SerialBitmap;
import com.github.irshulx.wysiwyg.Utilities.RealPathUtil;
import com.github.irshulx.wysiwyg.ui.CategorySelectActivity;
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

import static android.support.v4.widget.NestedScrollView.*;


public class MemoLoadManager extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int FILE_SELECT_CODE = 0;
    String imageFilePath;
    Uri uri;
    int pagew, pageh;
    int pdfCount;
    int lastTouch;
    String memoName;
    int tColor = 0;
    NLPManager nlpManager;
    static final int LOAD_PAGE = 20;
    static final int NUM_PAGE_LOAD_BY_SCROLL = 5;
    int loadedPageIndex;
    File memoFolder;
    RelativeLayout scroll;
    BitmapManager bitmapManager;
    int numLoadedPage;
    PaintViewManager paintViewManager;
    boolean loadThreadUsing = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcanvas);
        bitmapManager = BitmapManager.getInstance();
        nlpManager = NLPManager.getInstance();
        imageFilePath = getFilesDir().toString() + "/memoImage";
        showFileChooser();
        scroll = findViewById(R.id.scroll);
        loadedPageIndex = 0 ;
        final ScrollView scrollView = findViewById(R.id.container);
        //TODO 스크롤 바닥/천장에 닿았을때의 기능 작성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {

                @Override
              public void onScrollChange(View view, int i0, int i1, int i2, int i3) {
                    boolean using = false;
                    View view2 = (View) scrollView.getChildAt(scrollView.getChildCount()-1);
                    // Calculate the scrolldiff
                    int diff = (view2.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()));
                    Log.e("diff",diff+"");

                    if(scrollView.getScrollY() == 0) { // 천장
                        if(loadedPageIndex + 1 - LOAD_PAGE > 0 ){
                        scrollView.setScrollY(scrollView.getScrollY() + 100);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                onTop();
                            }
                        }).start();
                        }

                    }
                    if( diff ==0 && loadedPageIndex != pdfCount-1) // 바닥
                    {
                        scrollView.setScrollY(scrollView.getScrollY() - 100);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                onBottom();
                            }
                        }).start();

//                            int numToLoad;
//                            if (pdfCount - loadedPageIndex >= NUM_PAGE_LOAD_BY_SCROLL)
//                                numToLoad = NUM_PAGE_LOAD_BY_SCROLL;
//                            else
//                                numToLoad = pdfCount - loadedPageIndex;
//                            Log.e("numToLoad", numToLoad + " ");
//                            if (numToLoad > 0) {
//                                final int numToLoadFinal = numToLoad;
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        saveAndLoadObjectByPageNum(loadedPageIndex - LOAD_PAGE + 1, loadedPageIndex - LOAD_PAGE + NUM_PAGE_LOAD_BY_SCROLL
//                                                , loadedPageIndex + 1, loadedPageIndex + numToLoadFinal, numToLoadFinal);
//                                    }
//                                }).start();
//
//                            }
                    }
                }
            });
        }
    }

    synchronized public void onTop() {
        try {
            //save
            String path = imageFilePath + "/" + memoName;
            final MyPaintView removeView = paintViewManager.getMyPaintViewById(loadedPageIndex + 1);
            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scroll.removeView(removeView);
                }
            });

            File file = new File(path, removeView.getDrawContaioner().getPageNum() + ".dat");
            Log.e("save", file.getAbsolutePath());
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(removeView.getDrawContaioner());
            oos.flush();
            oos.close();
            fos.close();
            Log.e("결과", bitmapManager.isExist(removeView.getSerialBitMap()) + "");
            bitmapManager.setUnUse(removeView.getDrawContaioner().getmBit().getBitmap());
            paintViewManager.remove(removeView);

            final MyPaintView cleared = new MyPaintView(this, null);
            File objectFile = new File(path + "/" + (loadedPageIndex + 1 - LOAD_PAGE) + ".dat");
            FileInputStream fis = new FileInputStream(objectFile);
            ObjectInputStream ios = new ObjectInputStream(fis);
            final DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();
            drawContaioner.createmPaint();
            cleared.setupToUse(this);
            cleared.setDrawContaioner(drawContaioner);
            cleared.setId(loadedPageIndex + 1 - LOAD_PAGE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW, loadedPageIndex - LOAD_PAGE);
            cleared.setLayoutParams(layoutParams);
            paintViewManager.addPaintView(cleared);
            cleared.setImageBitmap(cleared.getDrawContaioner().getmBit().getBitmap());
            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scroll.addView(cleared);
                    scroll.requestLayout();
                }
            });
            loadedPageIndex--;
            Log.e("끝남", "끝남");
        } catch (Exception e) {
            Log.e("io", e.getMessage());
        }




    }

    synchronized public void saveAndLoadObjectByPageNum(final int startPage, final int endPage, final int startLoadPage, final int endLoadPage, final int numToLoad){

        Log.e("startPage", startPage + "");
        Log.e("endPage", endPage + "");

                    if(paintViewManager.getMyPaintViewById(startPage+1) != null && paintViewManager.getMyPaintViewById(startPage+1).getDrawContaioner() != null) {
                        Log.e("진입", "진입");
                        final String path = imageFilePath + "/" + memoName;
                        for (int i = startPage; i <= endPage; i++) {
                            final int viewId = i +1;
                            File file = new File(path + "/" + i);
                            Log.e("save", file.getAbsolutePath() + Thread.currentThread().getId());

                            FileOutputStream fos = null;
                            MyPaintView myPaintView = paintViewManager.getMyPaintViewById(viewId);
                            Log.e("pageNUm", myPaintView.getDrawContaioner().getPageNum() + "");
                            DrawContaioner drawContaioner = myPaintView.getDrawContaioner();
                            try {
                                fos = new FileOutputStream(file);
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(drawContaioner);
                                oos.flush();
                                oos.close();
                                fos.close();
                            } catch (Exception e) {
                                Log.e("so", e.getMessage());
                            }
                            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    paintViewManager.clearPaintViewByid(viewId);
                                }
                            });
                        }
                        try {
                            loadSavedPage(startLoadPage, endLoadPage, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
    }

    synchronized public void onBottom(){
        try {
            Log.e("loadedPageIndex", loadedPageIndex - LOAD_PAGE + 2 + " ");
            //save
            String path = imageFilePath + "/" + memoName;
            final MyPaintView removeView = paintViewManager.getMyPaintViewById(loadedPageIndex - LOAD_PAGE + 2);
            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scroll.removeView(removeView);
                }
            });

            File file = new File(path, removeView.getDrawContaioner().getPageNum() + ".dat");
            Log.e("save", file.getAbsolutePath());
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(removeView.getDrawContaioner());
            oos.flush();
            oos.close();
            fos.close();
            Log.e("결과", bitmapManager.isExist(removeView.getSerialBitMap())+"");
            bitmapManager.setUnUse(removeView.getDrawContaioner().getmBit().getBitmap());
            paintViewManager.remove(removeView);

            final MyPaintView cleared = new MyPaintView(this, null);
            File objectFile = new File(path + "/" + (loadedPageIndex + 1) + ".dat");
            FileInputStream fis = new FileInputStream(objectFile);
            ObjectInputStream ios = new ObjectInputStream(fis);
            final DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();
            drawContaioner.createmPaint();
            cleared.setupToUse(this);
            cleared.setDrawContaioner(drawContaioner);
            cleared.setId(loadedPageIndex + 2);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW, loadedPageIndex + 1);
            cleared.setLayoutParams(layoutParams);
            paintViewManager.addPaintView(cleared);
            cleared.setImageBitmap(cleared.getDrawContaioner().getmBit().getBitmap());
            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scroll.addView(cleared);
                    scroll.requestLayout();
                }
            });
            loadedPageIndex++;
            Log.e("끝남", "끝남");
        }
            catch (Exception e){
            Log.e("io", e.getMessage());
            }




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
                        try {
                            loadSavedPage(0,20, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "이미 존재하는 메모입니다, 저장된 메모를 로드합니다", Toast.LENGTH_SHORT).show();
//                        loadExistMemoObject();
                    } else {
                        makeFolder();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    final ArrayList<Noun> nouns = nlpManager.getResultFrequencyDataFromPdfFilePath(RealPathUtil.getRealPath(getApplicationContext(), uri));
                                    (MemoLoadManager.this).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Memo memo = nlpManager.pushNewMemoAndResultFrequency(memoName, nouns, pdfCount);
                                            ArrayList<Category> recommandedCategory = nlpManager.getRecommandedCategories(memo);
                                            Intent intent = new Intent(getApplicationContext(), CategorySelectActivity.class);
                                            intent.putExtra("memo", memo);
                                            intent.putExtra("categoryPool", recommandedCategory);
                                            startActivity(intent);
                                        }
                                    });
                                } catch (FileNotFoundException e) {
                                    Log.e("Error", e.getMessage());
                                }
                            }
                        }).start();
                        try {
                            loadPdfAndSave();
                            paintViewManager = new PaintViewManager(this, scroll, pdfCount, pagew, pageh);
                            loadSavedPage(0, LOAD_PAGE, false);
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                    }
                }
//                System.gc();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadSavedPage(final int startPage, final int toLoadPage, final boolean isLoaded) throws IOException, ClassNotFoundException {
        final String path = imageFilePath + "/" + memoName;
        File file = new File(path);
        final int pageNum = file.list().length;

        if(paintViewManager == null){
            File objectFile;
            objectFile = new File(path + "/" + 0 + ".dat");
            FileInputStream fis = new FileInputStream(objectFile);
            ObjectInputStream ios = new ObjectInputStream(fis);
            final DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();
            paintViewManager = new PaintViewManager(getApplicationContext(), scroll, pageNum, drawContaioner.getWidth(), drawContaioner.getHeight());
        }
        Log.e("진입", "로드진입");
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("startPage" , startPage + "");
                    Log.e("toLoadPage" , toLoadPage + "");
                    for(int i = startPage ; i < toLoadPage; i++){
                        try {
                            final int viewId = i+1;
                            Log.e("Dasd", viewId + "");
                            File objectFile;
                            objectFile = new File(path + "/" + i + ".dat");
                            FileInputStream fis = new FileInputStream(objectFile);
                            ObjectInputStream ios = new ObjectInputStream(fis);
                            final DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();

                            Log.e("Dasd2", viewId + "");
                            drawContaioner.createmPaint();
                            drawContaioner.setPageNum(i);
                            if (isLoaded == true)
                                (MemoLoadManager.this).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        paintViewManager.addNewViewById(getApplicationContext(),drawContaioner,viewId);
                                    }
                                });
                            else {
                                paintViewManager.setContainerById(viewId, drawContaioner);
                                paintViewManager.setupToUseById(viewId, getApplicationContext());
                            }

                            pagew = drawContaioner.getWidth();
                            pagew = drawContaioner.getHeight();

                            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    paintViewManager.setBitmapItSelfById(viewId);
                                    scroll.requestLayout();
                                    Log.e("Dasd4", viewId + "");
                                }
                            });
                            if(i >= loadedPageIndex)
                                loadedPageIndex = i;
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch(Exception e) {
            Log.e("Error",e.getMessage());
        }
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

    void loadPdfAndSave() throws IOException {

        final int NUM_THREAD = 5;
        final String FILE_PATH = imageFilePath + "/" + memoName;

        final ParcelFileDescriptor fd = this.getContentResolver().openFileDescriptor(uri, "r");
        final PdfiumCore pdfiumCore = new PdfiumCore(this);

        final PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
        pdfCount = pdfiumCore.getPageCount(pdfDocument);
        Log.e("pn", pdfCount + "");
        numLoadedPage = 0 ;
        final int batchSize = pdfCount/NUM_THREAD;


        for(int i = 0 ; i < NUM_THREAD ; i++){
            final int batchNum = i;
             new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    for (int j = batchNum ; j < pdfCount ; j += NUM_THREAD) {
                        int toLoadPage = j;
                            pdfiumCore.openPage(pdfDocument, toLoadPage);
                            final int width = pdfiumCore.getPageWidthPoint(pdfDocument, toLoadPage);
                            final int height = pdfiumCore.getPageHeightPoint(pdfDocument, toLoadPage);
                            final int bitMapWidth =scroll.getMeasuredWidth();;
                            final int bitMapHeight = Math.round(height * ((float) bitMapWidth / width));
                            SerialBitmap bitmap = bitmapManager.getUnUsingBitmap();
                            pagew = bitMapWidth;
                            pageh = bitMapHeight;

                            bitmap.setBitmap(Bitmap.createBitmap(bitMapWidth,bitMapHeight, Bitmap.Config.RGB_565));
                            pdfiumCore.renderPageBitmap(pdfDocument, bitmap.getBitmap(), toLoadPage, 0, 0,
                                    bitMapWidth, bitMapHeight);
                            bitmap.setBitmap(Bitmap.createScaledBitmap(bitmap.getBitmap(), (int) (bitMapWidth / 0.8), (int) (bitMapHeight / 0.8), true));
                            DrawContaioner drawContaioner = new DrawContaioner();
                            drawContaioner.setDetail(bitmap, toLoadPage , bitMapWidth, bitMapHeight);
                            drawContaioner.setPageNum(toLoadPage);
                            Log.e("toLoadPage", toLoadPage +"");

                            File file = new File(FILE_PATH ,toLoadPage+".dat");
                            Log.e("save", file.getAbsolutePath());

                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(drawContaioner);
                                oos.flush();
                                oos.close();
                                fos.close();
                                bitmapManager.setUnUse(bitmap);
                            } catch (Exception e) {
                                Log.e("so", e.getMessage());
                            }
                            numLoadedPage++;
                            Log.e("nlp", numLoadedPage +"");
                        }
                }
            }).start();
        }

        while(numLoadedPage < pdfCount -1){Log.e("이유는?", numLoadedPage +"  " +  pdfCount);};
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("저장")
                .setMessage("수정 내용을 저장하시겠습니까?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveObjectInMemory();
                        bitmapManager.clearAll();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


//    void openNewMemoThread() throws FileNotFoundException {
//        Log.e("mem", ((ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() + "");
//        Log.e("mem", Runtime.getRuntime().maxMemory() + "");
//        final RelativeLayout scroll = findViewById(R.id.scroll);

//        final int NUM_LOAD_PAGE = 20;
//
//
//        final ParcelFileDescriptor fd = this.getContentResolver().openFileDescriptor(uri, "r");
//        final PdfiumCore pdfiumCore = new PdfiumCore(this);
//
//
//        PdfDocument pdfDocument = null;
//        try {
//            pdfDocument = pdfiumCore.newDocument(fd);
//            pdfCount = pdfiumCore.getPageCount(pdfDocument) -1;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//            paintArr = new MyPaintView[pdfCount];
//
//            for (int i = 0; i < NUM_LOAD_PAGE; i++) { // 먼저 20장 로딩
//                paintArr[i] = new MyPaintView(getApplicationContext(), null,new DrawContaioner());
//                paintArr[i].setId(i);
////                paintArr[i].setAdjustViewBounds(true);
//                scroll.addView(paintArr[i]);
//                loadedPageIndex = i;
//            }
//            Log.e("pdfCount", pdfCount+"");
//
//


//




//
//            for(int i = 0 ; i < NUM_LOAD_PAGE ; i++) {
//                DrawContaioner drawContaioner;
//                ByteArrayOutputStream stream;
//                Bitmap tmpBitmap;
//                pdfiumCore.openPage(pdfDocument, i);
//                final int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
//                final int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);
//                Log.e("page", pdfiumCore.getPageCount(pdfDocument) + "");
//                Log.e("width", width + "");
//                Log.e("height", height + "");
//                final int pageNum = i;
//                if (width > 0 && height > 0) {
//                    pagew = scroll.getMeasuredWidth();
//                    pageh = Math.round(height * ((float) pagew / width));
//                    tmpBitmap = Bitmap.createBitmap(pagew, pageh, Bitmap.Config.RGB_565);
//                    pdfiumCore.renderPageBitmap(pdfDocument, tmpBitmap, pageNum, 0, 0,
//                            pagew, pageh);
//                    tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap, (int) (pagew / 0.8), (int) (pageh / 0.8), true);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    tmpBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//                    tmpBitmap.recycle();
//                    tmpBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
//
//                    final SerialBitmap finalBitmap = new SerialBitmap(tmpBitmap);
//                    try {
//                        baos.flush();
//                        baos.close();
//                        drawContaioner = paintArr[pageNum].getDrawContaioner();
//                        drawContaioner.setDetail(finalBitmap, pageNum, pagew, pageh);
//                        paintArr[pageNum].setDrawContaioner(drawContaioner);
//                        (MemoLoadManager.this).runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                                                    paintArr[pageNum].setAdjustViewBounds(true);
//                                layoutParams.addRule(RelativeLayout.BELOW, pageNum - 1);
////                                                    paintArr[pageNum].setScaleType(ImageView.ScaleType.FIT_XY);
//                                paintArr[pageNum].setLayoutParams(layoutParams);
//                                paintArr[pageNum].measure(pagew, pageh);
//                                paintArr[pageNum].setImageBitmap(finalBitmap.getBitmap());
//                                scroll.requestLayout();
//                            }
//                        });
//
//                    } catch (Exception e) {
//                        Log.e("dasdsada", e.getMessage());
//                    }
//
//
//                }
//            }
//
//    }







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

//    void loadExistMemoObject(){
//        String path = imageFilePath + "/" + memoName;
//        RelativeLayout scroll = findViewById(R.id.scroll);
//        File file = new File(path);
//        int pageNum = file.list().length;
//        Log.e("PageNum", pageNum + "");
//
//        try {
//            pdfCount = pageNum;
//            paintArr = new MyPaintView[pageNum];
//
//            for(int i = 0 ; i < pageNum; i++){
//                File objectFile = new File(path + "/" + i + ".dat");
//                FileInputStream fis = new FileInputStream(objectFile);
//                ObjectInputStream ios = new ObjectInputStream(fis);
//                DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();
//                MyPaintView myPaintView = new MyPaintView(getApplicationContext(),null,drawContaioner);
//                paintArr[i] = myPaintView;
//                myPaintView.setId(i);
//                pagew= myPaintView.getDrawContaioner().getHeight();
//                pagew= myPaintView.getDrawContaioner().getWidth();
//                Log.e("loadInfo", myPaintView.getDrawContaioner().getHeight() + "");
//                Log.e("loadInfo", myPaintView.getDrawContaioner().getWidth() + "");
//                final int lastNum = i;
//                myPaintView.setOnTouchListener(new View.OnTouchListener(){
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        lastTouch = lastNum;
//                        return false;
//                    }
//                });
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.addRule(RelativeLayout.BELOW, i-1);
//                myPaintView.setLayoutParams(layoutParams);
//                scroll.addView(myPaintView);
//            }
//        } catch(Exception e) {
//            Log.e("Error",e.getMessage());
//        }
//    }

    void saveObjectInMemory() {
        try {
            ArrayList<MyPaintView> myPaintViewPool = paintViewManager.getMyPaintViewPool();
            String path = imageFilePath + "/" + memoName;
            for(int i = 0 ; i < myPaintViewPool.size() ; i++){
                MyPaintView myPaintView = myPaintViewPool.get(i);
                File file = new File(path ,myPaintView.getDrawContaioner().getPageNum()+".dat");
                Log.e("save", file.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(myPaintView.getDrawContaioner());
                oos.flush();
                oos.close();
                fos.close();
            }
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
                paintViewManager.getMyPaintViewById(lastTouch).onClickUndo();
                break;
            case R.id.redo:
                paintViewManager.getMyPaintViewById(lastTouch).onClickRedo();
                break;
            case R.id.erase:
                int i = 0;
                for(MyPaintView p : paintViewManager.getMyPaintViewPool()){
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
                            paintViewManager.getMyPaintViewById(lastTouch).setStrokeWidth(Integer.parseInt(editText.getText().toString()));
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
                    paintViewManager.getMyPaintViewById(lastTouch).setColor(color);
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

