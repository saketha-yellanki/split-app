package com.splitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.splitapp.R;
import com.splitapp.models.ModelFriendList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddExpenses extends AppCompatActivity {

    FirebaseAuth mAuth;
    String current_user_id;
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
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
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

        // names.add("Me");

        paid_member.setAdapter(new ArrayAdapter<>(AddExpenses.this, android.R.layout.simple_list_item_1, names));
        paid_member.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                paid_member.showDropDown();
                paid_member.requestFocus();
                return false;
            }
        });
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
        double share = amount / (dividedAmong.size() + 1);
        CollectionReference rootref = FirebaseFirestore.getInstance().collection("users");
        if (names.get(pos[0]).toString().equals("Me")) {
            for (int i = 0; i < dividedAmong.size(); i++) {

                double original = Double.parseDouble(sharedBy.get(dividedAmong.get(i)).getAmount());
                sharedBy.get(dividedAmong.get(i)).setAmount(share + original);
                Double current_amt = Double.parseDouble(sharedBy.get(dividedAmong.get(i)).getAmount());

                Map<String, Object> temp = new HashMap<>();
                temp.put("transactionAmount", (Object) Double.toString(current_amt));
                rootref.document(sharedBy.get(dividedAmong.get(i)).getUid()).collection("Friends")
                        .document(current_user_id).update(temp);
            }
            updateLocalWithDb();
        } else {
            ModelFriendList paid_member = sharedBy.get(pos[0]);
            paid_member.setAmount(Double.parseDouble(paid_member.getAmount()) - share);

        }
    }

    private void updateLocalWithDb() {
        final ArrayList<ModelFriendList> friends = FriendsList.getInstance().friends;
        final CollectionReference rootref = FirebaseFirestore.getInstance().collection("users");
        rootref.document(current_user_id).collection("Friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (int i = 0; i < friends.size(); i++) {
                            HashMap<String, String> map = new HashMap();
                            map.put("friendName", friends.get(i).getName());
                            map.put("friendEmail", friends.get(i).getEmail());
                            map.put("friendPhone", friends.get(i).getPhone());
                            map.put("transactionAmount", friends.get(i).getAmount());
                            rootref.document(current_user_id).collection("Friends").document(friends.get(i).getUid())
                                    .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddExpenses.this, "Successfully updated", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(AddExpenses.this, ProfileActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(AddExpenses.this, "Failed" + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void loadNames() {
        for (int i = 0; i < sharedBy.size(); i++) {
            names.add(i, sharedBy.get(i).getName());
        }
        names.add("Me");
    }
}
