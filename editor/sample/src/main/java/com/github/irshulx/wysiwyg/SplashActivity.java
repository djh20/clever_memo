package com.github.irshulx.wysiwyg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.NLP.NLPManager;
import com.github.irshulx.wysiwyg.NLP.Twitter;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.BitmapManager;
import com.github.irshulx.wysiwyg.Utilities.DrawManager.PaintConfig;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        Handler handler = new Handler();
        PaintConfig.getInstance();
        handler.post(new Runnable() {
            @Override
            public void run() {

                ImageView logo = (ImageView) findViewById(R.id.logo);
                Animation splashAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_animation_show);
                logo.startAnimation(splashAnim);
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Twitter twitter = new Twitter(); // 트위터 객체 생성 및 put
                        NLPManager nlpManager = NLPManager.getInstance(twitter);
                        BitmapManager.getInstance();
                        DatabaseManager databaseManager = DatabaseManager.getInstance(SplashActivity.this);
                        Intent intent = new Intent(SplashActivity.this, FirstActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                });
            }
        });
    }
}
