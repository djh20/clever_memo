package com.github.irshulx.wysiwyg.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.wysiwyg.Model.Category;
import com.github.irshulx.wysiwyg.Model.Memo;
import com.github.irshulx.wysiwyg.NLP.NLPManager;
import com.github.irshulx.wysiwyg.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
    NLPManager nlpManager;
    ArrayList<Category> categoryPool;
    ArrayList<Category> recommandedCategoryPool;
    Memo memo;
    int recommandedCategorySize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recommandedCategoryPool = (ArrayList<Category>) getIntent().getSerializableExtra("categoryPool");
        memo =(Memo) getIntent().getSerializableExtra("memo");
        if(recommandedCategoryPool == null)
            recommandedCategorySize = 0;
        else
            recommandedCategorySize = recommandedCategoryPool.size();

        Log.e("size", recommandedCategorySize + " ");
        //
        nlpManager = NLPManager.getInstance();
        categoryPool = nlpManager.getCategoryManager().getCategortPool();
        //
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

        //

        if(recommandedCategorySize != 0){
            for(int i = 0 ; i < recommandedCategoryPool.size() ; i++){
                if(i == 3)
                    break;
                addRecommandCategoryButton(i+1, recommandedCategoryPool.get(i));
            }
        }

        if(categoryPool.size() - recommandedCategorySize > 0)
            inputSpinner(recommandedCategoryPool);

        addNewCategoryButton();
        // 액티비티 바깥화면이 클릭되어도 종료되지 않게 설정하기
        this.setFinishOnTouchOutside(false);
    }

    public void addRecommandCategoryButton(int index, Category category){

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

        final TextView tv = new TextView(this);

        //TODO 카테고리 선택시 작동 코드써야함
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = tv.getText().toString();
                nlpManager.getCategoryManager().addMemoIntoCategory(memo, categoryName);
                finish();
            }
        });
        tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
        tv.setText(category.getCategoryName());
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
                        boolean isCorrect = false;
                        while(!isCorrect) {
                            String categorytName = editText.getText().toString();//
                            Category category = nlpManager.getCategoryManager().addNewCategory(categorytName);
                            if (category != null){
                                nlpManager.addMemoIntoCategory(memo, category);
                                Log.e("categoryadd", category.getCategoryName());
                                break;
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "이미 존재하는 카테고리 입니다, 다시입력해 주세요", Toast.LENGTH_LONG);
                            }
                        }
                        finish();
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

    public void inputSpinner(ArrayList<Category> recommandedCategoryPool){
        final ArrayList<Category> categoryPool = nlpManager.getCategoryManager().getCategortPool();

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

        int addedCategory = 0;
        for(int i = 0 ; i < recommandedCategoryPool.size() ; i++){
            Category recommandedCategory = recommandedCategoryPool.get(i);
            for(int j = 0 ; j < categoryPool.size() ; j++){
                Category category = categoryPool.get(j);
                if(category != recommandedCategory){
                    data.add(category.getCategoryName());
                    addedCategory++;
                }
            }
        }
        final int addedCategoryInSpinner = addedCategory;

        AdapterSpinner adapterSpinner = new AdapterSpinner(this,data);
        spinner.setAdapter(adapterSpinner);

        //TODO 카테고리 선택시 작용 추가
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedCategoryName = adapterView.getItemAtPosition(position).toString();
                nlpManager.getCategoryManager().addMemoIntoCategory(memo, selectedCategoryName);
                finish();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(layWidth/5*4, layHeight-10);
        spinnerParams.setMargins(layWidth/20,5,0,0);
        spinner.setLayoutParams(spinnerParams);
        layoutSpinner.addView(spinner);
        lay.addView(layoutSpinner);
        mid.addView(lay);

    }
}
