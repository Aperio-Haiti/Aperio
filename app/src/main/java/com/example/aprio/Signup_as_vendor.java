package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Signup_as_vendor extends AppCompatActivity implements FragmentLayout1.FragmentLayout1Listener {

    FragmentLayout1 f1;
    FragmentLayout2 f2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_as_vendor);
        Toolbar toolbar = findViewById(R.id.toolbarSignupVendor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Sign Up");

        f1 = new FragmentLayout1();
        f2 = new FragmentLayout2();

        LoadFragment(f1);
    }

    private Boolean LoadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.slideViewPager, fragment,"Frag1")
                    .addToBackStack("frag1").commit();
            return true;
        }else {
            return false;
        }
    }

    public void Fragment2(View view) {
        Fragment fm;
        if(view==findViewById(R.id.btnPost))
        {
            fm=new FragmentLayout2();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.slideViewPager, fm,"Frag2")
                    .addToBackStack("frag2").hide(f1).commit();

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.d("CountF",String.valueOf(count));
        if (count <= 1) {
            finish();
            return true;
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.d("CountF",String.valueOf(count));
        if (count <= 1) {
            finish();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void Log(View view) {
        startActivity(new Intent(this,HomeSeller.class));
    }

    @Override
    public void onInputFragmentLayout1Sent(CharSequence username, CharSequence password, CharSequence email, CharSequence phone) {
        f2=new FragmentLayout2();
        f2.updateEditText(username,password,email,phone);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.slideViewPager, f2,"Frag2")
                .addToBackStack("frag2").hide(f1).commit();
    }
}
