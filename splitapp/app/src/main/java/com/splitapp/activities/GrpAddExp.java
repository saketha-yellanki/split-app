package com.splitapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.splitapp.R;
import com.splitapp.adapters.AdapterFriendList;
import com.splitapp.models.ModelFriendList;

import java.util.ArrayList;

public class GrpAddExp<total_mem> extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String groupTitle, groupId, myGroupRole = "";
    private ActionBar actionBar;
    private FloatingActionButton AddFriendBtnGrp;
    private ExtendedFloatingActionButton add_exp_grp;

    private RecyclerView myrecyclerview;
    private ArrayList<ModelFriendList> friendLists;
    private AdapterFriendList adapterFriendList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grp_add_exp);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        groupTitle = getIntent().getStringExtra("groupTitle");
        groupId = getIntent().getStringExtra("groupId");
        actionBar.setTitle(groupTitle);
        Log.d("groupId", groupId);
        myrecyclerview = findViewById(R.id.participant_recyclerview);
        friendLists = new ArrayList<ModelFriendList>();
        adapterFriendList = new AdapterFriendList(GrpAddExp.this, friendLists);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myrecyclerview.setLayoutManager(llm);
        myrecyclerview.setAdapter(adapterFriendList);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        getNo_Participants(groupId);
        updateamount(groupId);

        AddFriendBtnGrp = findViewById(R.id.fab_btn_group_main);
        add_exp_grp = findViewById(R.id.expenses_btn_grp);
        final String user_id = firebaseAuth.getUid();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        AddFriendBtnGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GrpAddExp.this, GroupParticipantAddActivity.class);
                intent.putExtra("groupTitle", groupTitle);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });

        add_exp_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GrpAddExp.this, AddExpenses.class);
                Bundle bundle = new Bundle();
            }
        });
    }
     int total_mem=0;
    private void getNo_Participants(final String groupId) {
        friendLists = new ArrayList<>();
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");
        rootRef1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final String g_id = document.getId();
                        if (groupId.equals(g_id)) {
                            rootRef1.document(g_id).collection("Participants").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                    Log.d("id->above", document1.getId());
                                                    total_mem=total_mem+1;
                                                }


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
    private void updateamount(final String groupId) {
        friendLists = new ArrayList<>();
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");
        rootRef1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final String g_id = document.getId();
                        if (groupId.equals(g_id)) {
                            rootRef1.document(g_id).collection("Participants").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                    Log.d("id->above", document1.getId());
                                                    getParticipantamtD(document1.getId());
                                                }


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


    private void getParticipantamtD(final String p_id) {

        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("users");
        final DocumentReference docref = rootRef.document(p_id);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap.exists()) {
                        final String f_id = docSnap.getId();
                        Log.d("id", f_id);
                        final String p_name = docSnap.getString("user_name").toString();
                        final String p_email = docSnap.get("user_email").toString();
                        double p_amt = Double.parseDouble("0");
                        p_amt=p_amt/total_mem;
                        final String p_phone = docSnap.get("user_mobile").toString();

                        ModelFriendList model = new ModelFriendList(p_name, p_email, p_phone, f_id, p_amt);
                        friendLists.add(model);

                    } else {
                        Log.d("Error", "Document doesn't exist");
                    }
                    Log.d("size", friendLists.size() + " ");
                    adapterFriendList = new AdapterFriendList(GrpAddExp.this, friendLists);
                    myrecyclerview.setAdapter(adapterFriendList);
                } else {
                    Log.d("Failed", task.getException().toString());
                }
            }
        });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            actionBar.setSubtitle(user.getEmail());
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
