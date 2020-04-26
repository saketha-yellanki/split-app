package com.splitapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriend extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Friend>lstContact;

    public FragmentFriend() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.friends,container,false);
        myrecyclerview=(RecyclerView) v.findViewById(R.id.friends_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(getContext(),lstContact);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lstContact =new ArrayList<>();
        lstContact.add(new Friend("frnd1","123425",R.drawable.ic_launcher_foreground));
        lstContact.add(new Friend("frnd2","126578",R.drawable.ic_launcher_foreground));

    }
}
