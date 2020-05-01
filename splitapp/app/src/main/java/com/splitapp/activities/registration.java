package com.splitapp.activities;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.splitapp.R;

public class registration extends AppCompatActivity {
    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText user_password;
    TextInputEditText confirm_password;
    TextInputEditText mobile;
    Button register_btn;
    LinearLayout reg_lin_layout;
    TextView SwitchToLogin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViews();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = name.getText().toString();
                final String user_email = email.getText().toString();
                //String user_mobile = mobile.getText().toString();
                final String number = "+91 " + mobile.getText().toString();
                final String password = user_password.getText().toString();
                final String conf_pass = confirm_password.getText().toString();

                if (TextUtils.isEmpty(user_email)) {
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    user_password.setError("Password is required");
                    return;
                }

                if (TextUtils.isEmpty(conf_pass)) {
                    user_password.setError("Password is required");
                    return;
                }

                if (!password.equals(conf_pass)) {
                    Toast.makeText(registration.this, "Pasword Do Not Match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (number.isEmpty() || number.length() < 13) {
                    Toast.makeText(registration.this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(registration.this, VerifyPhoneActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("user_email", user_email);
                    //bundle.putString("user_mobile",user_mobile);
                    bundle.putString("user_number", number);
                    bundle.putString("password", password);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }


            }
        });
        String text = "Already have an Account? Login.";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent myIntent = new Intent(registration.this, LoginActivity.class);
                startActivity(myIntent);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLACK);
            }
        };
        ss.setSpan(clickableSpan, 0, 30, SpannedString.SPAN_EXCLUSIVE_EXCLUSIVE);
        SwitchToLogin.setText(ss);
        SwitchToLogin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    void findViews() {
        name = findViewById(R.id.name_edit);
        email = findViewById(R.id.email_edit);
        user_password = findViewById(R.id.pass_edit);
        confirm_password = findViewById(R.id.conf_pass_edit);
        mobile = findViewById(R.id.mobile_edit);
        register_btn = findViewById(R.id.reg_btn);
        reg_lin_layout = findViewById(R.id.reg_layout);
        SwitchToLogin = findViewById(R.id.switchToLogin);
    }

}
