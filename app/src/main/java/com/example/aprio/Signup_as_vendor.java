package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.ViewParent;

public class Signup_as_vendor extends AppCompatActivity {
    private ViewPager SlideviewPager;
    private slideAdapter slideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_as_vendor);
        SlideviewPager=findViewById(R.id.slideViewPager);

        slideAdapter=new slideAdapter(this);
        SlideviewPager.setAdapter(slideAdapter);
    }
}
