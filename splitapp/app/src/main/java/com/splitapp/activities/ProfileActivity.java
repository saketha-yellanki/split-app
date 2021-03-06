package com.splitapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.splitapp.R;
import com.splitapp.adapters.ViewPAgeAdapter;
import com.splitapp.fragments.FragmentFriend;
import com.splitapp.fragments.FragmentGroups;
import com.splitapp.fragments.FragmentTransactions;
import com.splitapp.models.ModelFriendList;

public class ProfileActivity extends AppCompatActivity {

    MaterialButton signout_btn;
    FirebaseAuth mAuth;
    MaterialTextView mUid;
    AlertDialog.Builder builder;
    private TabLayout tablayout;
    private ViewPager viewPager;
    private ViewPAgeAdapter adapter;
    final FirebaseUser this_user = FirebaseAuth.getInstance().getCurrentUser();
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadFriends();
        String f_uid = this_user.getUid();

        signout_btn = findViewById(R.id.signout_btn);
        tablayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.ViewPager_id);
        adapter = new ViewPAgeAdapter(getSupportFragmentManager());

        adapter.AddFragment(new FragmentFriend(), "Friends");
        adapter.AddFragment(new FragmentGroups(), "Groups");
        adapter.AddFragment(new FragmentTransactions(), "Activity");

        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);


//        signout_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });

        if (!this_user.isEmailVerified()) {

            builder = new AlertDialog.Builder(this);
            builder.setMessage("Please click here to verify you're email id").setTitle("Email Not Verified")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            this_user.sendEmailVerification()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileActivity.this, "Verification Email Has been Sent", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });


                        }
                    }).setNegativeButton(android.R.string.no, null).show();
        }
    }

    private void loadFriends() {
        CollectionReference rootref = FirebaseFirestore.getInstance().collection("users").document(this_user.getUid()).collection("Friends");
        rootref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String f_name = doc.get("friendName").toString();
                    String f_email = doc.get("friendEmail").toString();
                    String f_phone = doc.get("friendPhone").toString();
                    double f_amount = Double.parseDouble(doc.get("transactionAmount").toString());
                    String f_id = doc.getId();

                    ModelFriendList friend = new ModelFriendList(f_name, f_email, f_phone, f_id, f_amount);
                    FriendsList.getInstance().friends.add(friend);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sign_out) {
            // do something
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
