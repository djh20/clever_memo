package com.github.irshulx.wysiwyg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.irshulx.wysiwyg.NLP.Twitter;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Twitter twit = new Twitter();

            Intent intent = new Intent(SplashActivity.this, // splash 이후 main호출
                    FirstActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 자동 애니메이션 삭제
            startActivity(intent);

            SplashActivity.this.finish();

        } catch (Exception e) {

        } finally {
            SplashActivity.this.finish();
        }
    }

}
