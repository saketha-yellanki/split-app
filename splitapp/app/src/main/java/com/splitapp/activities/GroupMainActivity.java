package com.splitapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.splitapp.R;

public class GroupMainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String groupId,myGroupRole="";

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);
        toolbar=findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");
    }

    private void setSupportActionBar(Toolbar toolbar) {
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
    }
}
