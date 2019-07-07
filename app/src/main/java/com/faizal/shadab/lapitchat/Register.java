package com.faizal.shadab.lapitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private static final String USERS = "users";
    private static final String DISPLAY_NAME = "name";
    private static final String STATUS = "status";
    private static final String DEFAULT_STATUS = "Hi there, I am using lets Chat";
    private static final String DISPLAY_PICTURE = "display_picture";


    //UI elements
    Toolbar toolbar;
    private TextInputLayout edtDisplayName;
    private TextInputLayout edtEmail;
    private TextInputLayout edtPassword;
    private Button btnCreateAccount;
    private ProgressBar progressBar;

    //Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String mUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        toolbar = findViewById(R.id.reg_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Ui elements related
        edtDisplayName = findViewById(R.id.reg_edt_display_name);
        edtEmail = findViewById(R.id.reg_edt_email);
        edtPassword = findViewById(R.id.reg_edt_password);
        btnCreateAccount = findViewById(R.id.reg_btn_create_account);
        progressBar = findViewById(R.id.reg_progress_bar);

        btnCreateAccount.setOnClickListener(this);

        //Firebase related..
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reg_btn_create_account) {
            registerUser();
        }
    }

    private void registerUser() {
        final String displayName = edtDisplayName.getEditText().getText().toString();
        String email = edtEmail.getEditText().getText().toString();
        String password = edtPassword.getEditText().getText().toString();
        if (!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            progressBar.setVisibility(View.VISIBLE);


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            mUid = mAuth.getUid();
                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child(USERS).child(mUid);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put(DISPLAY_NAME, displayName);
                            userMap.put(STATUS, DEFAULT_STATUS);
                            userMap.put(DISPLAY_PICTURE, "default");
                            mUserDatabase.setValue(userMap);
                            sendToMain();
                        } else
                            Toast.makeText(Register.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        } else
            Toast.makeText(this, "Fields Can't be Empty", Toast.LENGTH_SHORT).show();

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(Register.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}
