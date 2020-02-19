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
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.ExpandableListView;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.ListModel.CategoryStructure;
import com.github.irshulx.wysiwyg.ListModel.ChildModel;
import com.github.irshulx.wysiwyg.ListModel.ParentsModel;
import com.github.irshulx.wysiwyg.Model.Category;
import com.github.irshulx.wysiwyg.NLP.MemoLoadManager;
import com.github.irshulx.wysiwyg.NLP.NLPManager;
import com.github.irshulx.wysiwyg.ui.CategorySelectActivity;
import com.github.irshulx.wysiwyg.ui.MainScreen;
import com.github.irshulx.wysiwyg.ui.drawer.ExpandableNavigationListView;
import com.github.irshulx.wysiwyg.ui.itemList.ItemListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;


public class FirstActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private ExpandableNavigationListView navigationExpandableListView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NLPManager nlpManager;
    private String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nlpManager = NLPManager.getInstance();
        setContentView(R.layout.activity_first);

        FloatingActionButton fab = findViewById(R.id.fab);
        final Context c = this;
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
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(FirstActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                } else {
                                    ActivityCompat.requestPermissions(FirstActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            1);

                                }
                            }
                            else{
                                Intent intent = new Intent(getApplicationContext(), MemoLoadManager.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        setDrawerBar();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
////        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
////        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
////                || super.onSupportNavigateUp();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings :
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String[] getData(){
        return items;
    }

    public void setDrawerBar(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*default main screen*/
        final MainScreen main = new MainScreen();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, main)
                .commit();

        Vector<Category> categoryPool = nlpManager.getCategoryManager().getCategortPool();
        Vector<ParentsModel> parentsModelPool = new Vector<ParentsModel>();
        Vector<ChildModel> childModelPool = new Vector<ChildModel>();
        Vector<CategoryStructure> categoryStructurePool = new Vector<CategoryStructure>();


        for(int i =  0 ; i < categoryPool.size() ; i++){
            Category category = categoryPool.get(i);
            Vector<Category> childCategoryPool = category.getChildCategoryPool();
            if(childCategoryPool.size() != 0){

            }
            else{



            }



                CategoryStructure categoryStructure = new CategoryStructure(category.getCategoryName());
                ParentsModel parent = new ParentsModel(categoryStructure.getName(),R.drawable.add, true);
                parentsModelPool.add(parent);
                categoryStructurePool.add(categoryStructure);
        }


        ArrayList<String> detail1nd = new ArrayList<>(Arrays.asList("C++","운영체제","네트워크","JAVA","LINUX"));
        ArrayList<String> detail2nd = new ArrayList<>(Arrays.asList("취미","여행","드라마"));
        CategoryStructure category1st = new CategoryStructure("공부");
        CategoryStructure category2nd = new CategoryStructure("일기");
        category1st.setDetailCategory(detail1nd);
        category2nd.setDetailCategory(detail2nd);
        System.out.println("======================================="+category1st.getName());

        /*make a parentsModel*/
        ParentsModel parents1st = new ParentsModel(category1st.getName(),R.drawable.add, true);
        for(int i=0;i<category1st.getDetailSize();i++)
            parents1st.addChildModel(new ChildModel(category1st.getDetailCategory().get(i)));

        ParentsModel parents2nd = new ParentsModel(category2nd.getName(),R.drawable.add, true);
        for(int i=0;i<category2nd.getDetailSize();i++)
            parents2nd.addChildModel(new ChildModel(category2nd.getDetailCategory().get(i)));

        /* drawer */
//        List<String> items = new ArrayList<String>( // test
//                Arrays.asList("item0","item1"));

        navigationExpandableListView = (ExpandableNavigationListView) findViewById(R.id.expandable_navigation);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationExpandableListView // category, item
                .init(this)
//                .addParentsModel(new ParentsModel("Test0"))
//                .addParentsModel(new ParentsModel("Test1", R.drawable.newbackground, false, true, false))
//                .addParentsModel(categoryModel)
                .addParentsModel(parents1st)
                .addParentsModel(parents2nd)
                .addParentsModel(new ParentsModel("HOME"))
                .build()
                .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // action of a selected category
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        navigationExpandableListView.setSelected(groupPosition);

                        //drawer.closeDrawer(GravityCompat.START);
                        if (id == 0) {
//                            drawer.closeDrawer(GravityCompat.START); // close from right to left
                        } else if (id == 1) {
//                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 2) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, main)
                                    .commit();
                            drawer.closeDrawer(GravityCompat.START);
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
                        String[] listItem = {Integer.toString(groupPosition), Integer.toString(childPosition)};
                        if (id == 0) {
                            ItemListFragment itemListFragment = new ItemListFragment();
                            items = listItem;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, itemListFragment)
                                    .commit();
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
}
