package com.github.irshulx.wysiwyg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(2000); //대기 초 설정
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, FirstActivity.class));
        finish();
    }
}
