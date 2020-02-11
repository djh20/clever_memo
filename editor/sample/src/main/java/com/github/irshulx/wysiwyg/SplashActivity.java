package com.github.irshulx.wysiwyg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.irshulx.wysiwyg.NLP.Twitter;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, FirstActivity.class);
        Twitter twitter = new Twitter(); // 트위터 객체 생성 및 put
        intent.putExtra("twitter", twitter);


        startActivity(intent);
        finish();
    }
}
