package com.muhammmagdy.smartdoorbell;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class videoCallActivity extends AppCompatActivity{

    WebView localVideoWebView;
    WebView remoteVideoWebView;
    FirebaseDatabase fDatabase;
    DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        initView();
        initFirebaseDatabase();
        enableWebViews();
    }


    private void initView(){
        localVideoWebView = findViewById(R.id.local_webView);
        remoteVideoWebView = findViewById(R.id.remote_webView);
    }

    private void initFirebaseDatabase(){
        fDatabase = FirebaseDatabase.getInstance();
        dbReference = fDatabase.getReference();
    }

    private void enableWebViews(){
        remoteVideoWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        remoteVideoWebView.getSettings().setJavaScriptEnabled(true);
        remoteVideoWebView.loadUrl("https://mohamed1996.pythonanywhere.com/callee");
    }



}

