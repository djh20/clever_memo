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
import android.util.Log;
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
import com.github.irshulx.wysiwyg.Model.Memo;
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
    private Toolbar toolbar;
    private MainScreen main;

    public static final int OPEN_NEW_ACTIVITY = 10;

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
                                startActivityForResult(intent, OPEN_NEW_ACTIVITY);
                            }
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*default main screen*/
        main = new MainScreen();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, main)
                .commit();
        setDrawerBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_NEW_ACTIVITY) {
            setDrawerBar();
        }
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
        Log.e("dadsda", "hello");

        ArrayList<Category> categoryPool = nlpManager.getCategoryManager().getCategortPool();
        final Vector<ParentsModel> parentsModelPool = new Vector<ParentsModel>();
        Vector<CategoryStructure> categoryStructurePool = new Vector<CategoryStructure>();

        for(int i =  0 ; i < categoryPool.size() ; i++){
            Category category = categoryPool.get(i);
            Log.e("dadsda", "hi");
            if(category.getParent() == null){
                ParentsModel parentsModel = new ParentsModel(category.getCategoryName());
                ArrayList<Category> childCategoryPool = category.getChildCategoryPool();
                for(int j = 0 ; j < childCategoryPool.size() ; j++){
                    Category childCategory = childCategoryPool.get(j);
                    parentsModel.addChildModel(new ChildModel(childCategory.getCategoryName()));
                }
                parentsModelPool.add(parentsModel);
            }
        }

//        ArrayList<String> detail1nd = new ArrayList<>(Arrays.asList("C++","운영체제","네트워크","JAVA","LINUX"));
//        ArrayList<String> detail2nd = new ArrayList<>(Arrays.asList("취미","여행","드라마"));
//        CategoryStructure category1st = new CategoryStructure("공부");
//        CategoryStructure category2nd = new CategoryStructure("일기");
//        category1st.setDetailCategory(detail1nd);
//        category2nd.setDetailCategory(detail2nd);
//        System.out.println("======================================="+category1st.getName());
//
//        /*make a parentsModel*/
//        ParentsModel parents1st = new ParentsModel(category1st.getName(),R.drawable.add, true);
//        for(int i=0;i<category1st.getDetailSize();i++)
//            parents1st.addChildModel(new ChildModel(category1st.getDetailCategory().get(i)));
//
//        ParentsModel parents2nd = new ParentsModel(category2nd.getName(),R.drawable.add, true);
//        for(int i=0;i<category2nd.getDetailSize();i++)
//            parents2nd.addChildModel(new ChildModel(category2nd.getDetailCategory().get(i)));

        /* drawer */
//        List<String> items = new ArrayList<String>( // test
//                Arrays.asList("item0","item1"));

        navigationExpandableListView = (ExpandableNavigationListView) findViewById(R.id.expandable_navigation);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationExpandableListView.init(this); // category, item
        for(int i = 0 ; i < parentsModelPool.size() ; i++){
            navigationExpandableListView.addParentsModel(parentsModelPool.get(i));
        }
        navigationExpandableListView.addParentsModel(new ParentsModel("HOME"));
        navigationExpandableListView.build();
        final int numParentModel = parentsModelPool.size();

        navigationExpandableListView.addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // action of a selected category
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {



                        navigationExpandableListView.setSelected(groupPosition);
                        if(id == numParentModel){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, main)
                                    .commit();
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        else{
                            ParentsModel parentsModel = (ParentsModel) navigationExpandableListView.getItemAtPosition(groupPosition);
                            if(parentsModel.isHasChild() == true){
                                drawer.closeDrawer(GravityCompat.START); // close from right to left
                            }
                            else{
                                ArrayList<Memo> memoPool = nlpManager.getCategoryManager().getMemoPoolInCategory(parentsModel.getTitle());
                                if(memoPool != null) {
                                    String[] listItem = new String[memoPool.size()];
                                    for(int i = 0 ; i < memoPool.size() ; i++){
                                        listItem[i] = memoPool.get(i).getMemoName();
                                    }
                                    ItemListFragment itemListFragment = new ItemListFragment();
                                    items = listItem;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame, itemListFragment)
                                            .commit();
                                }
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        }

                        return false;
                    }
                });

        navigationExpandableListView.addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) { // action of a selecetd item after GroupAction
                        navigationExpandableListView.setSelected(groupPosition, childPosition);

                        ChildModel childModel = (ChildModel) navigationExpandableListView.getItemAtPosition(childPosition);
                        ArrayList<Memo> memoPool = nlpManager.getCategoryManager().getMemoPoolInCategory(childModel.getTitle());
                        if(memoPool != null) {
                            String[] listItem = new String[memoPool.size()];
                            for (int i = 0; i < memoPool.size(); i++) {
                                listItem[i] = memoPool.get(i).getMemoName();
                            }
                            ItemListFragment itemListFragment = new ItemListFragment();
                            items = listItem;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, itemListFragment)
                                    .commit();

                        }
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
                });
            navigationExpandableListView.expandGroup(2);
    }
}
