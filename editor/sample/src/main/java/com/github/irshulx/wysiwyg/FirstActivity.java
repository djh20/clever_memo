package com.github.irshulx.wysiwyg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.util.LogPrinter;
import android.view.MenuItem;
import android.view.View;

import android.view.Menu;
import android.widget.ExpandableListView;

import com.github.irshulx.wysiwyg.Model.ChildModel;
import com.github.irshulx.wysiwyg.Model.ParentsModel;
import com.github.irshulx.wysiwyg.ui.drawer.ExpandableNavigationListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FirstActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private NavigationView navigationView;
    private ExpandableNavigationListView navigationExpandableListView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        final Context context = FirstActivity.this;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        /* fab */
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
                            startActivity(new Intent(getApplicationContext(),PDFCanvas.class));
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        /* drawer */
        List<String> items = new ArrayList<String>( // test
                Arrays.asList("item0","item1"));

        navigationExpandableListView = (ExpandableNavigationListView) findViewById(R.id.expandable_navigation);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ParentsModel categoryModel = new ParentsModel("Test2", R.drawable.add, true);
        for(int i=0;i<items.size();i++){
            categoryModel.addChildModel(new ChildModel(items.get(i)));
        }

        navigationExpandableListView // category, item
                .init(this)
                .addParentsModel(new ParentsModel("Test0"))
                .addParentsModel(new ParentsModel("Test1", R.drawable.newbackground, false, true, false))
                .addParentsModel(categoryModel)
                .addParentsModel(new ParentsModel("Test3"))
                .addParentsModel(new ParentsModel("Test4"))
                .addParentsModel(new ParentsModel("Test5"))
                .build()
                .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // action of a selected category
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        navigationExpandableListView.setSelected(groupPosition);

                        //drawer.closeDrawer(GravityCompat.START);
                        if (id == 0) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 1) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 2) {
//                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 3) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 4) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 5) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        return false;
                    }
                })
                .addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) { // action of a selecetd item after GroupAction
                        navigationExpandableListView.setSelected(groupPosition, childPosition);

                        if (id == 0) {
                        } else if (id == 1) {
                        } else if (id == 2) {
                        } else if (id == 3) {
                        }
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
                });
        navigationExpandableListView.expandGroup(2);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


