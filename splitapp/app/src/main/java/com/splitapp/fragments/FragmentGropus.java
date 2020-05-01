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
import com.splitapp.adapters.AdapterGroupsList;
import com.splitapp.models.ModelGroupsList;

import java.util.ArrayList;

public class FragmentGropus extends Fragment {
    View v;
    FloatingActionButton actionButton;
    private RecyclerView groupsRv;
    private ArrayList<ModelGroupsList> groupsLists;
    private AdapterGroupsList adapterGroupsList;

    private FirebaseAuth firebaseAuth;

    public FragmentGropus() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.groups, container, false);
        groupsRv = v.findViewById(R.id.groupRv);

        firebaseAuth = FirebaseAuth.getInstance();

        groupsLists = new ArrayList<ModelGroupsList>();
        adapterGroupsList = new AdapterGroupsList(getActivity(), groupsLists);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        groupsRv.setLayoutManager(llm);
        groupsRv.setAdapter(adapterGroupsList);

        loadGroupsList();
        return v;
    }

    private void loadGroupsList() {

        String user_id = firebaseAuth.getUid();
        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Groups");
        rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("SUCCESS", document.getId() + " => " + document.getData());
                        final String g_id = document.getId();
                        final String g_title = document.get("groupTitle").toString();
                        final String g_desc = document.get("groupDescription").toString();
                        final String g_creator = document.get("createdBy").toString();
                        final String g_time = document.get("timestamp").toString();
                        rootRef.document(document.getId()).collection("Participants").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                if (document1.getId().equals(firebaseAuth.getUid())) {
                                                    Log.d("SUCCESS", "User Exists in group");
                                                    ModelGroupsList model = new ModelGroupsList(g_id, g_title, g_desc, g_time, g_creator);
                                                    groupsLists.add(model);
                                                    adapterGroupsList = new AdapterGroupsList(getActivity(), groupsLists);
                                                    groupsRv.setAdapter(adapterGroupsList);
                                                    Log.d("Count of list UP", " " + groupsLists.size());
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
                    //adapterGroupsList = new AdapterGroupsList(getActivity(), groupsLists);
                    Log.d("Count of list", " " + groupsLists.size());
//                    adapterGroupsList = new AdapterGroupsList(getActivity(), groupsLists);
//                    groupsRv.setAdapter(adapterGroupsList);
                } else {
                    Log.d("FAILED", "Error getting documents: ", task.getException());
                }
            }
        });

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        actionButton = v.findViewById(R.id.fab_btn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("Create new group?");
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

