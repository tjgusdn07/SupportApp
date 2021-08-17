package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Live.activities.LiveBroadCastActivity;


public class bottom_setting_fragment extends Fragment {
    private View view;
    private Button live_button;
    private Button account_button;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_setting_fragment, container, false);

        live_button = (Button) view.findViewById(R.id.live_button);
        live_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), LiveBroadCastActivity.class);
                startActivity(intent);
            }
        });

        account_button = (Button) view.findViewById(R.id.account_button);
        account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), accountActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
}
