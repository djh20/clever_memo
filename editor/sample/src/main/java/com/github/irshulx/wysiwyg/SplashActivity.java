package com.github.irshulx.wysiwyg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.NLP.Twitter;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, FirstActivity.class);
        Twitter twitter = new Twitter(); // 트위터 객체 생성 및 put
        intent.putExtra("twitter", twitter);
        DatabaseManager dbManager = new DatabaseManager(getApplicationContext());

        dbManager.insertSQL("insert into Word (word, globalFrequency, docFrequency) values ('Banana', 1 , 1)");
        dbManager.insertSQL("insert into Word (word, globalFrequency, docFrequency) values ('김해준자지', 1 , 1)");
        dbManager.insertSQL("insert into Word (word, globalFrequency, docFrequency) values ('김해준후장', 1 , 1)");
        dbManager.selectSQL("select * from Word");

        startActivity(intent);
        finish();
    }
}
