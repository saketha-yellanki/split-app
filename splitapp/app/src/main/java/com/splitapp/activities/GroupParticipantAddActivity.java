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
        actionBar.setTitle("Add participants");
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
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("users");
        rootRef1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        rootRef1.document(document.getId()).collection("Friends").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                        }
                                    }
                                });
                    }

                } else {

                }
            }
        });
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
