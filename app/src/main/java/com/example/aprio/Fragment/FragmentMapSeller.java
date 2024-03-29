package com.example.aprio.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.MapsUserActivity;
import com.example.aprio.ProfileDetail;
import com.example.aprio.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentMapSeller extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    List<ParseUser> list;

    private static final String TAG = "FragmentMapSeller";
    @BindView(R.id.fabLocateSeller)
    FloatingActionButton icGps;

    GoogleMap map;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private  boolean mLocationPermissionGranted = false;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final static float DEFAULT_ZOOM = 15f;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        getLocationPermission();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(),"Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        map = googleMap;

        if(mLocationPermissionGranted){
            getDeviceLocation();
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setMapToolbarEnabled(false);

            init();

            getAllNearbyVendors();
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                                };

        if((ContextCompat.checkSelfPermission(getActivity(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(getActivity(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
        {
                mLocationPermissionGranted = true;
                initMap();
        }
        else{
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){
                    for(int i = 0; i< grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: Initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try{
            if(mLocationPermissionGranted){
                Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            moveCameraToMyLocationDevice(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), DEFAULT_ZOOM,"my Location");
                        }
                        else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(),"unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException : "+e.getMessage());
        }
    }

    private void moveCameraToMyLocationDevice(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera : move the camera to : lat: " + latLng.latitude + ",lng: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("my Location")) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            map.addMarker(markerOptions);
        }
        hideSoftKeyboard();
    }

    private void init() {
        Log.d(TAG, "init: initializing");
        icGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: click gps icon");
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        Window window = getActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void moveCameraNearbyVendor(ParseUser user) {
        LatLng coordinates = new LatLng(user.getDouble("Latitude"),user.getDouble("Longitude"));
        Log.d(TAG, "moveCamera : move the camera to : lat: " + user.getDouble("Latitude") + ",lng: " + user.getDouble("Longitude"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM));
        if(user.getParseFile("ProfileImg")!=null) {
            ParseFile profileImgBitmap = user.getParseFile("ProfileImg");
            if (!user.getUsername().equals("my Location")) {
                putAllMarkersOnMap(user,profileImgBitmap,coordinates);
            }
        }
        else {
            if (!user.getUsername().equals("my Location")) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(coordinates)
                        .title(user.getUsername())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_personne));
                map.addMarker(markerOptions);
            }
        }

        hideSoftKeyboard();
    }

    public void getAllNearbyVendors(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("Category",true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e!=null){
                    Log.d(TAG,e.toString());
                    e.printStackTrace();
                    return;
                }
                list.clear();
                list.addAll(objects);
                Log.d(TAG,String.valueOf(list.size()));

                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses = null;
                List<Address> addressescurrentuser = null;
                String city_currentuser= null;

                ParseUser currentUser = ParseUser.getCurrentUser();

                try {
                    addressescurrentuser = geocoder.getFromLocation(currentUser.getDouble("Latitude"), currentUser.getDouble("Longitude"), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    city_currentuser = addressescurrentuser.get(0).getLocality();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }


                for (int i=0; i <list.size(); i++){
                    Log.d(TAG,list.get(i).getUsername());
                    try {
                        Log.d(TAG, "done: Lat:"+list.get(i).getDouble("Latitude"));
                        addresses = geocoder.getFromLocation(list.get(i).getDouble("Latitude"), list.get(i).getDouble("Longitude"), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                       if(city_currentuser.equals(addresses.get(0).getLocality())){
                           Log.d(TAG, "done: current : "+addresses.get(0).getLocality()+" user : "+city_currentuser);
                           moveCameraNearbyVendor(list.get(i));
                       }
                        ParseUser mUser = list.get(i);

                        LatLng coordinates = new LatLng(mUser.getDouble("Latitude"),mUser.getDouble("Longitude"));
                        ParseFile profileImgBitmap = mUser.getParseFile("ProfileImg");

                        putAllMarkersOnMap(mUser,profileImgBitmap,coordinates);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void putAllMarkersOnMap(ParseUser user,ParseFile img,LatLng coordinates){
        Context context = (Context) getActivity();
        String _name = "";

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
                        map.addMarker(options);

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("MapUser","Erreur marker img: "+e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("MAP_FETCH","Title "+marker.getTitle());
        Intent intent = new Intent(getActivity(), ProfileDetail.class);
        intent.putExtra("Title",marker.getTitle());
        startActivity(intent);
        return false;
    }
}
