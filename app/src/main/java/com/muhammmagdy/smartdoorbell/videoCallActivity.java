package com.muhammmagdy.smartdoorbell;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class videoCallActivity extends AppCompatActivity{

    WebView localVideoWebView;
    WebView remoteVideoWebView;
    FirebaseDatabase fDatabase;
    DatabaseReference dbReference;
    Button doorGrantBtn;
    Button endCallBtn;
    AlertDialog openDoorAlertDialog;


    int peopleInRoom = 0;
    boolean destroyDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        initView();
        initFirebaseDatabase();
        enableWebViews();
        doorGrantBtn.setOnClickListener(view -> initAlertDialog());
        endCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCallDB();
                goToMainActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        accessCameraPermissions();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        destroyDone = true;
        remoteVideoWebView.destroy();
        resetCallDB();
        goToMainActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteVideoWebView.destroy();
        if(!destroyDone)
            resetCallDB();
        finish();
    }


    private void accessCameraPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            int hasAudioPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            int hasAudioOutPermission = checkSelfPermission(Manifest.permission.CAPTURE_AUDIO_OUTPUT);

            List<String> permissions = new ArrayList<>();

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED )
                permissions.add(Manifest.permission.CAMERA);

            if ( hasAudioOutPermission != PackageManager.PERMISSION_GRANTED )
                permissions.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);

            if ( hasAudioPermission != PackageManager.PERMISSION_GRANTED )
                permissions.add(Manifest.permission.RECORD_AUDIO);

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 111);
            }
        }
    }
    private void initView(){
        localVideoWebView = findViewById(R.id.local_webView);
        remoteVideoWebView = findViewById(R.id.remote_webView);
        doorGrantBtn = findViewById(R.id.open_door_btn);
        endCallBtn = findViewById(R.id.end_call_btn);
    }

    private void initFirebaseDatabase(){
        fDatabase = FirebaseDatabase.getInstance();
        dbReference = fDatabase.getReference();

        dbReference.child("peopleInRoom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                peopleInRoom = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initAlertDialog(){
        openDoorAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are yo sure to open the door ?").setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialogInterface, whichButton) -> {
                    resetCallDB();
                    remoteVideoWebView.destroy();
                    dbReference.child("DoorGrant").setValue(1);
                    goToMainActivity();
                 })
                .setNegativeButton("No", null)
                .show();
    }

    private void enableWebViews(){

        remoteVideoWebView.setWebChromeClient(new WebChromeClient(){
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        remoteVideoWebView.getSettings().setJavaScriptEnabled(true);
        remoteVideoWebView.getFitsSystemWindows();
        remoteVideoWebView.loadUrl("https://mohamed1996.pythonanywhere.com/android");
    }


    public void resetCallDB(){
        if (!(peopleInRoom < 1))
            dbReference.child("peopleInRoom").setValue(peopleInRoom-1);
        dbReference.child("CalleeToCaller").setValue("\"bye\"");
    }

    private void goToMainActivity(){

        Intent main = new Intent(videoCallActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    //    private int getScale(){
    //        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    //        int width = display.getWidth();
    //        Double val = new Double(width)/new Double(PIC_WIDTH);
    //        val = val * 100d;
    //        return val.intValue();
    //    }



}

