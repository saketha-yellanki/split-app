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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.splitapp.R;
import com.splitapp.adapters.ViewPAgeAdapter;
import com.splitapp.fragments.FragmentFriend;
import com.splitapp.fragments.FragmentGroups;
import com.splitapp.fragments.FragmentTransactions;

public class ProfileActivity extends AppCompatActivity {

    MaterialButton signout_btn;
    FirebaseAuth mAuth;
    MaterialTextView mUid;
    AlertDialog.Builder builder;
    private TabLayout tablayout;
    private ViewPager viewPager;
    private ViewPAgeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FirebaseUser this_user = FirebaseAuth.getInstance().getCurrentUser();
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
