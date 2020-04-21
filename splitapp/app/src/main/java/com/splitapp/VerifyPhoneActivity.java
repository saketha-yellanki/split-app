package com.splitapp;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
public class VerifyPhoneActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    String username;
    String user_email;
    String user_number;
    String password;
    private FirebaseFirestore db;

    private ProgressBar progressBar;
    private TextInputEditText otp_edit_text;
    MaterialButton signin_btn;
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
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        otp_edit_text = findViewById(R.id.otp_edit);
        signin_btn = findViewById(R.id.sign_in_btn);

        //Intent i = getIntent();
        Bundle b = getIntent().getExtras();
        username = b.getString("username");
        user_email = b.getString("user_email");
        user_number = b.getString("user_number");
        password = b.getString("password");
        password = encryption(password);

        sendVerificationCode(user_number);

        signin_btn.setOnClickListener(new View.OnClickListener() {
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
                            send_data(username, user_email, user_number, password);

                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }

    public void send_data(String username, String user_email, String user_mobile, String password) {

        Map<String, Object> note = new HashMap<>();
        note.put("username", username);
        note.put("user_email", user_email);
        note.put("user_mobile", user_mobile);
        note.put("password",password);
        db.collection("users").add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(VerifyPhoneActivity.this, "Successful", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VerifyPhoneActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }
    public static final String ALPHABET = "0123456789";
    public String encryption(String password){
        //ceaser cipher
        password = password.toLowerCase();
        String ciphertext = "";
        for (int i = 0; i < password.length(); i++)
        {
            int charPosition = ALPHABET.indexOf(password.charAt(i));
            int keyVal = (3 + charPosition) % 10;
            char replaceVal = ALPHABET.charAt(keyVal);
            ciphertext += replaceVal;
        }
        return ciphertext;
    }
}
