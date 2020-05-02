package com.splitapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.splitapp.R;
import com.splitapp.activities.CreateGroup;
import com.splitapp.adapters.AdapterFriendList;
import com.splitapp.adapters.AdapterGroupsList;
import com.splitapp.adapters.RecyclerViewAdapter;
import com.splitapp.models.ModelFriendList;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriend extends Fragment {
    View v;
   FloatingActionButton actionButton;
    private RecyclerView myrecyclerview;
    private List<ModelFriendList> friendLists;
    private AdapterFriendList adapterFriendList;

    private FirebaseAuth firebaseAuth;

    public FragmentFriend() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.friends, container, false);
        myrecyclerview = v.findViewById(R.id.friends_recyclerview);
        firebaseAuth = FirebaseAuth.getInstance();

        friendLists = new ArrayList<ModelFriendList>();
        adapterFriendList = new AdapterFriendList(getActivity(), friendLists);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myrecyclerview.setLayoutManager(llm);
        myrecyclerview.setAdapter(adapterFriendList);

        loadFriendList();
        return v;
    }
    private void loadFriendList() {

        String user_id = firebaseAuth.getUid();
        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Friends");
        rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final String f_id = document.getId();
                        final String f_name = document.get("Friend").toString();
                        final String f_amt = document.get("Amount").toString();
                        final String f_phone = document.get("phone").toString();
                        rootRef.document(document.getId()).collection("Participants").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                if (document1.getId().equals(firebaseAuth.getUid())) {
                                                    Log.d("SUCCESS", "User Exists in group");
                                                    ModelFriendList model = new ModelFriendList(f_id, f_name, f_phone);
                                                    friendLists.add(model);
                                                    adapterFriendList = new AdapterFriendList(getActivity(), friendLists);
                                                    myrecyclerview.setAdapter(adapterFriendList);
                                                    Log.d("Count of list UP", " " + friendLists.size());
                                                } else {
                                                    Log.d("FAILED", "User Does Not Exist in group");
                                                }
                                            }
                                        } else {
                                            Log.d("FAILED", "Error getting documents from participants: ", task.getException());
                                        }
                                    }
                                });
                    }
                } else {
                    Log.d("FAILED", "Error getting documents: ", task.getException());
                }
            }
        });

    }

                    @Override
                    public void onViewCreated (View view, Bundle savedInstanceState) {
                        actionButton = v.findViewById(R.id.fab_btn);
                        actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                                dlg.setTitle("Add a friend");
                                dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent myIntent = new Intent(getActivity(), CreateGroup.class);
                                        startActivity(myIntent);
                                        dialog.dismiss();
                                    }
                                }).create();
                                dlg.show();
                            }
                        });

                    }
        }
