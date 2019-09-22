package com.example.aprio;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeUser extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
@Override
protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    drawerLayout= findViewById(R.id.drawer);
    actionBarDrawerToggle=new ActionBarDrawerToggle(HomeUser.this,drawerLayout,R.string.Open,R.string.Close);
    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
   // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    ImageView imgprofil=findViewById(R.id.imgprofildrawer);
   /* imgprofil.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(HomeUser.this,profildetail.class));
        }
    }); */
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

@Override
public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
        return false;
        }}
