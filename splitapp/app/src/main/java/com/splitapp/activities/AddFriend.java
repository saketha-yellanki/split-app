package com.splitapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.splitapp.R;
import com.splitapp.fragments.FragmentFriend;
import com.splitapp.models.ModelFriendList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class AddFriend extends AppCompatActivity {

    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText email;
    private TextInputEditText name;
    private TextInputEditText mobile;
    private FloatingActionButton AddFriendBtn;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean userExists=false;
    private boolean sameUser=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);


        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Add Friend");

        email = findViewById(R.id.email_edit);
        name = findViewById(R.id.name_edit);
        mobile = findViewById(R.id.phone_edit);
        AddFriendBtn = findViewById(R.id.add_frnd_btn);

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        AddFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddingFrnd();
            /*    final String friend_name = name.getText().toString();
                final String friend_email = email.getText().toString();
                final String number = "+91 " + mobile.getText().toString();
                Intent intent = new Intent(AddFriend.this, AcceptRequest.class);
                Bundle bundle = new Bundle();
                bundle.putString("friendname", friend_name);
                bundle.putString("friendemail", friend_email);
                bundle.putString("friendnumber", number);
                intent.putExtras(bundle);

                startActivity(intent);*/
            }
        });

    }

    private void startAddingFrnd() {
        //sending code to friend and once friend accept it add it into database with amount 0;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Friend");

        final String friendEmail = email.getText().toString().trim();
        String friendName = name.getText().toString().trim();
        String friendPhone = mobile.getText().toString().trim();


        if (TextUtils.isEmpty(friendEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (friendPhone.isEmpty() || friendPhone.length() < 10) {
            Toast.makeText(AddFriend.this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.show();

        final String g_timestamp = "" + System.currentTimeMillis();
        final String friendNamest = "" + friendName;
        final String friendEmailst = "" + friendEmail;
        final String friendPhonest = "" + friendPhone;

        addFriend("" + g_timestamp);


        // friend Collection document

        final String current_id = FirebaseAuth.getInstance().getUid();
        DocumentReference documentref = FirebaseFirestore.getInstance().collection("users").document(current_id);
        documentref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String current_name = (String) document.get("user_name");
                        String current_email = (String) document.get("user_email");
                        String current_mobile = (String) document.get("user_mobile");
                        checking(current_name,current_email,current_mobile,current_id,friendEmailst);
                    } else {
                        Log.d("failed", "No such document");
                    }
                } else {
                    Log.d("failed", "get failed with ", task.getException());
                }
            }
        });

        final String uid = user.getUid();
        final HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("friendName", friendNamest);
        hashMap1.put("friendEmail", friendEmailst);
        hashMap1.put("friendPhone", friendPhonest);
        hashMap1.put("transactionAmount", "0");

        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("users");
        rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        final String fid = document.getId();
                        final String email = document.get("user_email").toString();
                        System.out.println(friendEmailst);
                        System.out.println(email);
                        if (friendEmailst.equals(email)) {
                            userExists=true;

                            System.out.println(friendEmailst);
                            System.out.println(email);

                            if (friendEmailst.equals(user.getEmail())) {
                                progressDialog.dismiss();
                                sameUser=true;
                                break;
                            }
                            db.collection("users").document(uid).collection("Friends").document(fid).set(hashMap1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            addFriendLocal(friendNamest, friendEmailst, friendPhonest, fid, 0);
                                            Toast.makeText(AddFriend.this, "Friend Added", Toast.LENGTH_SHORT).show();
                                            reloadFriendFragment();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddFriend.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break;

                        }
                    }
                    if(sameUser) {
                        progressDialog.dismiss();
                        Toast.makeText(AddFriend.this, "Add a friend", Toast.LENGTH_SHORT).show();
                        if (!userExists) {
                            Toast.makeText(AddFriend.this, "No such user", Toast.LENGTH_SHORT).show();

                        }
                    }

                } else {
                    Log.d("FAILED", "Error getting documents: ", task.getException());
                }
            }
        });


    }


            private void addFriendLocal(String friendNamest, String friendEmailst, String friendPhonest, String fid, int amount) {
                ModelFriendList modelFriendList = new ModelFriendList(friendNamest, friendEmailst, friendPhonest, fid, 0);
                FriendsList.getInstance().friends.add(modelFriendList);

                for (int i = 0; i < FriendsList.getInstance().friends.size(); i++) {
                    Log.d("All Friends", FriendsList.getInstance().friends.get(i).getName());
                }

            }

    private void addFriend(String g_timestamp) {

    }

    private void checkUser() {
        if (user != null) {
            actionBar.setSubtitle(user.getEmail());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void reloadFriendFragment() {
        Fragment friendFragment = new FragmentFriend();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(friendFragment);
        ft.attach(friendFragment);
        ft.commit();

        startActivity(new Intent(AddFriend.this, ProfileActivity.class));
        finish();
    }
    public void send_data(String username, String user_email, String user_mobile, final String uid,String current_id) {

        if (uid != null) {
            Map<String, Object> note = new HashMap<>();
            note.put("user_email", user_email);
            note.put("user_mobile", user_mobile);
            note.put("user_name", username);

            Log.d("uid ", uid);
            db.collection("users").document(uid).collection("Friends").document(current_id).set(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddFriend.this, uid, Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddFriend.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(AddFriend.this, "user not logged in", Toast.LENGTH_LONG).show();
        }

    }

    public void checking(String email, final String name, final String mobile, final String Id, final String femail){

        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("users");
        rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        final String fid = document.getId();
                        final String email = document.get("user_email").toString();
                        //checking for users id
                        if (femail.equals(email)) {
                            if (femail.equals(user.getEmail())) {
                                progressDialog.dismiss();
                                sameUser=true;
                                break;
                            }
                            Object hashMap1 = null;
                            db.collection("users").document(fid).collection("Friends").document(Id).set(hashMap1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                          //storing the values into the friend database
                                            send_data(email,name,mobile,fid,Id);
                                        Toast.makeText(AddFriend.this, "Friend Added", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddFriend.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            //if found a id the break the for loop
                            break;
                        }
                    }
                    if(sameUser) {
                        progressDialog.dismiss();
                        Toast.makeText(AddFriend.this, "Add a friend", Toast.LENGTH_SHORT).show();
                        if (!userExists) {
                            Toast.makeText(AddFriend.this, "No such user", Toast.LENGTH_SHORT).show();

                        }
                    }

                } else {
                    Log.d("FAILED", "Error getting documents: ", task.getException());
                }
            }
        });

    }
}
