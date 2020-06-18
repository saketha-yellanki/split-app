package com.splitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.splitapp.fragments.FragmentGroups;
import com.splitapp.models.ModelFriendList;

import java.util.ArrayList;

public class GroupMainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String groupTitle,groupId,myGroupRole="";
    private ActionBar actionBar;
    private FloatingActionButton AddFriendBtnGrp;
    private ExtendedFloatingActionButton add_exp_grp;

    private RecyclerView myrecyclerview;
    private ArrayList<ModelFriendList> friendLists;
    private AdapterFriendList adapterFriendList;




    //Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Intent iin=getIntent();
        Bundle b=iin.getExtras();
        if(b!=null){
            groupTitle=(String)b.get("groupTitle");
            groupId=(String)b.get("groupId");
        }
        actionBar.setTitle(groupTitle);
        Log.d("groupId",groupId);

        myrecyclerview = findViewById(R.id.participant_recyclerview);
        friendLists = new ArrayList<ModelFriendList>();
        adapterFriendList = new AdapterFriendList(GroupMainActivity.this, friendLists);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myrecyclerview.setLayoutManager(llm);
        myrecyclerview.setAdapter(adapterFriendList);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        getAllParticipants(groupId);

        /*friendLists = new ArrayList<ModelFriendList>();
        adapterFriendList = new AdapterFriendList(GroupMainActivity.this, friendLists);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myrecyclerview.setLayoutManager(llm);
        myrecyclerview.setAdapter(adapterFriendList);*/

        AddFriendBtnGrp = findViewById(R.id.fab_btn_group_main);
        add_exp_grp = findViewById(R.id.expenses_btn_grp);
        final String user_id = firebaseAuth.getUid();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        AddFriendBtnGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(GroupMainActivity.this,GroupParticipantAddActivity.class);
                intent.putExtra("groupTitle",groupTitle);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });

        add_exp_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupMainActivity.this, GrpAddExp.class);
                intent.putExtra("groupTitle",groupTitle);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
                Bundle bundle = new Bundle();
            }
        });





        /*toolbar=findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");*/
    }

    private void getAllParticipants(final String groupId){
        friendLists = new ArrayList<>();
        final String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");
        rootRef1.document(groupId).collection("Participants").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(final QueryDocumentSnapshot document1:task.getResult()) {
                                Log.d("id->above", document1.getId());
                                if(current_user_id.equals(document1.getId())){

                                }
                                else {
                                    rootRef1.document(groupId).collection("Participants").document(current_user_id).collection("transactions").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (QueryDocumentSnapshot document11:task.getResult()){
                                                        if(document11.getId().equals(document1.getId())){
                                                            final double p_amt = Double.parseDouble((String) document11.get("transactionAmount"));
                                                            getParticipantDetails(document11.getId(),p_amt);
                                                        }
                                                    }
                                                }
                                            });
                                    /*final double p_amt = Double.parseDouble((String) document1.get("transactionAmount"));
                                    getParticipantDetails(document1.getId(),p_amt);*/
                                }
                            }


                        }
                    }
                });

    }

    private void getParticipantDetails(final String p_id, final double p_amt){

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
                        //final double p_amt = Double.parseDouble((String) docSnap.get("transactionAmount"));
                        final String p_phone = docSnap.get("user_mobile").toString();

                        ModelFriendList model = new ModelFriendList(p_name, p_email, p_phone, f_id, p_amt);
                        friendLists.add(model);

                    } else {
                        Log.d("Error", "Document doesn't exist");
                    }
                    Log.d("size", friendLists.size() + " ");
                    adapterFriendList = new AdapterFriendList(GroupMainActivity.this, friendLists);
                    myrecyclerview.setAdapter(adapterFriendList);
                } else {
                    Log.d("Failed", task.getException().toString());
                }
            }
        });


//        rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for (QueryDocumentSnapshot document2:task.getResult()){
//                        if(p_id.equals(document2.getId())){
//                            final String f_id=document2.getId();
//                            //Log.d("frnd_id",f_id);
//                            final String p_name = document2.get("user_name").toString();
//                            final String p_email=document2.get("user_email").toString();
//                            final double p_amt = Double.parseDouble("0");
//                            final String p_phone = document2.get("user_mobile").toString();
//                            ModelFriendList model = new ModelFriendList(p_name,p_email,p_phone,f_id,p_amt);
//                            if(model.getUid()!=null) {
//                                //if (!firebaseAuth.getUid().equals(f_id)) {
//                                friendLists.add(model);
//                                adapterFriendList = new AdapterFriendList(GroupMainActivity.this, friendLists);
//                                myrecyclerview.setAdapter(adapterFriendList);
//                                //}
//                            }
//                        }
//
//                    }
//                }
//            }
//        });

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

    /*private void setSupportActionBar(Toolbar toolbar) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.findItem(R.id.menu_sign_out).setVisible(false);
        if(myGroupRole.equals("creator")){
            menu.findItem(R.id.action_add_participant).setVisible(true);
        }
        else{
            menu.findItem(R.id.action_add_participant).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_add_participant){
            Intent intent=new Intent(this,GroupParticipantAddActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(GroupMainActivity.this, FragmentGroups.class);
        startActivity(intent);
        finish();
    }*/
}
