package com.splitapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.splitapp.R;
import com.splitapp.adapters.AdapterGroupsList;
import com.splitapp.adapters.AdapterParticipantAdd;
import com.splitapp.models.ModelFriendList;
import com.splitapp.models.ModelGroupsList;

import java.util.ArrayList;

public class GroupParticipantAddActivity extends AppCompatActivity {

    private RecyclerView usersRv;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private String groupId;
    private FirebaseFirestore db;
    private  String myGroupRole;
    private ArrayList<ModelFriendList> friendsList;
    private AdapterParticipantAdd adapterParticipantAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant_add);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Add Participants");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();

        usersRv=findViewById(R.id.usersRv);
        groupId=getIntent().getStringExtra("groupId");
        loadGroupInfo();
        getAllFriends();
    }

    private void getAllFriends() {
        friendsList = new ArrayList<>();
        String user_id = firebaseAuth.getUid();
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("users");
        rootRef1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final String u_id =firebaseAuth.getUid();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        rootRef1.document(u_id).collection("Friends").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        System.out.println("xyz "+ firebaseAuth.getUid());
                                        if (task.isSuccessful()) {
                                            friendsList.clear();
                                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                ModelFriendList modelFriendList= (ModelFriendList) document1.get(String.valueOf(ModelFriendList.class));
                                                if (!firebaseAuth.getUid().equals(modelFriendList.getUid())) {
                                                    friendsList.add(modelFriendList);

                                                }
                                            }

                                            adapterParticipantAdd=new AdapterParticipantAdd(GroupParticipantAddActivity.this,friendsList,""+groupId,""+myGroupRole);
                                            usersRv.setAdapter(adapterParticipantAdd);
                                        }else{
                                            Log.d("FAILED", "Error getting documents from friends: ", task.getException());
                                        }
                                    }
                                });
                    }

                } else {
                    Log.d("FAILED", "Error getting documents: ", task.getException());
                }
            }
        });


        /*String user_id = firebaseAuth.getUid();
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
                                .addOnCompleteListener(    new OnCompleteListener<QuerySnapshot>() {
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
        });*/
    }
    private void loadGroupInfo() {
        String user_id = firebaseAuth.getUid();
        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Groups");
        rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {

                        final String g_id = document.getId();
                        final String g_title = document.get("groupTitle").toString();
                        final String g_desc = document.get("groupDescription").toString();
                        final String g_creator = document.get("createdBy").toString();
                        final String g_time = document.get("timestamp").toString();
                        actionBar.setTitle("Add Participants");

                        rootRef.document(document.getId()).collection("Participants").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            myGroupRole=document.get("role").toString();
                                            actionBar.setTitle(g_title+"("+myGroupRole+")");
                                            getAllFriends();
                                        } else {

                                        }
                                    }
                                });
                    }

                } else {

                }
            }
        });




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
