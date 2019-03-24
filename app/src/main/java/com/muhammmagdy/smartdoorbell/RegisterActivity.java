package com.muhammmagdy.smartdoorbell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.muhammmagdy.smartdoorbell.LoginActivity;
import com.muhammmagdy.smartdoorbell.MainActivity;
import com.muhammmagdy.smartdoorbell.R;
import com.muhammmagdy.smartdoorbell.setupActivity;

public class RegisterActivity extends AppCompatActivity {


    private EditText regEmail;
    private EditText regPassword;
    private EditText regConfrimPassword;
    private Button regConfirmBtn;
    private Button regLoginBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmail = (EditText) findViewById(R.id.register_email_txt);
        regPassword = (EditText) findViewById(R.id.register_password_txt);
        regConfrimPassword = (EditText) findViewById(R.id.register_password_confirm_txt);
        regConfirmBtn = (Button) findViewById(R.id.register_cofirm_btn);
        regLoginBtn = (Button) findViewById(R.id.register_login_btn);

        regConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = regEmail.getText().toString();
                String password = regPassword.getText().toString();
                String confirmPassword = regConfrimPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){

                    if(email.isEmpty()){
                        regEmail.setError("Please Enter Email");
                    }if(password.isEmpty()){
                        regPassword.setError("Please Enter Password");
                    }if (confirmPassword.isEmpty()){
                        regConfrimPassword.setError("please confirm Your Password");
                    }
                }else {
                    if(password.trim().equals(confirmPassword.trim())){

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToSetupActivity();
                                }else {
                                    String errMsg = task.getException().toString();
                                    Toast.makeText(RegisterActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        regConfrimPassword.setError("Not Matching Password");
                    }
                }
            }
        });

        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToLoginPage();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            sendToMainActivity();
        }
    }


    private void sendToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToSetupActivity(){
        Intent intent = new Intent(RegisterActivity.this, setupActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToLoginPage(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
