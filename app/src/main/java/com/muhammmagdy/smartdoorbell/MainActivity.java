package com.muhammmagdy.smartdoorbell;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolBar;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainToolBar =  findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);

        getSupportActionBar().setTitle("Smart Doorbell");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.child("bellButton").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(MainActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                if(dataSnapshot.getValue().toString().equals("1")) {
                    myRef.child("bellButton").setValue(0);
                    startActivity(new Intent(MainActivity.this, callActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("bellButton").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(MainActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                if(dataSnapshot.getValue().toString().equals("1")) {
                    myRef.child("bellButton").setValue(0);
                    startActivity(new Intent(MainActivity.this, callActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("bellButton").setValue(0);
        //myRef.child("DoorGrand").setValue(0);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null){    //no user is signed in
            sendToLoginPage();
        }else {                     //user signed in
            //sendToRegisterPage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.action_settings_btn:
                goToAccountSetup();

        }
        return false;
    }

    private void goToAccountSetup() {
        Intent intent = new Intent(MainActivity.this, setupActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuth.signOut();
        sendToLoginPage();
    }


    private void sendToLoginPage(){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToRegisterPage(){
        Intent RegisterIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(RegisterIntent);
        finish();
    }

}













