package com.muhammmagdy.smartdoorbell;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.webrtc.AudioSource;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.CameraEnumerator;
import org.webrtc.Camera1Enumerator;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;



public class videoCallActivity extends AppCompatActivity {

    PeerConnectionFactory peerConnectionFactory;
    PeerConnection localPeer, remotePeer;

    VideoSource videoSource;
    VideoTrack videoTrack;

    SurfaceViewRenderer localVideoView;
    SurfaceViewRenderer remoteVideoView;

    boolean gotUserMedia;


    Button openDoorBtn, start, call, hangup;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        String VIDEO_TRACK_ID = "M16A4AR";
        String AUDIO_TRACK_ID = "M416AR";

        openDoorBtn = findViewById(R.id.open_door_btn);
        start = findViewById(R.id.start_btn);
        call = findViewById(R.id.call_btn);
        hangup = findViewById(R.id.hangup_btn);
        localVideoView = findViewById(R.id.local_gl_surface_view);
        remoteVideoView = findViewById(R.id.remote_gl_surface_view);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("DoorGrant");
        myRef.setValue(0);

        initPeerConnection();
        initPeerConnectionFactorty();
        initVideoAudioCapturer(VIDEO_TRACK_ID, AUDIO_TRACK_ID);

//        openDoorBtn.setOnClickListener(view -> {
//            myRef.setValue(1);
//            startActivity(new Intent(videoCallActivity.this, MainActivity.class));
//            finish();
//
//        });

    }

    private void initPeerConnection() {
        //parameters: (context, initialize_audio, initialize_video, videoCodecHwAcceleration, renderEGLContext)
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .setEnableVideoHwAcceleration(true)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
    }

    private void initPeerConnectionFactorty(){
        peerConnectionFactory = new PeerConnectionFactory();
    }

    private void initVideoAudioCapturer(String VIDEO_TRACK_ID, String AUDIO_TRACK_ID) {




        VideoCapturer capturer = createCameraCapturer(new Camera1Enumerator(false));

        if (capturer != null)
            videoSource = peerConnectionFactory.createVideoSource(capturer);

        videoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);

        if (capturer != null)
            capturer.startCapture(1024, 720, 30);

        localVideoView.setVisibility(View.VISIBLE);
        remoteVideoView.setVisibility(View.VISIBLE);
        videoTrack.addSink(localVideoView);
        localVideoView.setMirror(true);
        remoteVideoView.setMirror(true);

        gotUserMedia = true;


    }



    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front acing camera
        Logging.d("CAMERA", "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d("CAMERA", "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d("CAMERA", "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d("CAMERA", "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

}

