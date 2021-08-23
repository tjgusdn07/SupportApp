package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.models.Subject;
import com.example.myapplication.models.Target;
import com.example.myapplication.models.bottom_home_data;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class newsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, adapter2activity {

    private ArrayList<bottom_home_data> arrayList;
    private Intent intent;
    private View view;
    private ArrayList<Target> targetList = new ArrayList<>();
    private RecyclerView recyclerview;
    private ArrayList<Subject> subjectList = new ArrayList<>();
    private MainAdapter MainAdapter;
    private DrawerLayout drawerLayout;
    private View drawer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        TextView textView1 = findViewById(R.id.textView1);
        ImageView imageView = findViewById(R.id.news_detail);
        TextView textView2 = findViewById(R.id.textView2);

        intent = getIntent();
        textView1.setText(intent.getStringExtra("title"));
        Glide.with(this).load(intent.getStringExtra("image")).into(imageView);
        textView2.setText(intent.getStringExtra("content"));

        String name = intent.getStringExtra("name");

        Button support_button = findViewById(R.id.support_button);
        support_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), profileActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
                finish();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.top_category_layout_news);
        drawer = (View) findViewById(R.id.category_drawer_news);
        ImageButton imageButton = (ImageButton)findViewById(R.id.top_category_click_news);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawer);
            }
        });

        //카테고리 recycler
        recyclerview = findViewById(R.id.top_category_view_news);
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
                        MainAdapter = new MainAdapter(subjectList,newsActivity.this);
                        recyclerview.setAdapter(MainAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
        // DB에서 후원대상 불러오기
        FirebaseDatabase.getInstance().getReference("target").orderByChild("name")  // 나중에는 orderbychild 붙여서
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            targetList.add(new Target(dataSnapshot.child("name").getValue().toString()));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void addItem(int type, int position) {

    }

    @Override
    public void deleteItem(int type, int position) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}

