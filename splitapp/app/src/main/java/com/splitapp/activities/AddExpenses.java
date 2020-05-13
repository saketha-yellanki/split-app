package com.splitapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialAutoCompleteTextView;
import com.splitapp.R;

public class AddExpenses extends AppCompatActivity {

    MaterialAutoCompleteTextView email_inp;
    TextInputEditText amount_inp;
    MaterialButton add_exp_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        email_inp = findViewById(R.id.email_et_auto);
        amount_inp = findViewById(R.id.amount_et);
        add_exp_btn = findViewById(R.id.add_exp_btn);

        add_exp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] emails = null;
                if (!TextUtils.isEmpty(email_inp.getText())) {
                    String input = email_inp.getText().toString();
                    emails = input.split("\n");

                }

            }
        });



    }
}
