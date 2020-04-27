package com.splitapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriend extends Fragment {
    View v;
    MaterialTextView no_friends_txt;
    private RecyclerView myrecyclerview;
    private List<Friend>lstContact;
    public FragmentFriend() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.friends,container,false);
        no_friends_txt = v.findViewById(R.id.noFriends_txt);
        lstContact = new ArrayList<>();
        if (lstContact.size() > 0) {
            no_friends_txt.setVisibility(View.INVISIBLE);
            myrecyclerview = (RecyclerView) v.findViewById(R.id.friends_recyclerview);
            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), lstContact);
            myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            myrecyclerview.setAdapter(recyclerViewAdapter);

        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lstContact.add(new Friend("Saketha", "9888888888", "saketha@gmail.com"));
//        lstContact.add(new Friend("grp2"));
    }
}
