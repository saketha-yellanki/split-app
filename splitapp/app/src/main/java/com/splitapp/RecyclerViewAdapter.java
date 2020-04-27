package com.splitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    View v;
    Context mContext;
    List<Friend> mData;
    private Object View;

    public RecyclerViewAdapter(Context mContext, List<Friend> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View=v;
        v= LayoutInflater.from(mContext).inflate(R.layout.item_frnds,parent,false);
        MyViewHolder vHolder=new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tv_name.setText(mData.get(position).getName());
        //holder.img.setImageResource(mData.get(position).getPhoto());
        

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name;
        //private ImageView img;


        public MyViewHolder(View itemView){
            super(itemView);
            tv_name=(TextView) itemView.findViewById(R.id.name_contact);
            //img=(ImageView) itemView.findViewById(R.id.img_contact);


        }
    }
}
