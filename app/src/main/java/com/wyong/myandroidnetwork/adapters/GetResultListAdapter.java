package com.wyong.myandroidnetwork.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wyong.myandroidnetwork.R;
import com.wyong.myandroidnetwork.domain.GetTextItem;

import java.util.ArrayList;
import java.util.List;

public class GetResultListAdapter extends RecyclerView.Adapter<GetResultListAdapter.InnerHolder> {

    public List<GetTextItem.DataBean> mData = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_get_text,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        View itemView = holder.itemView;
        TextView titleTv = itemView.findViewById(R.id.item_title);
        TextView userName = itemView.findViewById(R.id.item_user_name);
        TextView count = itemView.findViewById(R.id.item_view_count);

        GetTextItem.DataBean dataBean = mData.get(position);
        titleTv.setText(dataBean.getTitle());
        userName.setText(dataBean.getUserName());
        count.setText(String.valueOf(dataBean.getViewCount()));

        ImageView cover = itemView.findViewById(R.id.item_img);
        Glide.with(itemView.getContext()).load("http://192.168.137.1:9102/" + mData.get(position).getCover()).into(cover);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(GetTextItem getTextItem) {
        mData.clear();
        mData.addAll(getTextItem.getData());
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
