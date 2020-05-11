package com.splitapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.splitapp.R;

public class AddExpenses extends AppCompatActivity {

    TextInputEditText email_inp;
    TextInputEditText amount_inp;
    MaterialButton add_exp_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        email_inp = findViewById(R.id.email_et);
        amount_inp = findViewById(R.id.amount_et);
        add_exp_btn = findViewById(R.id.add_exp_btn);

    }
}
