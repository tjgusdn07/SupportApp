package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.models.Subject;
import com.example.myapplication.models.Target;
import com.example.myapplication.models.bottom_home_data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class bottom_home_fragment extends Fragment {
    private static final String TAG = "HomeNews";
    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter bottom_home_adapter;
    private ArrayList<bottom_home_data> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private GridLayoutManager gridLayoutManager;

    private ViewPager viewPager;
    private viewpager_FirstFragment fragment1;
    private viewpager_SecondFragment fragment2;
    private viewpager_ThirdFragment fragment3;
    private ArrayList<String> recoList;
    private FirebaseAuth mAuth;

    private clickListener frag_clickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_home_fragment, container, false);
        //recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);// 리사이클러뷰 id
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spanCount = 2; // columns
        int spacing = 60; // px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new ItemDecoration(spanCount, spacing, includeEdge));
        //layoutManager = new LinearLayoutManager(getContext());

        arrayList = new ArrayList<>();
        //viewpager
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        bottom_home_viewpager_adapter adapter = new bottom_home_viewpager_adapter(getChildFragmentManager());
        fragment1 = new viewpager_FirstFragment();
        adapter.addItem(fragment1);
        fragment2 = new viewpager_SecondFragment();
        adapter.addItem(fragment2);
        fragment3 = new viewpager_ThirdFragment();
        adapter.addItem(fragment3);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.top_category_click_main);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "버튼작동중");
                int num = 1;
                frag_clickListener.click(num);
            }
        });


        // 추천 목록 불러오기
        recoList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("reco")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            recoList.add(child.getKey());
                        }
                        loadRecoNews();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        // 기사 전체 메인페이지에 뿌리는 코드
//        database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReference("news");
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                arrayList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    bottom_home_data bottom_home_data = snapshot.getValue(bottom_home_data.class);
//                    arrayList.add(bottom_home_data);
//                }
//                bottom_home_adapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Fraglike", String.valueOf(error.toException())); //에러 시 출력
//            }
//        });
//
//        bottom_home_adapter = new bottom_home_adapter(arrayList, getContext());
//        recyclerView.setAdapter(bottom_home_adapter);
        return view;
    }

    public void loadRecoNews() {
        arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("news");
        for (String recommended : recoList) {
            databaseReference.orderByChild("name").equalTo(recommended).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue() != null) {
                            Log.d(TAG, "onDataChange: " + child);
                            bottom_home_data bottom_home_data = child.getValue(bottom_home_data.class);
                            arrayList.add(bottom_home_data);
                            bottom_home_adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Fraglike", String.valueOf(error.toException())); //에러 시 출력
                }
            });
        }

        bottom_home_adapter = new bottom_home_adapter(arrayList, getContext());
        recyclerView.setAdapter(bottom_home_adapter);
    }

    public interface clickListener {
        public void click(int num);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof clickListener) {
            frag_clickListener = (clickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        frag_clickListener = null;
    }
}