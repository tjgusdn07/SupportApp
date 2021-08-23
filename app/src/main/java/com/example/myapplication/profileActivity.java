package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.models.Subject;
import com.example.myapplication.models.Target;
import com.example.myapplication.models.bottom_home_data;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class profileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, adapter2activity {

    private static final String TAG = "ProfilePage";
    private Intent intent;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<bottom_home_data> arrayList2;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter profile_adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String targetKey;
    private String name;
    private boolean is_liked = false;
    private ArrayList<Target> targetList = new ArrayList<>();
    private RecyclerView recyclerview;
    private ArrayList<Subject> subjectList = new ArrayList<>();
    private MainAdapter MainAdapter;
    private DrawerLayout drawerLayout;
    private View drawer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        intent = getIntent();
        name = intent.getStringExtra("name");

        drawerLayout = (DrawerLayout) findViewById(R.id.top_category_layout_profile);
        drawer = (View) findViewById(R.id.category_drawer_profile);
        ImageButton imageButton = (ImageButton)findViewById(R.id.top_category_click_profile);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawer);
            }
        });

        //카테고리 recycler
        recyclerview = findViewById(R.id.top_category_view_profile);
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
                        MainAdapter = new MainAdapter(subjectList,profileActivity.this);
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


        TextView textView = findViewById(R.id.profile_top_name);
        textView.setText(name + " 프로필");

        TextView tv_profile_birth = findViewById(R.id.tv_profile_birth);
        ImageView imageView = findViewById(R.id.img_profile);
        TextView tv_profile_debut = findViewById(R.id.tv_profile_debut);
        TextView tv_profile_intro = findViewById(R.id.tv_profile_intro);
        TextView tv_profile_name = findViewById(R.id.tv_profile_name);
        TextView tv_profile_sns = findViewById(R.id.tv_profile_sns);
        TextView tv_profile_team = findViewById(R.id.tv_profile_team);


        database = FirebaseDatabase.getInstance();
        database.getReference("target").orderByChild("name")
                .equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot this_target : snapshot.getChildren()) {
                    targetKey = this_target.getKey();
                    String birth = this_target.child("birth").getValue().toString();
                    String debut = this_target.child("debut").getValue().toString();
                    String icon = this_target.child("icon").getValue().toString();
                    String intro = this_target.child("intro").getValue().toString();
                    String sns = this_target.child("sns").getValue().toString();
                    String team = this_target.child("team").getValue().toString();

                    tv_profile_birth.setText(birth);
                    tv_profile_debut.setText(debut);
                    tv_profile_intro.setText(intro);
                    tv_profile_name.setText(name);
                    tv_profile_sns.setText(sns);
                    tv_profile_team.setText(team);
                    Glide.with(profileActivity.this).load(icon).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        Button btn_profile_like = findViewById(R.id.btn_profile_like);
        mDatabase.child(mAuth.getUid()).child("like").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    btn_profile_like.setText("찜 취소");
                    is_liked = true;
                } else {
                    btn_profile_like.setText("찜하기");
                    is_liked = false;
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        btn_profile_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeClicked(mAuth.getUid(), targetKey, is_liked);
            }
        });


        Button btn_profile_board = findViewById(R.id.btn_profile_board);
        btn_profile_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profileActivity.this, BoardListActivity.class);
                intent.putExtra("name1", name);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.profile_recycler);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spanCount = 2; // columns
        int spacing = 60; // px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new ItemDecoration(spanCount, spacing, includeEdge));

        arrayList2 = new ArrayList<>();
        databaseReference = database.getReference("target").child(name).child("news");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList2.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    bottom_home_data bottom_home_data = snapshot.getValue(bottom_home_data.class);
                    arrayList2.add(bottom_home_data);
                }
                profile_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Fraglike", String.valueOf(error.toException())); //에러 시 출력
            }
        });
        profile_adapter = new bottom_home_adapter(arrayList2, this);
        recyclerView.setAdapter(profile_adapter);


        Button btn2 = findViewById(R.id.btn_profile_dona);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profileActivity.this, support_popupActivity.class);
                intent.putExtra("name1", name);
                startActivity(intent);
            }
        });
    }

    public void onLikeClicked(String uid, String target, boolean is_liked) {
        if (!is_liked) {
            FirebaseDatabase.getInstance().getReference("target").child(target)
                    .child("subject").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        mDatabase.child(uid).child("like").child(dataSnapshot.child("lCategory").getValue().toString()).setValue(true);
                        mDatabase.child(uid).child("like").child(dataSnapshot.child("mCategory").getValue().toString()).setValue(true);
                        mDatabase.child(uid).child("like").child(dataSnapshot.child("sCategory").getValue().toString()).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });
            mDatabase.child(uid).child("like").child(name).setValue(true);
        } else {
            mDatabase.child(uid).child("like").child(name).setValue(null);
        }
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
