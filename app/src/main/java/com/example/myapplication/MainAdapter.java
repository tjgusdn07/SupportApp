package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "category";
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Subject> data;
    private adapter2activity a2a;

    private Intent intent;

    HashMap<String, Boolean> hm = new HashMap<String, Boolean>();

    public MainAdapter(List<Subject> data, adapter2activity a2a) {
        this.data = data;
        this.a2a = a2a;

        for (Subject check_subject : data) {
            hm.put(check_subject.mCategory, false);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.expandable_list_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                LayoutInflater child_inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = child_inflater.inflate(R.layout.target_list_item2, parent, false);
                ListChildViewHolder child = new ListChildViewHolder(view);
                return child;
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Subject subject = data.get(position);
        switch (subject.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalSubject = subject;
                itemController.header_title.setText(subject.lCategory);
                if (subject.invisibleChildren == null) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
                } else {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                }
                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subject.invisibleChildren == null) {
                            subject.invisibleChildren = new ArrayList<Subject>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalSubject);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                subject.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                        } else {
                            int pos = data.indexOf(itemController.refferalSubject);
                            int index = pos + 1;
                            for (Subject i : subject.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
                            subject.invisibleChildren = null;
                        }
                    }
                });
                itemController.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subject.invisibleChildren == null) {
                            subject.invisibleChildren = new ArrayList<Subject>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalSubject);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                subject.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                        } else {
                            int pos = data.indexOf(itemController.refferalSubject);
                            int index = pos + 1;
                            for (Subject i : subject.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
                            subject.invisibleChildren = null;
                        }
                    }
                });
                break;
            case CHILD:
                final ListChildViewHolder childController = (ListChildViewHolder) holder;
                childController.child_title.setText(data.get(position).mCategory);

                if (hm.get(subject.mCategory) == null) {
                    hm.put(subject.mCategory, false);
                }
                /*if (hm.get(subject.mCategory)) {
                    childController.child_cb.setChecked(true);
                } else {
                    childController.child_cb.setChecked(false);
                }*/

                //????????? ????????? ?????????
                childController.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent = new Intent(view.getContext(), categoryActivity.class);
                        intent.putExtra("mCategory", data.get(position).mCategory);
                        view.getContext().startActivity(intent);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public Subject refferalSubject;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    public static class ListChildViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public ImageView child_img;

        public ListChildViewHolder(View childView) {
            super(childView);
            child_title = childView.findViewById(R.id.tv_name);
            child_img = childView.findViewById(R.id.iv_icon);
        }
    }
}