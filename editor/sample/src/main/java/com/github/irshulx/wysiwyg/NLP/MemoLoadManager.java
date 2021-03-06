package com.github.irshulx.wysiwyg.NLP;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.github.irshulx.wysiwyg.Utilities.DrawManager.PaintConfig;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.PaintViewManager;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.SerialBitmap;
import com.github.irshulx.wysiwyg.Utilities.RealPathUtil;
import com.github.irshulx.wysiwyg.ui.CategorySelectActivity;
import com.github.irshulx.wysiwyg.ui.toolFragment;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.rtugeek.android.colorseekbar.ColorSeekBar;
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
    PaintConfig pc = PaintConfig.getInstance();
    int startP;
    int toLoadP;
    String imageFilePath;
    private int change_stroke = 10; // 굵기 변경용
    private int change_color; // 색상 변경용
    private ColorSeekBar mColorSeekBar;
    private SeekBar strokeSeekBar;
    private float mScale = 1f;
    private Drawable tempicon;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;
    Uri uri;
    boolean eraseMode = false;

    int pagew, pageh;
    int pdfCount;
    String memoName;
    int tColor = 0;
    NLPManager nlpManager;
    static final int LOAD_PAGE = 20;
    int loadedPageIndex;
    File memoFolder;
    RelativeLayout scroll;
    BitmapManager bitmapManager;
    int numLoadedPage;
    PaintViewManager paintViewManager;
    boolean onTopUsing = false;
    boolean onBOtUsing = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcanvas);
        bitmapManager = BitmapManager.getInstance();

        nlpManager = NLPManager.getInstance();
        imageFilePath = getFilesDir().toString() + "/memoImage";

        scroll = findViewById(R.id.scroll);
        gestureDetector = new GestureDetector(this, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener()
        {
            @Override
            public boolean onScale(ScaleGestureDetector detector)
            {
                float scale = 1 - detector.getScaleFactor();

                float prevScale = mScale;
                mScale += scale;

                if (mScale < 0.1f) // Minimum scale condition:
                    mScale = 0.1f;

                if (mScale > 10f) // Maximum scale condition:
                    mScale = 10f;
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / mScale, 1f / prevScale, 1f / mScale, detector.getFocusX(), detector.getFocusY());
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                ScrollView layout =(ScrollView) findViewById(R.id.container);
                layout.startAnimation(scaleAnimation);
                return true;
            }
        });
        showFileChooser();
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

                    if(scrollView.getScrollY() <= (loadedPageIndex + 1 - LOAD_PAGE)*pageh + pageh*5 && onTopUsing == false)  { // 천장
                        if (loadedPageIndex != LOAD_PAGE-1){
                            for(int i = 0 ; i < 3 ; i++){
//                                scrollView.setScrollY(scrollView.getScrollY() + 100);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onTop();
                                    }
                                }).start();
                            }
                        }
                    }
                    if(scrollView.getScrollY() >= loadedPageIndex*pagew - pageh*5 && onBOtUsing == false){
                        for(int i = 0 ; i <3 ; i++) {
                            if(loadedPageIndex != pdfCount - 1){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onBottom();
                                    }
                                }).start();
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

//step 4: add private class GestureListener

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }

    synchronized public int onTop() {
        if (loadedPageIndex == LOAD_PAGE-1)
            return 0;
        try {
            onTopUsing = true;
            //save
            String path = imageFilePath + "/" + memoName;
            final MyPaintView removeView = paintViewManager.getMyPaintViewById(loadedPageIndex + 1);
            File file = new File(path, removeView.getDrawContaioner().getPageNum() + ".dat");
            Log.e("save", file.getAbsolutePath());
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(removeView.getDrawContaioner());

            oos.flush();
            oos.close();
            fos.close();
            Log.e("결과", bitmapManager.isExist(removeView.getSerialBitMap()) + "");
            removeView.clear();
            removeView.getDrawContaioner().setBitmapUnUse();

            final MyPaintView cleared = paintViewManager.getMyPaintViewById(loadedPageIndex  - LOAD_PAGE + 1);
            File objectFile = new File(path + "/" + (loadedPageIndex  - LOAD_PAGE) + ".dat");
            FileInputStream fis = new FileInputStream(objectFile);
            ObjectInputStream ios = new ObjectInputStream(fis);
            final DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();

            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cleared.setDrawContaioner(drawContaioner);
                    cleared.setImageBitmap(cleared.getDrawContaioner().getmBit().getBitmap());
                    paintViewManager.replace(removeView, getApplicationContext());
                    cleared.setImageBitmap(cleared.getDrawContaioner().getmBit().getBitmap());
                    scroll.requestLayout();
                }
            });

            onTopUsing = false;
            loadedPageIndex--;
            Log.e("끝남", "끝남");
        } catch (Exception e) {
            Log.e("io", e.getMessage());
        }

        return 0;


    }



    synchronized public int onBottom(){
        if(loadedPageIndex == pdfCount - 1)
            return 0;
        try {
            onBOtUsing = true;
            Log.e("loadedPageIndex", loadedPageIndex+ " ");
            //save
            String path = imageFilePath + "/" + memoName;
            final MyPaintView removeView = paintViewManager.getMyPaintViewById(loadedPageIndex - LOAD_PAGE + 2);
            File file = new File(path, removeView.getDrawContaioner().getPageNum() + ".dat");
            Log.e("save", file.getAbsolutePath());
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(removeView.getDrawContaioner());
            oos.flush();
            oos.close();
            fos.close();
            removeView.getDrawContaioner().setBitmapUnUse();


            final MyPaintView cleared = paintViewManager.getMyPaintViewById(loadedPageIndex +2);
            File objectFile = new File(path + "/" + (loadedPageIndex + 1) + ".dat");
            FileInputStream fis = new FileInputStream(objectFile);
            ObjectInputStream ios = new ObjectInputStream(fis);
            final DrawContaioner drawContaioner = (DrawContaioner) ios.readObject();

            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    paintViewManager.replace(removeView, getApplicationContext());
                    cleared.setDrawContaioner(drawContaioner);
                    cleared.setImageBitmap(cleared.getDrawContaioner().getmBit().getBitmap());
                    scroll.requestLayout();
                }
            });
            loadedPageIndex++;
            onBOtUsing = false;
        }
        catch (Exception e){
            Log.e("io", e.getMessage());
        }
        return 0;
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
                        SerialBitmap bitmap = bitmapManager.getUnUsingBitmap_565();
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
                            drawContaioner.setBitmapUnUse();
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
                        bitmapManager.clearAll_565();
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
                showTools();
                break;
            case R.id.colorPick:
                eraserSetup();
                break;
            case R.id.hilight:
                for(MyPaintView p: paintViewManager.getMyPaintViewPool()){
                    p.highlight();
                }
                break;
            case R.id.undo:
                paintViewManager.getMyPaintViewById(paintViewManager.getLastTouchViewID()).onClickUndo();
                break;
            case R.id.redo:
                paintViewManager.getMyPaintViewById(paintViewManager.getLastTouchViewID()).onClickRedo();
                break;
            case R.id.erase:
                if(!eraseMode){
                    tempicon = item.getIcon();
                    item.setIcon(R.drawable.ic_edit_black_24dp);
                }else{
                    item.setIcon(tempicon);
                }
                eraseMode = !eraseMode;
                for(MyPaintView p : paintViewManager.getMyPaintViewPool()){
                    p.setEraseMode();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTools(){

        final toolFragment tool = new toolFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .setCustomAnimations(R.anim.show_from_right, R.anim.exit_to_right, R.anim.show_from_right, R.anim.exit_to_right)
                .add(R.id.tool_frame, tool)
                .addToBackStack(null)
                .commit();

    }

    public void setDefaultPen(int color, int width, int alpha){
        for(MyPaintView p : paintViewManager.getMyPaintViewPool()){
            p.setStrokeWidth(width);
            p.setColor(color);
            p.setAlpha(alpha);
        }
    }

    public void showWriteSetup() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewInDialog = inflater.inflate(R.layout.seekbar_color_stroke, null);
        final AlertDialog penDialog = new AlertDialog.Builder(this).setView(viewInDialog).create();
        penDialog.show();
        Window window = penDialog.getWindow(); // dialog 상단 배치
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);
        mColorSeekBar = (ColorSeekBar) viewInDialog.findViewById(R.id.colorSlider);
        mColorSeekBar.setColorSeeds(R.array.text_colors);
        strokeSeekBar = viewInDialog.findViewById(R.id.strokeSlider);

        final TextView textView = (TextView) viewInDialog.findViewById(R.id.textView);
        final ImageView oval_image = (ImageView) viewInDialog.findViewById(R.id.oval_image);
        final GradientDrawable drawable_color = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.shape);
        change_stroke=10;
//        drawable_color.setStroke(change_stroke,change_color);
//        oval_image.setImageDrawable(drawable_color);
        for(MyPaintView p : paintViewManager.getMyPaintViewPool()){
            p.setStrokeWidth(change_stroke);
        }

        mColorSeekBar.setMaxPosition(100);
        mColorSeekBar.setThumbHeight(30);
        mColorSeekBar.setDisabledColor(Color.GRAY);
        mColorSeekBar.setOnInitDoneListener(new ColorSeekBar.OnInitDoneListener() {
            @Override
            public void done() {
                Log.i(TAG,"done!");
            }
        });

        mColorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) { // 색상 변경
                change_color = mColorSeekBar.getColor();
                textView.setTextColor(change_color); // 텍스트 색
                drawable_color.setStroke(change_stroke ,change_color); // 테두리 굵기/색
                oval_image.setImageDrawable(drawable_color); // 적용
                for(MyPaintView p : paintViewManager.getMyPaintViewPool()){
                    p.setColor(change_color);
                }
            }
        });



        strokeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // 굵기 변경
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                change_stroke=progress+10;
                drawable_color.setStroke(change_stroke,change_color); // 테두리 굵기/색
                oval_image.setImageDrawable(drawable_color); // 적용
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                for(MyPaintView p : paintViewManager.getMyPaintViewPool()){
                    p.setStrokeWidth(seekBar.getProgress());
                }
            }
        });
    }

    private void eraserSetup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewInDialog = inflater.inflate(R.layout.seek_bar, null);
        final AlertDialog penDialog = new AlertDialog.Builder(this).setView(viewInDialog).create();

        penDialog.show();
        Window window = penDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        final SeekBar mSeekBarPenWidth = (SeekBar) viewInDialog.findViewById(R.id.seekbar_stroke);
        final TextView widthStatus = (TextView) viewInDialog.findViewById(R.id.pen_width);
        mSeekBarPenWidth.setProgress((int)pc.getErasePaint().getStrokeMiter()); // thumb position
        widthStatus.setText("굵기: "+Float.toString((int)pc.getErasePaint().getStrokeMiter()));

        mSeekBarPenWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pc.mPaintSetStroke(seekBar.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                System.out.println("=============================================================================="+paintArr[0].getTempStroke());
//                seekBar.setProgress((int)paintArr[1].getTempStroke());
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                widthStatus.setText("굵기: "+progress);
            }
        });
    }
}


