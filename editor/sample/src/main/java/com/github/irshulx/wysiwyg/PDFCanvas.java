package com.github.irshulx.wysiwyg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;


public class PDFCanvas extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    Uri uri;
    int pagew, pageh;
    MyPaintView paintArr[];
    int pdfCount;
    int lastTouch;
    File storage; //내부저장소 경로
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcanvas);
        storage = getCacheDir();
        showFileChooser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.pdfmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {

        switch(item.getItemId())
        {
            case R.id.pdfLoad:
                showFileChooser();
                break;
            case R.id.stroke:
                show();
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
            case R.id.save:
                showSaveDialog();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showSaveDialog(){
        Log.e("진입","showdialog");
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save");
        saveDialog.setMessage("저장할 메모의 제목을 입력해주세요");
        final EditText et = new EditText(this);
        saveDialog.setView(et);

        saveDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = et.getText().toString();
                File dir = new File(storage,value);
                if(!dir.exists())
                    dir.mkdirs();
                Log.e("경로",dir.getAbsolutePath());
                for(int i = 0 ; i < pdfCount ; i++)
                {
                    saveBitmapToPNG(paintArr[i].getCanvasBit(), i+"", dir);//각 페이지의 캔버스마다 bitmap
                }
                dialog.dismiss();
            }
        });

        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        saveDialog.show();



    }

    private void saveBitmapToPNG(Bitmap bitmap, String name, File dir) {

        String fileName = name + ".png";

        File tempFile = new File(dir, fileName);

        try {
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

        } catch (FileNotFoundException e) {
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("MyTag","IOException : " + e.getMessage());
        }
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                }
                break;
        }

        openPdf();

        super.onActivityResult(requestCode, resultCode, data);
    }

    int tColor=0;

    void openPdf() {
        RelativeLayout scroll = findViewById(R.id.scroll);
        ParcelFileDescriptor fd = null;
        try {
            fd = this.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ;
        int pageNum = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfCount = pdfiumCore.getPageCount(pdfDocument);
            paintArr = new MyPaintView[pdfCount];
            for(int i = 0 ; i < pdfCount ; i++){
                pdfiumCore.openPage(pdfDocument, pageNum);

                int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
                int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

                // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
                // RGB_565 - little worse quality, twice less memory usage


                if(width>0&&height>0){
                    pagew = scroll.getMeasuredWidth()-50;
                    pageh = height*pagew/width;
                    Bitmap bitmap = Bitmap.createBitmap(pagew, pageh, Bitmap.Config.RGB_565);

                    pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0, pagew, pageh);

                    MyPaintView img = new MyPaintView(this,null,bitmap);
                    img.setId(i);
                    paintArr[i] = img;
                    final int lastNum = i;
                    img.setOnTouchListener(new View.OnTouchListener(){
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            lastTouch = lastNum;
                            return false;
                        }
                    });
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, i-1);
                    img.setLayoutParams(layoutParams);
                    scroll.addView(img);
                    pageNum++;
                }
            }
            Log.e("",""+pdfiumCore.getPageCount(pdfDocument));
            printInfo(pdfiumCore, pdfDocument);
            pdfiumCore.closeDocument(pdfDocument);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printInfo(PdfiumCore core, PdfDocument doc) {
        PdfDocument.Meta meta = core.getDocumentMeta(doc);
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(core.getTableOfContents(doc), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    private void show() {
        final EditText editText=new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setMessage("굵기 입력");
        builder.setView(editText);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0 ; i < pdfCount ; i++)
                        {
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
                for(int i = 0 ; i < pdfCount ; i++)
                {
                    paintArr[i].setColor(color);
                }
            }
        });
        colorPicker.show();
    }

    class MyPaintView extends View {
        private Canvas mCanvas;
        private Path mPath;
        private Paint mPaint;
        private Bitmap mBit;
        private ArrayList<Path> paths = new ArrayList<Path>();
        private ArrayList<Path> undonePaths = new ArrayList<Path>();
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private Bitmap canvasBit;

        public MyPaintView(Context context, AttributeSet attributeSet, Bitmap mBit) {
            super(context, attributeSet);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.BLACK);
            this.mBit = mBit;
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(6);
            mPath = new Path();
            canvasBit = Bitmap.createBitmap(pagew,pageh,Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(canvasBit);
        }
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int height = pageh;
            int width = pagew;
            setMeasuredDimension(width, height);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mBit != null) {
                canvas.drawBitmap(mBit, 0, 0, null);
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            super.onDraw(canvas);
            for (Path p : paths){
                canvas.drawPath(p, mPaint);
            }
            canvas.drawPath(mPath, mPaint);
        }

        public void onClickUndo () {
            if (paths.size()>0) {
                undonePaths.add(paths.remove(paths.size()-1));
                invalidate();
            }else{
            }
        }

        public void onClickRedo (){
            if (undonePaths.size()>0){
                paths.add(undonePaths.remove(undonePaths.size()-1));
                invalidate();
            }else {
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    undonePaths.clear();
                    mPath.reset();
                    mPath.moveTo(x, y);
                    mX = x;
                    mY = y;
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                        mX = x;
                        mY = y;
                    }
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    mPath.lineTo(mX, mY);
                    mCanvas.drawPath(mPath, mPaint);
                    paths.add(mPath);
                    mPath = new Path();
                    invalidate();
                    break;
            }
            return true;
        }
        public void setStrokeWidth(int width){
            mPaint.setStrokeWidth(width);
        }


        public void setColor(int color){
            mPaint.setColor(color);
        }

        public Bitmap getCanvasBit(){
            Canvas saveCanvas = new Canvas(mBit);
            saveCanvas.drawBitmap(canvasBit, 0, 0, null);
            return mBit;
        }
    }
}