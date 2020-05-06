package com.splitapp.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.splitapp.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AcceptRequest extends AppCompatActivity {
    String friend_name;
    String friend_email;
    String friend_phone;
    MaterialButton AcceptBtn;
    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextInputEditText otp_edit_text;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otp_edit_text.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(AcceptRequest.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
         protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_request);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        otp_edit_text = findViewById(R.id.otp_edit);
        AcceptBtn = findViewById(R.id.Accept_btn);

        //Intent i = getIntent();
        Bundle b = getIntent().getExtras();
        friend_name = b.getString("friendname");
        friend_email= b.getString("friendemail");
        friend_phone= b.getString("friendnumber");
        sendVerificationCode(friend_phone);

        AcceptBtn.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        String code = otp_edit_text.getText().toString().trim();

        if (code.isEmpty() || code.length() < 6) {
        otp_edit_text.setError("Enter Code...");
        otp_edit_text.requestFocus();
        return;
        }

        verifyCode(code);


        }
        });
        }

private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
        }

private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
        send_data(friend_name, friend_email, friend_phone);
        } else {
        Toast.makeText(AcceptRequest.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show(); }
        }
        });
        }

private void sendVerificationCode(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
        }

public void send_data(String friendname, String friendemail, String friendmobile) {

    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Sending Request");

    progressDialog.show();

    final String g_timestamp = "" + System.currentTimeMillis();
    final String friendNamest = "" + friendname;
    final String friendEmailst = "" + friendemail;
    final String friendPhonest = "" + friendmobile;

    addFriend("" + g_timestamp);

    // friend Collection document
    FirebaseUser f_user = FirebaseAuth.getInstance().getCurrentUser();

    final String uid = f_user.getUid();
    HashMap<String, String> hashMap1 = new HashMap<>();
    hashMap1.put("friendName", friendNamest);
    hashMap1.put("friendEmail", friendEmailst);
    hashMap1.put("friendPhone", friendPhonest);

    hashMap1.put("transactionAmount", "0");
    db.collection("users").document(uid).collection("Friends").document().set(hashMap1)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    HashMap<String, String> hashMap1 = new HashMap<>();


                    db.collection("users").document(uid).collection("Friends").document(mAuth.getUid()).set(hashMap1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AcceptRequest.this, "Friend Added", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AcceptRequest.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AcceptRequest.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
}
    private void addFriend(String g_timestamp) {

    }
}

