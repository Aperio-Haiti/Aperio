package com.example.aprio;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpRequest;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsUserActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_FINE_LOCATION = 1234;
    private GoogleMap mMap;
    List<ParseUser> list;
    FloatingActionButton fab;
    LatLng myCoordinates;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_user);

        Toolbar toolbar = findViewById(R.id.mapusertoolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayShowTitleEnabled(false);


        CircleImageView imageView = findViewById(R.id.ivUserAvatar);
        Glide.with(this).load(ParseUser.getCurrentUser().getParseFile("ProfileImg").getUrl())
                .apply(new RequestOptions().placeholder(R.drawable.error).error(R.drawable.error))
                .into(imageView);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MapsUserActivity.this,drawerLayout,R.string.Open,R.string.Close);

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //img on click to open drawer
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //navigationview
        NavigationView navigationView = findViewById(R.id.user_navigation_view);
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
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }
                        Intent intent = new Intent(MapsUserActivity.this,Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        fab = findViewById(R.id.fabLocateUser);
        myCoordinates = new LatLng(18.534275,-72.324748);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }



    /*public static Bitmap createCustomMarker(Context context, ParseFile img, String _name){

       /* View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        Log.d("MAP_FETCH","Marker : "+img.getUrl());
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setBorderWidth(3);
        markerImage.setBorderColor(ContextCompat.getColor(context, R.color.colorPrimary));

        Picasso.get().load(img.getUrl())
                .into(markerImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        TextView txt_name = (TextView) marker.findViewById(R.id.name);
                        txt_name.setText(_name);

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
                        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
                        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
                        marker.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        marker.draw(canvas);

                        return bitmap;
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }*/

    public void getAllVendors(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("Category",true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e!=null){
                    Log.d("MAP_FETCH",e.toString());
                    e.printStackTrace();
                    return;
                }
                list.clear();
                list.addAll(objects);
                Log.d("MAP_FETCH",String.valueOf(list.size()));
                Log.d("MAP_FETCH","I'm "+list.get(0).getUsername());

                for (int i=0; i <list.size(); i++){
                    Log.d("MAP_FETCH",list.get(i).getUsername());
                    putAllMarkersOnMap(list.get(i));
                }
            }
        });
    }

    public void putAllMarkersOnMap(ParseUser user){
        Context context = (Context) MapsUserActivity.this;
        ParseFile img = user.getParseFile("ProfileImg");
        String _name = "Lol";

        LatLng coordinates = new LatLng(user.getDouble("Latitude"),user.getDouble("Longitude"));

        /*MarkerOptions options = new MarkerOptions();
        options.position(coordinates).title(user.getUsername())
                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(this,user.getParseFile("ProfileImg"),user.getUsername())));
        mMap.addMarker(options);*/

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        Log.d("MAP_FETCH","Marker : "+img.getUrl());
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setBorderWidth(3);
        markerImage.setBorderColor(ContextCompat.getColor(context, R.color.colorPrimary));

        Picasso.get().load(img.getUrl())
                .into(markerImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        TextView txt_name = (TextView) marker.findViewById(R.id.name);
                        txt_name.setText(_name);

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
                        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
                        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
                        marker.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        marker.draw(canvas);

                        MarkerOptions options = new MarkerOptions();
                        options.position(coordinates).title(user.getUsername())
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        mMap.addMarker(options);

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("MapUser","Erreur marker img: "+e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    public void getLastKnownLocation(){
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d("MAP_FETCH",msg);
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        myCoordinates = new LatLng(location.getLatitude(),location.getLongitude());
        //Option camera to focus in target
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCoordinates)      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east// Sets the tilt of the camera to 30 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(checkPermissions()) {
            googleMap.setMyLocationEnabled(true);
            getLastKnownLocation();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermissions()) {
                    googleMap.setMyLocationEnabled(true);
                    getLastKnownLocation();
                }
            }
        });

        list = new ArrayList<>();
        getAllVendors();

        mMap.setOnMarkerClickListener(this);

        //to remove the compass under the toolbar
        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setMyLocationButtonEnabled(false);

        /*Test coordinates on Parse
        * 18.534275,-72.323027
        * 18.533444,-72.324748
        * 18.534899,-72.325964
        * */

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("MAP_FETCH","Title "+marker.getTitle());
        Intent intent = new Intent(MapsUserActivity.this, ProfileDetail.class);
        intent.putExtra("Title",marker.getTitle());
        startActivity(intent);
        return false;
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_FINE_LOCATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.app_bar_search:

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsUserActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.search_dialog,null);

                TextView inputSearch = mView.findViewById(R.id.input_search);

                inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                                || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                            Toast.makeText(getApplicationContext(),"Goog",Toast.LENGTH_SHORT).show();
//                            geoLocate();
                        }
                        return false;
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog =mBuilder.create();
                dialog.show();
                return true;
            case R.id.app_bar_list:
                return true;
                default:
                    return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}
