package com.splitapp;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email;
    TextInputEditText password;
    Button login_btn;
    private FirebaseFirestore db;
    private View SwitchToRegister;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
        findViews();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = email.getText().toString();
                String pass_word = password.getText().toString();
                // now compare email and then check corresponding password if it is a match or not
                if(TextUtils.isEmpty(user_email)){
                    email.setError("Email.is.required");
                    return;
                }
                if(TextUtils.isEmpty(pass_word)){
                    email.setError("password.is.empty");
                    return;
                }
                fAuth.signInWithEmailAndPassword(user_email,pass_word).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "login succssfll", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
    }
    void findViews() {
        email = findViewById(R.id.email_edit);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.button);
    }

}
