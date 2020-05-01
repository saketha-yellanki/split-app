package com.splitapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.splitapp.R;
import com.splitapp.models.ModelGroupsList;

import java.util.ArrayList;

public class AdapterGroupsList extends RecyclerView.Adapter<AdapterGroupsList.HolderGroupsList> {

    private Context context;
    private ArrayList<ModelGroupsList> groupsList;

    public AdapterGroupsList(Context context, ArrayList<ModelGroupsList> groupsList) {
        this.context = context;
        this.groupsList = groupsList;
    }

    @NonNull
    @Override
    public HolderGroupsList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_groups_list, parent, false);

        return new HolderGroupsList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupsList holder, int position) {

        ModelGroupsList model = groupsList.get(position);
        String groupId = model.getGroupId();
        String groupTitle = model.getGroupTitle();
        String groupDescription = model.getGroupDescription();

        holder.groupTitleTv.setText(groupTitle);
        holder.groupDescriptionTv.setText(groupDescription);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //meghana -- for you
            }
        });


    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    class HolderGroupsList extends RecyclerView.ViewHolder {

        private MaterialTextView groupTitleTv;
        private MaterialTextView groupDescriptionTv;

        public HolderGroupsList(@NonNull View itemView) {
            super(itemView);

            groupTitleTv = itemView.findViewById(R.id.groupTitleTv);
            groupDescriptionTv = itemView.findViewById(R.id.descriptionTv);


        }
    }
}
