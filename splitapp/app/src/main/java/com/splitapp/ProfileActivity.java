package com.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewPager;
    private ViewPAgeAdapter adapter;
    MaterialButton signout_btn;
    FirebaseAuth mAuth;
    MaterialTextView mUid;
    MaterialTextView verify_txt;
    MaterialButton verify_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FirebaseUser this_user = FirebaseAuth.getInstance().getCurrentUser();
        String f_uid = this_user.getUid();
        mUid = findViewById(R.id.current_uid);
        signout_btn = findViewById(R.id.signout_btn);
        verify_txt = findViewById(R.id.email_not_ver_txt);
        verify_btn = findViewById(R.id.ver_email_btn);
        tablayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.ViewPager_id);
        adapter = new ViewPAgeAdapter(getSupportFragmentManager());

        adapter.AddFragment(new FragmentFriend(),"Friends");
        adapter.AddFragment(new FragmentGropus(),"Groups");
        adapter.AddFragment(new FragmentTransactions(),"Activity");

        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);

        mUid.setText("uid: " + f_uid);
        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        if (!this_user.isEmailVerified()) {
            verify_txt.setVisibility(View.VISIBLE);
            verify_btn.setVisibility(View.VISIBLE);

            verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            });
        }


    }
}
