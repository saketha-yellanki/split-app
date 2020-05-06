package com.splitapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.splitapp.R;

public class GroupMainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String groupTitle,groupId,myGroupRole="";
    private ActionBar actionBar;
    private FloatingActionButton AddFriendBtnGrp;



    //Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        groupTitle=getIntent().getStringExtra("groupTitle");
        groupId=getIntent().getStringExtra("groupId");
        actionBar.setTitle(groupTitle);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        AddFriendBtnGrp = findViewById(R.id.fab_btn_group_main);

        AddFriendBtnGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupMainActivity.this,GroupParticipantAddActivity.class);
                intent.putExtra("groupTitle",groupTitle);
                intent.putExtra("groupId",groupId);
                startActivity(intent);

            }
        });
        /*toolbar=findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");*/
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

    /*private void setSupportActionBar(Toolbar toolbar) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        menu.findItem(R.id.menu_sign_out).setVisible(false);
        if(myGroupRole.equals("creator")){
            menu.findItem(R.id.action_add_participant).setVisible(true);
        }
        else{
            menu.findItem(R.id.action_add_participant).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_add_participant){
            Intent intent=new Intent(this,GroupParticipantAddActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/
}
