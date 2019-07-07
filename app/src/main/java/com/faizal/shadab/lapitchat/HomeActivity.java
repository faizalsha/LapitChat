package com.faizal.shadab.lapitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    //UI elements
    private Button btnRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //Ui Related..
        btnRegister = findViewById(R.id.home_reg_btn);
        btnLogin = findViewById(R.id.home_login_btn);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_reg_btn:
                sendToRegister();
                break;
            case R.id.home_login_btn:
                sendToLogin();
                break;
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(HomeActivity.this, Login.class);
        startActivity(intent);
    }

    private void sendToRegister() {
        Intent intent = new Intent(HomeActivity.this, Register.class);
        startActivity(intent);
    }
}
