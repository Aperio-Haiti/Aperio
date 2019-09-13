package com.example.aprio;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeUser extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

@Override
protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeuser);
        }

@Override
public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
        return false;
        }}
