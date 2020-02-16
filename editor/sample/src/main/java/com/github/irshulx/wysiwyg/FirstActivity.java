package com.github.irshulx.wysiwyg;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.NLP.NLPManager;
import com.github.irshulx.wysiwyg.NLP.NewMemoLoadManager;
import com.github.irshulx.wysiwyg.NLP.Twitter;
import com.github.irshulx.wysiwyg.ui.CategorySelectActivity;


public class FirstActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NLPManager nlpManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, CategorySelectActivity.class);
        startActivity(intent);

        nlpManager = NLPManager.getInstance();
        setContentView(R.layout.activity_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        final Context c = this;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setTitle("메모 작성 방법을 선택해주세요.");

                builder.setItems(R.array.Select, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0)
                        {
                            startActivity(new Intent(getApplicationContext(),EditorTestActivity.class));
                        }
                        else
                        {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(FirstActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                } else {
                                    ActivityCompat.requestPermissions(FirstActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            1);

                                }
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), NewMemoLoadManager.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.w
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                 R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_tree :
                Fragment tree = getSupportFragmentManager().findFragmentById(R.id.tree);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if(tree.isHidden())
                {
                    ft.show(tree);
                }
                else
                {
                    ft.hide(tree);
                }
                ft.commit();
                return true;
            case R.id.action_settings :
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
