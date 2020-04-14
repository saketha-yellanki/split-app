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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextView SwitchToRegister = findViewById(R.id.switchToRegister);
        String text = "New to SplitApp? Register here";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent myIntent = new Intent(LoginActivity.this, registration.class);
                startActivity(myIntent);

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


}
