package com.splitapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String groupId,groupTitle;
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
        Intent iin=getIntent();
        Bundle b=iin.getExtras();
        if(b!=null){
            groupTitle=(String)b.get("groupTitle");
            groupId=(String)b.get("groupId");
        }
        /*groupTitle=getIntent().getStringExtra("groupTitle");
        groupId=getIntent().getStringExtra("groupId");*/
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

                    //for (QueryDocumentSnapshot document : task.getResult()) {

                        rootRef1.document(u_id).collection("Friends").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        //System.out.println("xyz "+ firebaseAuth.getUid());
                                        if (task.isSuccessful()) {
                                            friendsList.clear();
                                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                //ModelFriendList modelFriendList= (ModelFriendList) document1.get(String.valueOf(ModelFriendList.class));
                                                final String f_id=document1.getId();
                                                Log.d("frnd_id",f_id);
                                                final String f_name = document1.get("friendName").toString();
                                                final String f_email=document1.get("friendEmail").toString();
                                                final double f_amt = Double.parseDouble(document1.get("transactionAmount").toString());
                                                final String f_phone = document1.get("friendPhone").toString();
                                                ModelFriendList model = new ModelFriendList(f_name,f_email,f_phone,f_id,f_amt);
                                                if(model.getUid()!=null) {
                                                    //if (!firebaseAuth.getUid().equals(f_id)) {
                                                        friendsList.add(model);
                                                        adapterParticipantAdd=new AdapterParticipantAdd(GroupParticipantAddActivity.this,friendsList,""+groupId,""+myGroupRole);
                                                        usersRv.setAdapter(adapterParticipantAdd);
                                                    //}
                                                }
                                                else{
                                                    Toast.makeText(GroupParticipantAddActivity.this,"No friends to add",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                           /*adapterParticipantAdd=new AdapterParticipantAdd(GroupParticipantAddActivity.this,friendsList,""+groupId,""+myGroupRole);
                                            usersRv.setAdapter(adapterParticipantAdd);*/
                                        }else{
                                            Log.d("FAILED", "Error getting documents from friends: ", task.getException());
                                        }
                                    }
                                });
                    //}

                } else {
                    Log.d("FAILED", "Error getting documents: ", task.getException());
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
                        if (groupId.equals(g_id)) {
                            final String g_title = document.get("groupTitle").toString();
                            final String g_desc = document.get("groupDescription").toString();
                            final String g_creator = document.get("createdBy").toString();
                            final String g_time = document.get("timestamp").toString();
                            actionBar.setTitle("Add Participants");
                            //Log.d("grp_title",g_title);
                            rootRef.document(document.getId()).collection("Participants").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                            /*myGroupRole=document.get("role").toString();
                                            actionBar.setTitle(g_title+"("+myGroupRole+")");
                                            getAllFriends();*/
                                                for (final QueryDocumentSnapshot document1 : task.getResult()) {
                                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                                    if(user.getUid().equals(document1.getId())){
                                                        myGroupRole = document1.get("role").toString();
                                                        actionBar.setSubtitle(groupTitle + "(" + myGroupRole + ")");
                                                        getAllFriends();
                                                    }

                                                }
                                            } else {

                                            }
                                        }
                                    });
                        }
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
