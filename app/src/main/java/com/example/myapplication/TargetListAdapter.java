package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.models.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TargetListAdapter extends BaseAdapter {

    HashMap<String, Boolean> hm = new HashMap<String, Boolean>();
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    private List<Target> targetList = null;
    private ArrayList<Target> arrayList;

    public TargetListAdapter(Context context, List<Target> targetList) {
        this.context = context;
        this.targetList = targetList;
        this.inflater = LayoutInflater.from(context);
        this.arrayList = new ArrayList<Target>();
        this.arrayList.addAll(targetList);

        for (Target check_target : arrayList) {
            // 나중에는 Key값으로 넣어줘
            hm.put(check_target.name, false);
            Log.d("hashMap", String.valueOf(hm.get(check_target.name)));
        }
    }

    public class ViewHolder {
        TextView tv_name;
        ImageView iv_icon;
        CheckBox cb_target;
    }

    @Override
    public int getCount() {
        return targetList.size();
    }

    @Override
    public Target getItem(int position) {
        return targetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final Target target = targetList.get(position);


        if (view == null) {
            view = inflater.inflate(R.layout.search_target_listview, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.cb_target = (CheckBox) view.findViewById(R.id.picked_target);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.tv_name.setText(target.name);

        // name으로 hashmap 검색 나중에는 Key값으로 찾아라
        if (hm.get(target.name)) {
            holder.cb_target.setChecked(true);
        } else {
            holder.cb_target.setChecked(false);
        }
        Log.d("hashMap_is_changed", String.valueOf(target.name + hm.get(target.name)));


//        holder.iv_icon.setImageIcon (target.icon); // 좀 이상해
//        Glide.with(context).load(target.icon).into(holder.iv_icon);

        holder.cb_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.picked_target);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                hm.put(target.name, checkBox.isChecked());
                Log.d("hashMap_after", String.valueOf(target.name + hm.get(target.name)));
            }
        });

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CheckBox checkBox = (CheckBox) arg0.findViewById(R.id.picked_target);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                hm.put(target.name, checkBox.isChecked());
                Log.d("hashMap_after", String.valueOf(target.name + hm.get(target.name)));


//                Intent intent = new Intent(context, BrewingActivity.class);;
//                ...
//                context.startActivity(intent);
            }
        });

        return view;
    }

    // Filter Class
    public List<Target> filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        targetList.clear();
        if (charText.length() == 0) {
            for (Target target : arrayList) {
                if (hm.get(target.name)) {
                    targetList.add(target);
                }
            }
            return targetList;
        } else {
            for (Target target : arrayList) {
                String name = target.name;
                if (name.toLowerCase().contains(charText)) {
                    targetList.add(target);
                }
            }
        }
        notifyDataSetChanged();
        return targetList;
    }
}