package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class Signup_as_vendor extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_as_vendor);


        LoadFragment(new FragmentLayout1());

    }

    private Boolean LoadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.slideViewPager, fragment)
                    .addToBackStack(null).commit();
            return true;
        }else {
            return false;
        }
    }

    public void Fragment2(View view) {
        Fragment fm;
        if(view==findViewById(R.id.btnNextOne))
        {
            fm=new FragmentLayout2();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.slideViewPager, fm)
                    .addToBackStack(null).commit();
        }

        else if(view==findViewById(R.id.btnNextTwo)){
            fm=new FragmentLayout3();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.slideViewPager, fm)
                    .addToBackStack(null).commit();
        }

        else if(view==findViewById(R.id.btnSignup)){
            startActivity(new Intent(this,HomeSeller.class));
        }
    }




}
