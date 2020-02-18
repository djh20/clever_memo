package com.github.irshulx.wysiwyg.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.irshulx.wysiwyg.R;

import org.w3c.dom.Text;

public class CategorySelectActivity extends Activity {
    int numCategory = 0;
    final int numFixMenu = 2;
    int width;
    int boxWidth = (int)(width/1.3);
    final int boxHeight = 250;
    LinearLayout top;
    LinearLayout mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_select);

        mid = (LinearLayout) findViewById(R.id.linearMid);
        top = (LinearLayout) findViewById(R.id.linearTop);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        // 배경 블러 처리
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;

        numCategory = 5;
        //width =

        Display dp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        // 3. 현재 화면에 적용
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = (numCategory + numFixMenu)*boxHeight + (int)(((numCategory + numFixMenu)*boxHeight)*0.1);
        addNewCategoryButton();
        addNewCategoryButton();
        addRecommandCategoryButton();
        addRecommandCategoryButton();
        addRecommandCategoryButton();
        // 액티비티 바깥화면이 클릭되어도 종료되지 않게 설정하기
        this.setFinishOnTouchOutside(false);
    }

    public void addRecommandCategoryButton(){
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
        tv.setText("test");
        tv.setTextSize(20);
        tv.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(boxWidth), 150);
        lp.setMargins(0,25,0,25);
        tv.setLayoutParams(lp);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(5, Color.GRAY);
        tv.setBackgroundDrawable(drawable);
        mid.addView(tv);
    }
    
    public void addNewCategoryButton(){
        EditText tv = new EditText(this);
        tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
        tv.setHint("새로운 카테고리");
        tv.setTextSize(20);
        tv.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(boxWidth), 150);
        lp.setMargins(0,25,0,25);
        tv.setLayoutParams(lp);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(5, Color.GRAY);
        tv.setBackgroundDrawable(drawable);
        mid.addView(tv);
    }



}
