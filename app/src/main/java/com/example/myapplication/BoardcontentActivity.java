package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BoardcontentActivity extends AppCompatActivity {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Boardcontent_recyclerview> arrayList;
    private Boardcontent_recyclerview boardcontentRecyclerview;
    private Boardcontent_adapter boardcontent_adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_content);

        //recyclerView
        recyclerView = (RecyclerView)findViewById(R.id.board_recycle);
        boardcontent_adapter = new Boardcontent_adapter(arrayList);
        arrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(boardcontent_adapter);
        //버튼
        Button btn_board = findViewById(R.id.btn_board);
        btn_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boardcontent_recyclerview board_recyclerview = new Boardcontent_recyclerview("댓글", "댓글1");
                arrayList.add(board_recyclerview);
                boardcontent_adapter.notifyDataSetChanged();
            }
        });
    }
}