package com.splitapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email;
    TextInputEditText password;
    Button login_btn;
    private View SwitchToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = email.getText().toString();
                String pass_word = password.getText().toString();
                pass_word = encryption(pass_word);
                // now compare email and then check corresponding password if it is a match or not
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
    //do not change the value of this string
    public static final String ALPHABET = "acegikmoqs";
    public String encryption(String password){
        //substitution cipher
        password = password.toLowerCase();
        String ciphertext = "";
        for (int i = 0; i < password.length(); i++)
        {
            int charPosition = ALPHABET.indexOf(password.charAt(i));
            char replaceVal = ALPHABET.charAt(charPosition);
            ciphertext += replaceVal;
        }
        return ciphertext;
    }

}
