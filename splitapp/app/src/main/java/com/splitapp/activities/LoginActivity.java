package com.splitapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.splitapp.R;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email;
    TextInputEditText password;
    Button login_btn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    private FirebaseFirestore db;
    private View SwitchToRegister;
    private TextView forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        findViews();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = email.getText().toString();
                String pass_word = password.getText().toString();
                // now compare email and then check corresponding password if it is a match or not
                if (TextUtils.isEmpty(user_email)) {
                    email.setError("Email.is.required");
                    return;
                }
                if (TextUtils.isEmpty(pass_word)) {
                    password.setError("password.is.empty");
                    return;
                }

                fAuth.signInWithEmailAndPassword(user_email, pass_word).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "error:" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


//                if (user_email.isEmpty() || pass_word.length() < 13) {
//                    Toast.makeText(LoginActivity.this, "Please Enter Valid emailid or password", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
//                    Bundle bundle = new Bundle();
//                    intent.putExtra("mobile", user_email);
//
//                    startActivity(intent);
//                }


            }
        });
        TextView SwitchToRegister = findViewById(R.id.switchToRegister);
        String text = "New to SplitApp? Register here";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent myIntent = new Intent(LoginActivity.this, registration.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(myIntent);
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
            }
        };
        ss.setSpan(clickableSpan, 16, 30, SpannedString.SPAN_EXCLUSIVE_EXCLUSIVE);
        SwitchToRegister.setText(ss);
        SwitchToRegister.setMovementMethod(LinkMovementMethod.getInstance());

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder((v.getContext()));
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter your email to receive reset link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset link sent", Toast.LENGTH_SHORT);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error ocurred! Reset link not sent ", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }

    void findViews() {
        email = findViewById(R.id.email_edit);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.button);
        forgotPasswordLink = findViewById(R.id.forgotPassword);

    }

}