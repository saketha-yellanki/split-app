package com.splitapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.splitapp.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Expenses extends AppCompatActivity {
    TextInputEditText description;
    TextInputEditText amount;
    Button save_expenses;
    LinearLayout exe_lin_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses);
        description = findViewById(R.id.des_edit);
        amount = findViewById(R.id.amount_edit);
        save_expenses = findViewById(R.id.save_btn);
        exe_lin_layout = findViewById(R.id.exe_layout);

        save_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_description = description.getText().toString();
                final String total_amount = amount.getText().toString();
                if (TextUtils.isEmpty(user_description)) {
                    description.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(total_amount)) {
                    amount.setError("Email is required");
                    return;
                }

            }

        });
    }
}

