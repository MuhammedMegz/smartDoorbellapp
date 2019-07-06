package com.muhammmagdy.smartdoorbell;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class callActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference databaseReference;
    DatabaseReference dataSnapshot;

    ImageButton videoCallBtn;
    ImageButton voiceCallBtn;
    ImageButton delinceCallBtn;

    TextView vNameTxt;

    Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        initFirebaseDatabase();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoCallBtn = findViewById(R.id.video_call_button);
        voiceCallBtn = findViewById(R.id.voice_call_button);
        delinceCallBtn = findViewById(R.id.delince_button);
        vNameTxt = findViewById(R.id.vistorName);

        databaseReference.child("visitorName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String vName = dataSnapshot.getValue().toString().trim();
                if (!vName.equals("Unkown Visitor"))
                    vNameTxt.setText(vName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        vNameTxt = databaseReference.child("visitorName").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoCallBtn.setOnClickListener(view -> {
            r.stop();
            databaseReference.child("AV").setValue("video");
            sendToVideoCall();
        });

        voiceCallBtn.setOnClickListener(view -> {
            r.stop();
            databaseReference.child("AV").setValue("audio");
            sendToVoiceCall();
        });

        delinceCallBtn.setOnClickListener(view -> {
            r.stop();
            databaseReference.child("AV").setValue("null");
            endCommingCall();
        });
    }

    private void sendToVoiceCall() {
        Intent mainIntent = new Intent(callActivity.this, voiceCall.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToVideoCall() {
            Intent mainIntent = new Intent(callActivity.this, videoCallActivity.class);
            startActivity(mainIntent);
            finish();
    }

    private void initFirebaseDatabase(){
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    private void endCommingCall(){
        dataSnapshot = databaseReference.child("CalleeToCaller");
        startActivity(new Intent(callActivity.this, MainActivity.class));
        finish();

    }


}
