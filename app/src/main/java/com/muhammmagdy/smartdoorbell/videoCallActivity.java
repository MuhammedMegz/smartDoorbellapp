package com.muhammmagdy.smartdoorbell;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
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

    WebView voiceCallWebView;

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


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        initView();
        initFirebaseDatabase();
        initWebView();
        enableWebViews();
        doorGrantBtn.setOnClickListener(view -> initAlertDialog());
        endCallBtn.setOnClickListener(v -> {
            resetCallDB();
            goToMainActivity();
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
        voiceCallWebView.destroy();
        resetCallDB();
        dbReference.child("bellButton").setValue(0);
        goToMainActivity();

    }

    @Override
    protected void onStop() {
        super.onStop();
        voiceCallWebView.destroy();
        dbReference.child("bellButton").setValue(0);
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
        voiceCallWebView = findViewById(R.id.remote_webView);
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
                    voiceCallWebView.destroy();
                    dbReference.child("bellButton").setValue(0);
                    dbReference.child("DoorGrant").setValue(1);
                    goToMainActivity();
                 })
                .setNegativeButton("No", null)
                .show();
    }


    private void initWebView() {
        WebSettings webSettings = voiceCallWebView.getSettings();
        // Enable Javascript
        webSettings.setJavaScriptEnabled(true);
        // Enable multiple window
        webSettings.setSupportMultipleWindows(true);
        // Allow WebView Content access
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(true);
        }
    }


    private void enableWebViews(){

        voiceCallWebView.getSettings().setLoadsImagesAutomatically(true);
        voiceCallWebView.getSettings().setLoadWithOverviewMode(true);
        voiceCallWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        voiceCallWebView.setScrollbarFadingEnabled(true);
        voiceCallWebView.getSettings().setAllowFileAccess(true);
        voiceCallWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        voiceCallWebView.getSettings().setDomStorageEnabled(true);
        voiceCallWebView.getSettings().setAppCacheEnabled(true);
        voiceCallWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        voiceCallWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        voiceCallWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        voiceCallWebView.loadUrl("https://mohamed1996.pythonanywhere.com/android");
    }


    public void resetCallDB(){
        if (!(peopleInRoom < 1))
            dbReference.child("peopleInRoom").setValue(peopleInRoom-1);
        dbReference.child("CalleeToCaller").setValue("\"bye\"");
        dbReference.child("bellButton").setValue(0);
    }

    private void goToMainActivity(){

        Intent main = new Intent(videoCallActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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


//    private void setUpWebViewDefaults(WebView webView) {
//        WebSettings settings = webView.getSettings();
//
//        // Enable Javascript
//        settings.setJavaScriptEnabled(true);
//
//        // Use WideViewport and Zoom out if there is no viewport defined
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//
//        // Enable pinch to zoom without the zoom buttons
//        settings.setBuiltInZoomControls(true);
//
//        // Allow use of Local Storage
//        settings.setDomStorageEnabled(true);
//
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//            // Hide the zoom controls for HONEYCOMB+
//            settings.setDisplayZoomControls(false);
//        }
//
//        // Enable remote debugging via chrome://inspect
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
//
//        webView.setWebViewClient(new WebViewClient());
//
//        // AppRTC requires third party cookies to work
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptThirdPartyCookies(voiceCallWebView, true);
//    }



}

