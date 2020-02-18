package com.github.irshulx.wysiwyg.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.github.irshulx.wysiwyg.R;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectActivity extends Activity {
    Context context;
    int numCategory = 0;
    final int numFixMenu = 2;
    int width;
    int boxWidth;
    final int boxHeight = 250;
    LinearLayout top;
    LinearLayout mid;
    int layWidth;
    int layHeight = 150;
    LinearLayout.LayoutParams lplay;
    LinearLayout.LayoutParams lptv;
    LinearLayout.LayoutParams lp;
    GradientDrawable drawable ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_select);
        context = this;
        mid = (LinearLayout) findViewById(R.id.linearMid);
        top = (LinearLayout) findViewById(R.id.linearTop);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        // 배경 블러 처리
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;

        numCategory = 5;
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels;
        boxWidth = (int)(width/1.3);
        layWidth = boxWidth*2/3;

        lplay = new LinearLayout.LayoutParams(layWidth, layHeight);
        lplay.setMargins(0,27,0,25);

        lptv = new LinearLayout.LayoutParams(layWidth/5-10, layHeight);
        lptv.setMargins(0,0,10,0);

        lp = new LinearLayout.LayoutParams(layWidth/5*4, layHeight);

        drawable = new GradientDrawable();
        drawable.setStroke(5, Color.GRAY);
        drawable.setCornerRadius(15);


        Display dp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        // 3. 현재 화면에 적용
        getWindow().getAttributes().width = boxWidth;
        getWindow().getAttributes().height = (numCategory + numFixMenu)*boxHeight + (int)(((numCategory + numFixMenu)*boxHeight)*0.1);
        addRecommandCategoryButton(1);
        addRecommandCategoryButton(2);
        addRecommandCategoryButton(3);
        inputSpinner();
        addNewCategoryButton();
        // 액티비티 바깥화면이 클릭되어도 종료되지 않게 설정하기
        this.setFinishOnTouchOutside(false);
    }

    public void addRecommandCategoryButton(int index){

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.HORIZONTAL);
        lay.setLayoutParams(lplay);

        ImageView imageView = new ImageView(this);
        switch (index){
            case 1:
                imageView.setImageResource(R.drawable.ic_looks_one_black_24dp);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_looks_two_black_24dp);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_looks_3_black_24dp);
                break;
        }

        imageView.setLayoutParams(lptv);
        lay.addView(imageView);

        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
        tv.setText("test");
        tv.setTextSize(20);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(lp);
        tv.setBackgroundDrawable(drawable);
        lay.addView(tv);
        mid.addView(lay);
    }

    public void addNewCategoryButton(){

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.HORIZONTAL);
        lay.setLayoutParams(lplay);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_mode_edit_black_24dp);
        imageView.setLayoutParams(lptv);
        lay.addView(imageView);

        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
        tv.setText("새 카테고리 입력");
        tv.setTextSize(20);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(lp);
        tv.setBackgroundDrawable(drawable);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {        // create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("새 카테고리를 입력해주세요");
                builder.setIcon(R.drawable.category_icon);
                final View customLayout = getLayoutInflater().inflate(R.layout.category_dialog, null);
                builder.setView(customLayout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = customLayout.findViewById(R.id.categorydialog_editText);
                        editText.getText();//겟
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        tv.setBackgroundDrawable(drawable);
        lay.addView(tv);
        mid.addView(lay);
    }

    public void inputSpinner(){
        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.HORIZONTAL);
        lay.setLayoutParams(lplay);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_format_list_numbered_black_24dp);
        imageView.setLayoutParams(lptv);
        lay.addView(imageView);

        LinearLayout layoutSpinner = new LinearLayout(this);
        layoutSpinner.setLayoutParams(lp);
        layoutSpinner.setBackgroundDrawable(drawable);

        Spinner spinner = new Spinner(this);

        List<String> data = new ArrayList<>();
        data.add("가짜 데이터 1"); data.add("가짜 데이터 2"); data.add("가짜 데이터 3"); data.add("가짜 데이터 4"); data.add("가짜 데이터 5");
        data.add("가짜 데이터 6"); data.add("가짜 데이터 7"); data.add("가짜 데이터 8"); data.add("가짜 데이터 9"); data.add("가짜 데이터 10");
        AdapterSpinner adapterSpinner = new AdapterSpinner(this,data);
        spinner.setAdapter(adapterSpinner);
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(layWidth/5*4, layHeight-10);
        spinnerParams.setMargins(layWidth/20,5,0,0);
        spinner.setLayoutParams(spinnerParams);
        layoutSpinner.addView(spinner);
        lay.addView(layoutSpinner);
        mid.addView(lay);

    }
}
