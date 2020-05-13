package com.splitapp.activities;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialAutoCompleteTextView;
import com.splitapp.R;
import com.splitapp.models.ModelFriendList;

import java.util.ArrayList;

public class AddExpenses extends AppCompatActivity {

    MultiAutoCompleteTextView email_inp;
    TextInputEditText amount_inp;
    MaterialButton add_exp_btn;
    final int[] pos = new int[1];
    ArrayList<ModelFriendList> sharedBy = FriendsList.getInstance().friends;
    ArrayList<String> names = new ArrayList<>();
    final ArrayList<Integer> dividedAmong = new ArrayList<>();
    MaterialAutoCompleteTextView paid_member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        email_inp = findViewById(R.id.email_et_auto);
        amount_inp = findViewById(R.id.amount_et);
        add_exp_btn = findViewById(R.id.add_exp_btn);
        paid_member = findViewById(R.id.paid_by_auto);
        loadNames();

        email_inp.setAdapter(new ArrayAdapter<>(AddExpenses.this, android.R.layout.simple_list_item_1, names));
        email_inp.setThreshold(1);
        email_inp.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        email_inp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                email_inp.showDropDown();
                email_inp.requestFocus();
                return false;
            }
        });


        email_inp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                int pos1 = -1;
                for (int in = 0; in < names.size(); i++) {
                    if (names.get(i).equals(selected)) {
                        pos1 = i;
                        break;
                    }
                }
                dividedAmong.add(pos1);

            }
        });

        names.add(0, "Me");
        paid_member.setAdapter(new ArrayAdapter<>(AddExpenses.this, android.R.layout.simple_list_item_1, names));
        paid_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                int pos2 = -1;
                for (int in = 0; in < names.size(); i++) {
                    if (names.get(i).equals(selected)) {
                        pos2 = i;
                        break;
                    }
                }
                pos[0] = pos2;

            }
        });

        add_exp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amount = Double.parseDouble(amount_inp.getText().toString());
                updateTransactionAmounts(amount);
            }
        });
    }


    private void updateTransactionAmounts(double amount) {
        double share = amount / dividedAmong.size() + 1;
        if (names.get(pos[0]).toString().equals("Me")) {
            for (int i = 0; i < dividedAmong.size(); i++) {
                sharedBy.get(dividedAmong.get(i)).setAmount(share);
            }
        } else {
            ModelFriendList paid_member = sharedBy.get(pos[0]);
            paid_member.setAmount(-amount);
        }


    }

    private void loadNames() {
        for (int i = 0; i < sharedBy.size(); i++) {
            names.add(i, sharedBy.get(i).getName());
        }
    }
}
