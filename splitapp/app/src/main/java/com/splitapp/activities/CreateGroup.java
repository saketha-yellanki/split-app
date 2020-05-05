package com.splitapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.splitapp.R;
import com.splitapp.fragments.FragmentGroups;

import java.util.HashMap;

public class CreateGroup extends AppCompatActivity {

    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText grpTitle;
    private TextInputEditText grpDescription;
    private FloatingActionButton createGroupBtn;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Create Group");

        grpTitle = findViewById(R.id.title_edit);
        grpDescription = findViewById(R.id.grp_des_edit);
        createGroupBtn = findViewById(R.id.create_grp_btn);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingGroups();
            }
        });

    }

    private void startCreatingGroups() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Group");

        String grpTitleSt = grpTitle.getText().toString().trim();
        String grpDescriptionSt = grpDescription.getText().toString().trim();

        if (TextUtils.isEmpty(grpTitleSt)) {
            Toast.makeText(this, "Please enter group title", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        final String g_timestamp = "" + System.currentTimeMillis();
        createGroup("" + g_timestamp, "" + grpTitleSt, "" + grpDescriptionSt);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", "" + g_timestamp);
        hashMap.put("groupTitle", "" + grpTitleSt);
        hashMap.put("groupDescription", "" + grpDescriptionSt);
        hashMap.put("timestamp", "" + g_timestamp);
        hashMap.put("createdBy", "" + firebaseAuth.getUid());

        db.collection("Groups").document(g_timestamp).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", firebaseAuth.getUid());
                        hashMap1.put("role", "creator");
                        hashMap1.put("timestamp", g_timestamp);

                        db.collection("Groups").document(g_timestamp).collection("Participants").document(firebaseAuth.getUid()).set(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateGroup.this, "Group created", Toast.LENGTH_SHORT).show();
                                        reloadGroupsFragment();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateGroup.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateGroup.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void createGroup(String g_timestamp, String grpTitleSt, String grpDescriptionSt) {

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            actionBar.setSubtitle(user.getEmail());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void reloadGroupsFragment() {
        Fragment groupFragment = new FragmentGroups();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(groupFragment);
        ft.attach(groupFragment);
        ft.commit();

        startActivity(new Intent(CreateGroup.this, ProfileActivity.class));
        finish();
    }
}
