package com.example.aprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeSeller extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_seller);
        LoadFragment(new FragmentMapSeller());

        BottomNavigationView tabs = findViewById(R.id.tabs);
        tabs.setOnNavigationItemSelectedListener(this);
        tabs.setSelectedItemId(R.id.btnMapFragment);
    }

    private Boolean LoadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.FrameLayout, fragment)
                    .commit();
            return true;
        }else {
            return false;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.btnMapFragment:
                fragment = new FragmentMapSeller();
                break;

            case R.id.btnMyProductsFragment:
                fragment = new FragmentProductsSeller();
                break;

        }
        return LoadFragment(fragment);
    }

    public void add(View view) {
        startActivity(new Intent(this,addproduct.class));
    }
}