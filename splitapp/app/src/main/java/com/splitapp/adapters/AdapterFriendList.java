// changes has to be made
package com.splitapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.splitapp.R;
import com.splitapp.models.ModelFriendList;

import java.util.List;

public class AdapterFriendList extends RecyclerView.Adapter<AdapterFriendList.HolderFriendList> {

    private Context context;
    private List<ModelFriendList> FriendList;

    public AdapterFriendList(Context context, List<ModelFriendList> friendList) {
        this.context = context;
        this.FriendList = friendList;
    }

    @NonNull
    @Override
    public HolderFriendList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_frnds, parent, false);

        return new HolderFriendList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFriendList holder, int position) {

        ModelFriendList model = FriendList.get(position);
      //  String groupId = model.getGroupId();
        //String groupTitle = model.getGroupTitle();
        //String groupDescription = model.getGroupDescription();

      //  holder.groupTitleTv.setText(groupTitle);
       // holder.groupDescriptionTv.setText(groupDescription);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //meghana -- for you
            }
        });


    }

    @Override
    public int getItemCount() {
        return FriendList.size();
    }

    class HolderFriendList extends RecyclerView.ViewHolder {

        private MaterialTextView Friend_name;
        private MaterialTextView Transaction_amount;

        public HolderFriendList(@NonNull View itemView) {
            super(itemView);

            Friend_name = itemView.findViewById(R.id.name_contact);
            Transaction_amount = itemView.findViewById(R.id.friend_transaction);


        }
    }
}