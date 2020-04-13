package com.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class registration extends AppCompatActivity {

    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText confirm_password;
    TextInputEditText mobile;
    Button register_btn;
    LinearLayout reg_lin_layout;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViews();
        mAuth = FirebaseAuth.getInstance();
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "+91 " + mobile.getText().toString();
                if (number.isEmpty() || number.length() < 13) {
                    Toast.makeText(registration.this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(registration.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", number);
                startActivity(intent);
            }
        });

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
        password = findViewById(R.id.pass_edit);
        confirm_password = findViewById(R.id.conf_pass_edit);
        mobile = findViewById(R.id.mobile_edit);
        register_btn = findViewById(R.id.reg_btn);
        reg_lin_layout = findViewById(R.id.reg_layout);
    }
}
