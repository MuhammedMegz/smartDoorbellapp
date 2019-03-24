package com.muhammmagdy.smartdoorbell;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class callActivity extends AppCompatActivity {

    Button acceptCall;
    Button delinceCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        acceptCall = (Button) findViewById(R.id.answer_call_btn);
        delinceCall = (Button) findViewById(R.id.reject_call_btn);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        acceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToVideoCall();
            }
        });
    }

    private void sendToVideoCall() {
            Intent mainIntent = new Intent(callActivity.this, videoCallActivity.class);
            startActivity(mainIntent);
            finish();
    }


}
