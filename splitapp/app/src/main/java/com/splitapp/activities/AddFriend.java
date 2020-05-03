package com.splitapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.splitapp.R;

import java.util.HashMap;

public class AddFriend extends AppCompatActivity {

    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText email;
    private TextInputEditText name;
    private TextInputEditText mobile;
    private FloatingActionButton AddFriendBtn;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;

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
              //  startAddingFrnd();
                final String friend_name = name.getText().toString();
                final String friend_email = email.getText().toString();
                final String number = "+91 " + mobile.getText().toString();
                Intent intent = new Intent(AddFriend.this, AcceptRequest.class);
                Bundle bundle = new Bundle();
                bundle.putString("friendname", friend_name);
                bundle.putString("friendemail", friend_email);
                bundle.putString("friendnumber", number);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

    }

    private void startAddingFrnd() {
       //sending code to friend and once friend accept it add it into database with amount 0;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Request");

        String friendEmail = email.getText().toString().trim();
        String friendName = name.getText().toString().trim();
        String friendPhone = mobile.getText().toString().trim();


        if (TextUtils.isEmpty(friendEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (friendPhone.isEmpty() || friendPhone.length() < 13) {
            Toast.makeText( AddFriend.this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
        }




        progressDialog.show();

        final String g_timestamp = "" + System.currentTimeMillis();
        final String friendNamest=""+friendName;
        final String friendEmailst=""+friendEmail;
        final String friendPhonest=""+friendPhone;

        addFriend("" + g_timestamp);

        // friend Collection document
        FirebaseUser f_user = FirebaseAuth.getInstance().getCurrentUser();

        final String uid = f_user.getUid();
        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("friendName",friendNamest);
        hashMap1.put("friendEmail",friendEmailst);
        hashMap1.put("friendPhone",friendPhonest);

        hashMap1.put("transactionAmount","0");
        db.collection("users").document(uid).collection("Friends").document().set(hashMap1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        HashMap<String, String> hashMap1 = new HashMap<>();


                        db.collection("users").document(uid).collection("Friends").document(firebaseAuth.getUid()).set(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
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

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddFriend.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addFriend(String g_timestamp) {

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
