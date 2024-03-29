package com.example.aprio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Fragment.FragmentMapSeller;
import com.example.aprio.Fragment.FragmentProductsSeller;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapSellerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_seller);

        CircleImageView imageView = findViewById(R.id.ivUserAvatar);
        Glide.with(this).load(ParseUser.getCurrentUser().getParseFile("ProfileImg").getUrl())
                .apply(new RequestOptions().placeholder(R.drawable.error).error(R.drawable.error))
                .into(imageView);

        //navigationview
        NavigationView navigationView = findViewById(R.id.vendor_navigation_view);
        View headerLayout = navigationView.getHeaderView(0);

        CircleImageView ivHeaderPhoto = (CircleImageView) headerLayout.findViewById(R.id.ivUser);
        ivHeaderPhoto.setBorderColor(Color.WHITE);
        ivHeaderPhoto.setBorderWidth(10);

        Glide.with(this).load(ParseUser.getCurrentUser().getParseFile("ProfileImg").getUrl())
                .apply(new RequestOptions().placeholder(R.drawable.error).error(R.drawable.error))
                .into(ivHeaderPhoto);

        TextView tvHeaderName = headerLayout.findViewById(R.id.tvUserName);
        tvHeaderName.setText(ParseUser.getCurrentUser().getUsername());

        TextView tvHeaderEmail = headerLayout.findViewById(R.id.tvUserEmail);
        tvHeaderEmail.setText(ParseUser.getCurrentUser().getEmail());

        TextView tvLogout = headerLayout.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(MapSellerActivity.this);
                progressDialog.setTitle("Logging out...");
                progressDialog.setMessage("Please wait.");
                progressDialog.setCancelable(false);
                progressDialog.show();
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ic_drawer_message:
                        Intent i = new Intent(MapSellerActivity.this,ViewMessage.class);
                        startActivity(i);
                        break;
                    case R.id.ic_drawer_favorite:
                        Intent i1 = new Intent(MapSellerActivity.this,ViewFavorites.class);
                        startActivity(i1);
                        break;
                }
                return true;
            }
        });

        drawerLayout= findViewById(R.id.drawer1);
        actionBarDrawerToggle=new ActionBarDrawerToggle(MapSellerActivity.this,drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //img on click to open drawer
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoadFragment(new FragmentMapSeller());

        BottomNavigationView tabs = findViewById(R.id.tabs);
        tabs.setOnNavigationItemSelectedListener(this);
        tabs.setSelectedItemId(R.id.btnMapFragment);
    }

    private Boolean LoadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.FrameLayout, fragment)
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

//    public void add(View view) {
//        startActivity(new Intent(this,AddProduct.class));
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
