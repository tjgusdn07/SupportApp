package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.bottom_favorite_profile_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class bottom_reco_fragment extends Fragment {
    private static final String TAG = "recoPage";
    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter bottom_reco_profile_adapter;
    private ArrayList<bottom_favorite_profile_model> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private GridLayoutManager gridLayoutManager;
    private FirebaseAuth mAuth;
    private ArrayList<String> likeList;
    private bottom_home_fragment.clickListener frag_clickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        view = inflater.inflate(R.layout.bottom_reco_fragment, container, false);
        //recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.bottom_reco_recyclerview);// 리사이클러뷰 id
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spanCount = 2; // columns
        int spacing = 60; // px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new ItemDecoration(spanCount, spacing, includeEdge));
        //layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(gridLayoutManager);

        // 찜목록 불러오기
        likeList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("like")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            likeList.add(child.getKey());
                        }
                        loadLikeTarget();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.top_category_click_reco);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "버튼작동중");
                int num = 1;
                frag_clickListener.click(num);
            }
        });

        return view;
    }

    public void loadLikeTarget() {
        arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("target");
        for (String liked : likeList) {
            databaseReference.orderByChild("name").equalTo(liked).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue() != null) {
                            Log.d(TAG, "onDataChange: " + child);
                            bottom_favorite_profile_model bottom_favorite_profile_model = child.getValue(bottom_favorite_profile_model.class);
                            arrayList.add(bottom_favorite_profile_model);
                            bottom_reco_profile_adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Fraglike", String.valueOf(error.toException())); //에러 시 출력
                }
            });
        }

        bottom_reco_profile_adapter = new bottom_reco_adapter(arrayList, getContext());
        recyclerView.setAdapter(bottom_reco_profile_adapter);
    }
    public interface clickListener {
        public void click(int num);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof bottom_home_fragment.clickListener) {
            frag_clickListener = (bottom_home_fragment.clickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        frag_clickListener = null;
    }
}
