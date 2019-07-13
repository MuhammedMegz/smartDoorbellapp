package com.muhammmagdy.smartdoorbell;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolBar;
    AlertDialog.Builder alertDialogBuilder;
    EditText inputVisitorName;
    TextView doorState;
    Button addVisitor;
    ImageView doorStateLock;


    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;


    String global_visitorName = "";
    String encodedImage = null;

    private static final int galleryPick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetDatabase();
        initViews();
        initFirebase();
        customActionBar();
        alertDialogInit();



        addVisitor.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),galleryPick);
        });

        myRef.child("doorbell_status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int doorVal = Integer.parseInt(dataSnapshot.getValue().toString());
                if (doorVal == 1) {
                    //doorState.setText("Door Current State: ");
                    Toast.makeText(MainActivity.this, "Door is open now !", Toast.LENGTH_SHORT).show();
                    doorStateLock.setImageResource(R.mipmap.open_lock);

                } else if (doorVal == 0) {
                    //doorState.setText("Door Current State: ");
                    Toast.makeText(MainActivity.this, "Door is closed now !", Toast.LENGTH_SHORT).show();
                    doorStateLock.setImageResource(R.mipmap.close_lock);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("bellButton").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(MainActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                if (dataSnapshot.getValue().toString().equals("1")) {
                    //myRef.child("bellButton").setValue(0);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         InputStream imageStream = null;
         Bitmap selectedImage;

        if (requestCode==galleryPick && resultCode==RESULT_OK && data!=null) {
           //Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();


                try {
                    imageStream = getContentResolver().openInputStream(resultUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                selectedImage = BitmapFactory.decodeStream(imageStream);
               encodedImage = encodeImage(selectedImage);
            }
            alertDialogInit();
            alertDialogBuilder.show();
        }
    }



    private void customActionBar() {
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle("Smart Doorbell");
    }

    private void initViews() {
        mainToolBar = findViewById(R.id.main_toolbar);
        addVisitor = findViewById(R.id.addNewVisitor);
        doorState = findViewById(R.id.doorStateTxt);
        doorStateLock = findViewById(R.id.doorLockIcon);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logout();
                return true;
//            case R.id.action_settings_btn:
//                goToAccountSetup();

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


    private void sendToLoginPage() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToRegisterPage() {
        Intent RegisterIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(RegisterIntent);
        finish();
    }

    private void resetDatabase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //myRef.child("bellButton").setValue(0);
        myRef.child("DoorGrant").setValue(0);
        myRef.child("AV").setValue("null");
    }

    private void startIncomingCallActivity() {
        Intent incomingCallActivity = new Intent(MainActivity.this, callActivity.class);
        startActivity(incomingCallActivity);
        finish();
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 65, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void alertDialogInit() {

        alertDialogBuilder = new AlertDialog.Builder(this);
        inputVisitorName = new EditText(this);
        inputVisitorName.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialogBuilder.setView(inputVisitorName);
        alertDialogBuilder.setIcon(R.mipmap.new_user);
        alertDialogBuilder.setView(inputVisitorName, 50, 30, 50, 30);
        alertDialogBuilder.setTitle("Add New Visitor Name");
        alertDialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            global_visitorName = inputVisitorName.getText().toString();
            myRef.child("VISITORS IMGS").child("Base64").setValue(encodedImage);
            myRef.child("VISITORS IMGS").child("Name").setValue(global_visitorName);
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}














