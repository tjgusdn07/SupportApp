package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Subject;
import com.example.myapplication.models.Target;
import com.example.myapplication.register.SurveyActivity;
import com.example.myapplication.searching.TargetSearch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import com.kakao.sdk.common.util.Utility;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, adapter2activity,
        bottom_home_fragment.clickListener, bottom_event_fragment.clickListener, bottom_favorite_fragment.clickListener, bottom_reco_fragment.clickListener {

    private static final String TAG = "MainPage";
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private bottom_home_fragment frag1;
    private bottom_event_fragment frag2;
    private bottom_favorite_fragment frag3;
    private bottom_reco_fragment frag4;
    private bottom_setting_fragment frag5;
    private MenuItem top_menu_Search;
    private ArrayList<Target> targetList = new ArrayList<>();
    private RecyclerView recyclerview;
    private ArrayList<Subject> subjectList = new ArrayList<>();
    private MainAdapter MainAdapter;
    private DrawerLayout drawerLayout;
    private View drawer;
    private View searchDrawer;

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_menu, menu);
        top_menu_Search = menu.findItem(R.id.top_action_search);
        SearchView searchView = (SearchView) top_menu_Search.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("???????????? ??????????????????.");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_action_category: {
                return true;
            }
            case R.id.top_action_search: {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }*/

    // ??????????????? ?????? ?????? ????????? ????????? ?????? ??????
    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            //super.onBackPressed();
            // ?????? ?????? ?????? ????????? ????????? ?????? ?????? ?????? ?????? ?????? ??????

            // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ?????? ?????? ????????? ?????? ???
            // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ???????????? Toast ??????
            // 2000 milliseconds = 2 seconds
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "?????? ?????? ????????? ??? ??? ??? ???????????? ???????????????.", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ?????? ?????? ????????? ?????? ???
            // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ????????? ???????????? ??????
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish();
                toast.cancel();
                toast = Toast.makeText(this, "????????? ????????? ???????????????.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("Debug", Utility.INSTANCE.getKeyHash(this));

        /*//Hooks
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.top_category_layout);
        //NavigationView navigationView = (NavigationView) findViewById(R.id.top_category_view);
        //Toolbar
        setSupportActionBar(toolbar);
        //Actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        //Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();*/
        //bottomNavigation
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_search:
                        setFrag(1);
                        break;
                    case R.id.action_favorite:
                        setFrag(2);
                        break;
                    case R.id.action_reco:
                        setFrag(3);
                        break;
                    case R.id.action_setting:
                        setFrag(4);
                        break;
                }
                return true;
            }
        });
        frag1 = new bottom_home_fragment();
        frag2 = new bottom_event_fragment();
        frag3 = new bottom_favorite_fragment();
        frag4 = new bottom_reco_fragment();
        frag5 = new bottom_setting_fragment();
        setFrag(0); // ?????? ????????? 0

        // drawer ??????
        drawerLayout = (DrawerLayout) findViewById(R.id.top_category_layout);
        searchDrawer = new TargetSearch(this).searchDrawer; // ?????? drawer view load ??? ?????? ??????

        //???????????? recycler
        recyclerview = findViewById(R.id.top_category_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        FirebaseDatabase.getInstance().getReference("Subject").orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot parentSubject : snapshot.getChildren()) {
                            subjectList.add(new Subject(Subject.HEADER, parentSubject.getKey()));

                            for (DataSnapshot childSubject : parentSubject.getChildren()) {
                                subjectList.add(new Subject(Subject.CHILD, childSubject.getValue().toString()));
                            }
                        }
                        MainAdapter = new MainAdapter(subjectList, MainActivity.this);
                        recyclerview.setAdapter(MainAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
    }

    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n) {
            case 0:
                ft.replace(R.id.main_frame, frag1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, frag2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, frag3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, frag4);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.main_frame, frag5);
                ft.commit();
                break;
        }
    }

    @Override
    public void addItem(int type, int position) {

    }

    @Override
    public void deleteItem(int type, int position) {

    }

    @Override
    public void click(int num) {
        if(num == 1) {
            drawer = (View) findViewById(R.id.category_drawer);
            drawerLayout.openDrawer(drawer);
        } else if (num == 2) {
            // ??????
            drawerLayout.openDrawer(searchDrawer);
        }
    }
}
