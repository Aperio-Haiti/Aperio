package com.example.aprio;


import android.Manifest;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class FragmentMapSeller extends Fragment implements OnMapReadyCallback {

    List<ParseUser> list;

    private static final String TAG = "FragmentMapSeller";
    @BindView(R.id.ic_magnify)
    ImageView icMagnify;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.ic_gps)
    ImageView icGps;

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
            map.getUiSettings().setZoomControlsEnabled(true);

            init();

//            getAllVendors();
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
//        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
//                    geoLocate();
//                }
//                return false;
//            }
//        });
        icGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: click gps icon");
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }

//    private void geoLocate() {
//        Log.d(TAG, "geoLocate: geoLocating");
//
//        String searchString = inputSearch.getText().toString();
//
//        Geocoder geocoder = new Geocoder(getContext());
//        List<Address> list = new ArrayList<>();
//
//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        } catch (IOException e) {
//            Log.d(TAG, "geoLocate: IOException " + e.getMessage());
//        }
//
//        if (list.size() > 0) {
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location " + address.toString());
//
//            moveCameraToMyLocationDevice(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
//        }
//    }

    private void hideSoftKeyboard() {
        Window window = getActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void moveCameraVendor(ParseUser user) {
        LatLng coordinates = new LatLng(user.getDouble("Latitude"),user.getDouble("Longitude"));
        Log.d(TAG, "moveCamera : move the camera to : lat: " + user.getDouble("Latitude") + ",lng: " + user.getDouble("Longitude"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM));
        if(user.getParseFile("ProfileImg")!=null) {
            File profileImg = null;

            try {
                profileImg = user.getParseFile("ProfileImg").getFile();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Bitmap profileImgBitmap = BitmapFactory.decodeFile(profileImg.getAbsolutePath());

            profileImgBitmap = scaleBitmap(profileImgBitmap, 100, 100);

            if (!user.getUsername().equals("my Location")) {

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(coordinates)
                        .title(user.getUsername())
                        .icon(BitmapDescriptorFactory.fromBitmap(profileImgBitmap));
                map.addMarker(markerOptions);
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

    private void moveCameraNearbyVendor(ParseUser user) {
        LatLng coordinates = new LatLng(user.getDouble("Latitude"),user.getDouble("Longitude"));
        Log.d(TAG, "moveCamera : move the camera to : lat: " + user.getDouble("Latitude") + ",lng: " + user.getDouble("Longitude"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM));
        if(user.getParseFile("ProfileImg")!=null) {
            File profileImg = null;

            try {
                profileImg = user.getParseFile("ProfileImg").getFile();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Bitmap profileImgBitmap = BitmapFactory.decodeFile(profileImg.getAbsolutePath());

            profileImgBitmap = scaleBitmap(profileImgBitmap, 100, 100);

            if (!user.getUsername().equals("my Location")) {

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(coordinates)
                        .title(user.getUsername())
                        .icon(BitmapDescriptorFactory.fromBitmap(profileImgBitmap));
                map.addMarker(markerOptions);
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

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

//    init();
//        // Add a marker in Sydney, Australia, and move the camera.
//        getAllVendors();
//    }
//
    public void getAllVendors(){
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

                for (int i=0; i <list.size(); i++){
                    Log.d(TAG,list.get(i).getUsername());
//                    moveCamera(new LatLng(list.get(i).));
                    moveCameraVendor(list.get(i));
                }
            }
        });
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
//
                    city_currentuser = addressescurrentuser.get(0).getLocality();
                }
                catch (IOException ex) {
                    e.printStackTrace();
                }


                for (int i=0; i <list.size(); i++){
                    Log.d(TAG,list.get(i).getUsername());
                    try {
                        Log.d(TAG, "done: Lat:"+list.get(i).getDouble("Latitude"));
                        addresses = geocoder.getFromLocation(list.get(i).getDouble("Latitude"), list.get(i).getDouble("Longitude"), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                        addresses = geocoder.getFromLocation(user.getDouble("Latitude"), user.getDouble("Longitude"), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                       if(city_currentuser.equals(addresses.get(0).getLocality())){
                           Log.d(TAG, "done: current : "+addresses.get(0).getLocality()+" user : "+city_currentuser);
                           moveCameraNearbyVendor(list.get(i));
                       }

//                    if(currentUser.)

                    } catch (IOException ex) {
                        e.printStackTrace();
                    }
//                    moveCamera(new LatLng(list.get(i).));

                }
            }
        });
    }

}
