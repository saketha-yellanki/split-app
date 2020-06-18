package com.splitapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialAutoCompleteTextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrpAddExp extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private String groupTitle, groupId;
    private ActionBar actionBar;
    TextInputEditText amount_grp;
    MaterialButton grpaddexp_btn;
    private ArrayList<ModelFriendList> friendLists;
    FirebaseAuth mAuth;
    String current_user_id;

    MaterialAutoCompleteTextView paid_member;
    String paid_by;
    //ArrayList<String> names = new ArrayList<>();
    List<List<String>> names = new ArrayList<>();

    final int[] pos = new int[1];
    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grp_add_exp);

        grpaddexp_btn=findViewById(R.id.grpaddexp_btn);
        amount_grp = findViewById(R.id.amount_grp);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        Intent iin=getIntent();
        Bundle b=iin.getExtras();
        if(b!=null){
            groupTitle=(String)b.get("groupTitle");
            groupId=(String)b.get("groupId");
        }

        paid_member = findViewById(R.id.paid_by_auto);
        loadNames();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        /*groupTitle = getIntent().getStringExtra("groupTitle");
        groupId = getIntent().getStringExtra("groupId");*/

        paid_member.setAdapter(new ArrayAdapter<>(GrpAddExp.this, android.R.layout.simple_list_item_1, names.get(0)));
        paid_member.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                paid_member.showDropDown();
                paid_member.requestFocus();
                return false;
            }
        });
        paid_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                int pos2 = -1;
                for (int in = 0; in < names.get(0).size(); i++) {
                    if (names.get(0).get(i).equals(selected)) {
                        pos2 = i;
                        break;
                    }
                }
                pos[0] = pos2;
                paid_by=names.get(1).get(pos2);
                Log.d("paid_by",paid_by);
            }
        });

        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");
        rootRef1.document(groupId).collection("Participants").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                //Log.d("id->above", document1.getId());
                                i=i+1;
                            }
                        }
                        else{

                        }
                    }
                });


        firebaseAuth = FirebaseAuth.getInstance();
        grpaddexp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amount = Double.parseDouble(amount_grp.getText().toString());
                int total_mem=i;
                Log.d("total_mem", String.valueOf(total_mem));
                updateamount(amount,groupId,total_mem);
            }
        });

        final String user_id = firebaseAuth.getUid();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

    }


    private void updateamount(final double amount, final String groupId,int total_mem) {
        final double share = amount / (total_mem);
        Log.d("share", String.valueOf(share));
        friendLists = new ArrayList<>();
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");

        //final String paid_user=paid_member.getText().toString();
        rootRef1.document(groupId).collection("Participants").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document1 : task.getResult()) {
                                Log.d("id->above", document1.getId());
                                if(paid_by.equals(document1.getId())){
                                    rootRef1.document(groupId).collection("Participants").document(document1.getId()).collection("transactions").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for(final QueryDocumentSnapshot document11:task.getResult()){
                                                            double p_amt = Double.parseDouble((String) document11.get("transactionAmount"));
                                                            p_amt=p_amt+share;
                                                            //getParticipantamtD(document11.getId(),p_amt);
                                                            Map<String,Object> temp = new HashMap<>();
                                                            temp.put("transactionAmount", Double.toString(p_amt));
                                                            rootRef1.document(groupId).collection("Participants").document(document1.getId()).collection("transactions").document(document11.getId()).update(temp);
                                                            //updating amt in frnds
                                                            /*final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Users");
                                                            rootRef.document(paid_by).collection("Friends").get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if(task.isSuccessful()){
                                                                                for (QueryDocumentSnapshot doc:task.getResult()){
                                                                                    if(doc.getId().equals(document11.getId())){
                                                                                        double p_amt1 = Double.parseDouble((String) doc.get("transactionAmount"));
                                                                                        p_amt1=p_amt1-share;
                                                                                        Map<String,Object> temp1 = new HashMap<>();
                                                                                        temp1.put("transactionAmount", Double.toString(p_amt1));
                                                                                        rootRef.document(paid_by).collection("Friends").document(doc.getId()).update(temp1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });*/
                                                            final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("users");
                                                            final DocumentReference docref = rootRef.document(paid_by);
                                                            docref.collection("Friends").document(document11.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot docSnap = task.getResult();
                                                                        if (docSnap.exists()) {
                                                                            final String f_id = docSnap.getId();
                                                                            double p_amt1 = Double.parseDouble((String) docSnap.get("transactionAmount"));
                                                                            p_amt1=p_amt1-share;
                                                                            Map<String,Object> temp1 = new HashMap<>();
                                                                            temp1.put("transactionAmount", Double.toString(p_amt1));
                                                                            rootRef.document(paid_by).collection("Friends").document(document11.getId()).update(temp1);
                                                                            rootRef.document(document11.getId()).collection("Friends").document(paid_by).update(temp1);

                                                                        } else {
                                                                            Log.d("Error", "Document doesn't exist");
                                                                        }

                                                                    } else {
                                                                        Log.d("Failed", task.getException().toString());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }

                                                }
                                            });
                                }
                                else{
                                    rootRef1.document(groupId).collection("Participants").document(document1.getId()).collection("transactions").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for(final QueryDocumentSnapshot document11:task.getResult()){
                                                            if(document11.getId().equals(paid_by)){
                                                                double p_amt = Double.parseDouble((String) document11.get("transactionAmount"));
                                                                p_amt=p_amt-share;
                                                                //getParticipantamtD(document11.getId(),p_amt);
                                                                Map<String, Object> temp = new HashMap<>();
                                                                temp.put("transactionAmount", Double.toString(p_amt));
                                                                rootRef1.document(groupId).collection("Participants").document(document1.getId()).collection("transactions").document(document11.getId()).update(temp);
                                                            }
                                                        }
                                                    }

                                                }
                                            });
                                }
                            }


                        }
                    }
                });
        Toast.makeText(GrpAddExp.this, "Successfully updated", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(GrpAddExp.this, GroupMainActivity.class);
        intent.putExtra("groupTitle",groupTitle);
        intent.putExtra("groupId",groupId);
        startActivity(intent);
        finish();

    }


    private void getParticipantamtD(final String p_id,final double p_amt) {
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
                        //double p_amt = Double.parseDouble((String) docSnap.get("transactionAmount"));
                        /*if(p_amt>=0) {
                            p_amt =p_amt+share;
                        }
                        else{
                            p_amt-=share;
                        }*/
                        Map<String, Object> temp = new HashMap<>();
                        temp.put("transactionAmount", (Object) Double.toString(p_amt));
                        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");
                        rootRef1.document(groupId).collection("Participants").document(p_id).update(temp)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(GrpAddExp.this, "Successfully updated", Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(GrpAddExp.this, GroupMainActivity.class);
                                            intent.putExtra("groupTitle",groupTitle);
                                            intent.putExtra("groupId",groupId);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(GrpAddExp.this, "Failed" + task.getException(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                    } else {
                        Log.d("Error", "Document doesn't exist");
                    }

                } else {
                    Log.d("Failed", task.getException().toString());
                }
            }
        });
    }



    /*private void updateLocalWithDb() {
        final ArrayList<ModelFriendList> friends = FriendsList.getInstance().friends;
        final CollectionReference rootref = FirebaseFirestore.getInstance().collection("users");
        rootref.document(current_user_id).collection("Friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (int i = 0; i < friends.size(); i++) {
                            HashMap<String, String> map = new HashMap();
                            map.put("friendName", friends.get(i).getName());
                            map.put("friendEmail", friends.get(i).getEmail());
                            map.put("friendPhone", friends.get(i).getPhone());
                            map.put("transactionAmount", friends.get(i).getAmount());
                            rootref.document(current_user_id).collection("Friends").document(friends.get(i).getUid())
                                    .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(GrpAddExp.this, "Successfully updated", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(GrpAddExp.this, GroupMainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(GrpAddExp.this, "Failed" + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }*/


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void loadNames() {
        friendLists= new ArrayList<>();
        final int[] i = {0};
        final List<String> id1 = new ArrayList<>();
        final List<String> name1 = new ArrayList<>();
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");
        //error near grp id(harshitha)
        rootRef1.document(groupId).collection("Participants").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document1 : task.getResult()) {
                                if(document1.getId().equals(current_user_id)) {
                                }
                                else{

                                final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("users");

                                final DocumentReference docref = rootRef.document(document1.getId());
                                docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot docSnap = task.getResult();
                                            if (docSnap.exists()) {
                                                final String name = (String) docSnap.get("user_name");
                                                String email=(String)docSnap.get("user_email");
                                                String phone=(String)docSnap.get("user_mobile");
                                                String uid=docSnap.getId();

                                                ModelFriendList model = new ModelFriendList(name,email,phone,uid,Double.parseDouble("0"));
                                                friendLists.add(model);
                                                name1.add(i[0], name);
                                                id1.add(i[0],uid);
                                                i[0]++;

                                            } else {
                                                Log.d("Error", "Document doesn't exist");
                                            }

                                        } else {
                                            Log.d("Failed", task.getException().toString());
                                        }
                                    }
                                });
                            }

                            }
                        }
                        else{

                        }
                    }
                });
        //names.add(i, sharedBy.get(i).getName());
        name1.add("Me");
        id1.add(current_user_id);

        names.add(0,name1);
        names.add(1,id1);
    }
}
