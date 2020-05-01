package com.splitapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.splitapp.R;
import com.splitapp.models.Friend;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    View v;
    Context mContext;
    List<Friend> mData;
    Dialog myDialog;
    private Object View;

    public RecyclerViewAdapter(Context mContext, List<Friend> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_frnds, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.dialog_friend);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        vHolder.item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Toast.makeText(mContext, "Test Click" + String.valueOf(vHolder.getAdapterPosition()), Toast.LENGTH_LONG).show();
                myDialog.show();
            }
        });
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_friend;
        private TextView tv_name;
        //private ImageView img;


        public MyViewHolder(View itemView) {
            super(itemView);
            item_friend = itemView.findViewById(R.id.friend_item_id);
            tv_name = (TextView) itemView.findViewById(R.id.name_contact);
            //img=(ImageView) itemView.findViewById(R.id.img_contact);


        }
    }
}
