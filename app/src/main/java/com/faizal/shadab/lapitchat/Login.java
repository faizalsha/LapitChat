package com.faizal.shadab.lapitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    //UI Elements
    private Toolbar toolbar;
    private TextInputLayout edtEmail;
    private TextInputLayout edtPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    //Firebase Instance
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        toolbar = findViewById(R.id.log_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //UI elements related
        edtEmail = findViewById(R.id.log_edt_email);
        edtPassword = findViewById(R.id.log_edt_password);
        btnLogin = findViewById(R.id.log_btn_login);
        progressBar = findViewById(R.id.log_progress_bar);

        btnLogin.setOnClickListener(this);

        //Firebase Related..
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.log_btn_login:
                loginUser();
                break;
        }
    }

    private void loginUser() {
        String email = edtEmail.getEditText().getText().toString();
        String password = edtPassword.getEditText().getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                sendToMain();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else
            Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(Login.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
