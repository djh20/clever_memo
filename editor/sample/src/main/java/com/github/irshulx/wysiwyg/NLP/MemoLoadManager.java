package com.github.irshulx.wysiwyg.NLP;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.Model.Category;
import com.github.irshulx.wysiwyg.Model.Memo;
import com.github.irshulx.wysiwyg.Model.Noun;
import com.github.irshulx.wysiwyg.R;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.BitmapManager;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.DrawContaioner;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.MyPaintView;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.PaintViewManager;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.SerialBitmap;
import com.github.irshulx.wysiwyg.Utilities.RealPathUtil;
import com.github.irshulx.wysiwyg.ui.CategorySelectActivity;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;


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
    boolean onTopUsing = false;
    boolean onBOtUsing = false;
    DatabaseManager databaseManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcanvas);
        databaseManager = DatabaseManager.getInstance();
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
                    if(scrollView.getScrollY() >= loadedPageIndex*pageh - pageh*5 && onBOtUsing == false){
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

    synchronized public int onTop() {
        if (loadedPageIndex == LOAD_PAGE-1)
            return 0;
        try {
            onTopUsing = true;
            //save
            String path = imageFilePath + "/" + memoName;
            final MyPaintView removeView = paintViewManager.getMyPaintViewById(loadedPageIndex + 1);
            if(removeView.isModified() == true)
                saveToDatabase(removeView.getDrawContaioner());
            Log.e("결과", bitmapManager.isExist(removeView.getSerialBitMap()) + "");
            bitmapManager.setUnUse(removeView.getDrawContaioner().getmBit().getBitmap(), true);

            final MyPaintView cleared = paintViewManager.getMyPaintViewById(loadedPageIndex  - LOAD_PAGE + 1);
            final DrawContaioner drawContaioner = loadPageFromDatabase(loadedPageIndex  - LOAD_PAGE);
            drawContaioner.createmPaint();
            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    cleared.setupToUse(getApplicationContext());
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
            Log.e("지울려고 하는 것", loadedPageIndex - LOAD_PAGE + 2 +"");
            onBOtUsing = true;
            final MyPaintView removeView = paintViewManager.getMyPaintViewById(loadedPageIndex - LOAD_PAGE + 2);
            if(removeView.isModified() == true)
                saveToDatabase(removeView.getDrawContaioner());
            bitmapManager.setUnUse(removeView.getDrawContaioner().getmBit().getBitmap(), true);

            final MyPaintView cleared = paintViewManager.getMyPaintViewById(loadedPageIndex +2);
            final DrawContaioner drawContaioner = loadPageFromDatabase(loadedPageIndex + 1);
            drawContaioner.createmPaint();
            (MemoLoadManager.this).runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    paintViewManager.replace(removeView, getApplicationContext());
                    cleared.setupToUse(getApplicationContext());
                    cleared.setDrawContaioner(drawContaioner);
                    cleared.setImageBitmap(cleared.getDrawContaioner().getmBit().getBitmap());
                    scroll.requestLayout();
                }
            });
            loadedPageIndex++;
            onBOtUsing = false;
        }
            catch (Exception e){
            Log.e("io", e.getMessage() + "");
            }
            return 0;
    }


    public DrawContaioner loadPageFromDatabase(int pageNum){
        try {
            Cursor cursor = databaseManager.selectSQL("SELECT * FROM MemoObject Where pageNum =" + pageNum);
            cursor.moveToNext();
            byte[] tmpBytes = cursor.getBlob(2);
            ByteArrayInputStream bais = new ByteArrayInputStream(tmpBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            DrawContaioner result = (DrawContaioner) ois.readObject();
            return result;
        }
        catch (Exception e){
            Log.e("터졋다..", e.getMessage() + "");
        }

        return null;
    }


    public void saveToDatabase(DrawContaioner drawContaioner){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(drawContaioner);
            ContentValues contentValues = new ContentValues();
            contentValues.put("memoName", memoName);
            contentValues.put("pageNum", drawContaioner.getPageNum());
            contentValues.put("data", baos.toByteArray());
            databaseManager.upsert("MemoObject", contentValues);
        } catch (Exception e) {
            Log.e("터졋다..", e.getMessage() + "");
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

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                            loadSavedPage(0,20);
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
                            loadSavedPage(0, LOAD_PAGE);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void loadSavedPage(final int startPage, final int toLoadPage) throws IOException, ClassNotFoundException {
        final String path = imageFilePath + "/" + memoName;
        File file = new File(path);
        final int pageNum = file.list().length;

        if(paintViewManager == null){
            DrawContaioner drawContaioner = loadPageFromDatabase(0);
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
                            final DrawContaioner drawContaioner = loadPageFromDatabase(i);
                            drawContaioner.createmPaint();
                            drawContaioner.setPageNum(i);

                            paintViewManager.setContainerById(viewId, drawContaioner);
                            paintViewManager.setupToUseById(viewId, getApplicationContext());
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

    void loadPdfAndSave() throws IOException, InterruptedException {

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
                        try {
                        int toLoadPage = j;
                            Log.e("save..", toLoadPage + "");
                            pdfiumCore.openPage(pdfDocument, toLoadPage);
                            final int width = pdfiumCore.getPageWidthPoint(pdfDocument, toLoadPage);
                            final int height = pdfiumCore.getPageHeightPoint(pdfDocument, toLoadPage);
                            final int bitMapWidth =scroll.getMeasuredWidth();;
                            final int bitMapHeight = Math.round(height * ((float) bitMapWidth / width));
                            SerialBitmap bitmap = bitmapManager.getUnUsingBitmap();
                            pagew = bitMapWidth;
                            pageh = bitMapHeight;
                            if(bitmap.getBitmap() == null)
                                bitmap.setBitmap(Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_8888));

                            pdfiumCore.renderPageBitmap(pdfDocument, bitmap.getBitmap(), toLoadPage, 0, 0,
                                    bitMapWidth, bitMapHeight);
                            DrawContaioner drawContaioner = new DrawContaioner();
                            drawContaioner.setDetail(bitmap, toLoadPage , bitMapWidth, bitMapHeight);
                            drawContaioner.setPageNum(toLoadPage);

                            saveToDatabase(drawContaioner);

                            bitmapManager.setUnUse(bitmap, false);

                            numLoadedPage++;
                            if (numLoadedPage == pdfCount) {
                                fd.close();
                                pdfiumCore.closeDocument(pdfDocument);
                            }

                            } catch (Exception e) {
                                Log.e("so", e.getMessage());
                            }
                        }
                }
            }).start();
        }

        Thread.sleep(3000);


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


    void saveObjectInMemory() {
        try {
            for(int i = loadedPageIndex + 1; i > loadedPageIndex - LOAD_PAGE ; i--){
                MyPaintView myPaintView = paintViewManager.getMyPaintViewById(i);
                if(myPaintView.isModified() == true)
                    saveToDatabase(myPaintView.getDrawContaioner());
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
                for(MyPaintView p : paintViewManager.getMyPaintViewPool()){
                    if(p.getDrawContaioner() != null)
                        p.setEraseMode();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showWriteSetup() {
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

    public void setDefaultPen(int color, int i, int i1) {


    }
}

