package com.faizal.shadab.lapitchat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //UI elements
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;


    //Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Lets Chat");

        //UI related..
        mViewPager = findViewById(R.id.main_view_pager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        //Firebase instances related..
        if(!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_log_out:
                logOutUser();
                break;
            case R.id.item_account:
                sendToSettings();
                break;
            case R.id.item_all_users:
                sendToAllUsers();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void sendToAllUsers() {
        Intent intent = new Intent(MainActivity.this, AllUserActivity.class);
        startActivity(intent);
    }

    private void sendToSettings() {
        Intent settingIntent = new Intent(MainActivity.this, Settings.class);
        startActivity(settingIntent);
    }

    private void logOutUser() {
        mAuth.signOut();
        sendToHome();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToHome();
            finish();
        }
    }

    private void sendToHome() {
        Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
