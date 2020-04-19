package com.splitapp;
import java.util.Map;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class registration extends AppCompatActivity {

    private static final String TAG = "Mainactivity";
    private static final String KEY_NAME="name";
    private static final String KEY_EMAIL = "email";
    private FirebaseFirestore db ;

    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText password;
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
        db = FirebaseFirestore.getInstance();
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
        password = findViewById(R.id.pass_edit);
        confirm_password = findViewById(R.id.conf_pass_edit);
        mobile = findViewById(R.id.mobile_edit);
        register_btn = findViewById(R.id.reg_btn);
        reg_lin_layout = findViewById(R.id.reg_layout);
        SwitchToLogin = findViewById(R.id.switchToLogin);
    }

    public void save_data(){
        String username = name.getText().toString();
        String user_email = email.getText().toString();
        String user_mobile = mobile.getText().toString();

        Map<String, Object> note = new HashMap<>();
        note.put("Username",username);
        note.put("User-email",user_email);
        note.put("User-mobile",user_mobile);
        db.collection("Users").document("details").set(note)
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("UserData","Details has been saved");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Userdata","Details has not been saved",e);
            }
        });
    }
}
