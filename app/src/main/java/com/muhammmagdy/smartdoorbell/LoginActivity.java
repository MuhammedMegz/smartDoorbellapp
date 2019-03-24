package com.muhammmagdy.smartdoorbell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar loginProgBar;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailText = (EditText) findViewById(R.id.register_email_txt);
        loginPassText = (EditText) findViewById(R.id.register_password_txt);
        loginBtn = (Button) findViewById(R.id.login_cofirm_btn);
        loginRegBtn = (Button) findViewById(R.id.register_login_btn);
        loginProgBar = (ProgressBar) findViewById(R.id.login_progress);

        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String loginEmail = loginEmailText.getText().toString();
                String loginPass = loginPassText.getText().toString();

                if(loginEmail.isEmpty() || loginPass.isEmpty()){
                    if(loginEmail.isEmpty()){
                        loginEmailText.setError("Please Enter Email");
                    }if(loginPass.isEmpty()) {
                        loginPassText.setError("Please Enter Password");
                    }
                }else {

                    loginProgBar.setVisibility(view.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                sendToMainActivity();

                            }else {

                                String e = task.getException().getMessage().toString();
                                Toast.makeText(LoginActivity.this,"Error: " + e, Toast.LENGTH_SHORT).show();
                            }
                            loginProgBar.setVisibility(view.INVISIBLE);

                        }
                    });
                }

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
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
