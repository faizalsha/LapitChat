package com.faizal.shadab.lapitchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountSettings extends AppCompatActivity implements View.OnClickListener {

    //UI Elements
    EditText edtDisplayName;
    Button btnDisplayChange;
    EditText edtStatus;
    Button btnStatusChange;

    //Firebase
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //Ui Related
        edtDisplayName = findViewById(R.id.ac_settings_edt_display_name);
        btnDisplayChange = findViewById(R.id.ac_settings_btn_display_name);
        edtStatus = findViewById(R.id.ac_settings_edt_status);
        btnStatusChange = findViewById(R.id.ac_settings_btn_status);

        btnDisplayChange.setOnClickListener(this);
        btnStatusChange.setOnClickListener(this);


        //Firebase related
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUid);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                edtDisplayName.setText(displayName);
                edtStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ac_settings_btn_display_name:
                String name = edtDisplayName.getText().toString();
                if (!name.isEmpty()){
                    mDatabaseReference.child("name").setValue(name);
                } else Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();

                break;
            case R.id.ac_settings_btn_status:
                String status = edtStatus.getText().toString();
                if (!status.isEmpty()){
                    mDatabaseReference.child("status").setValue(status);
                } else Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
