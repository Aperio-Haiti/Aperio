package com.example.aprio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

    @BindView(R.id.ic_magnify)
    ImageView icMagnify;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.ic_gps)
    ImageView icGps;
    private GoogleMap mMap;

    List<ParseUser> list;

    private static final String TAG = "FragmentMapSeller";
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final float DEFAULT_ZOOM = 8;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void init() {
        Log.d(TAG, "init: initializing");
        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });
        icGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: click gps icon");
                getLocationDevice();
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geoLocating");

        String searchString = inputSearch.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOException " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void hideSoftKeyboard() {
        Window window = getActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void getLocationDevice() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete : found location!");
                        Location currentLocation = (Location) task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "my Location");

                    } else {
                        Log.d(TAG, "onComplete : current location is null!");
                        Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityError " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera : move the camera to : lat: " + latLng.latitude + ",lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("my Location")) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);

            mMap.addMarker(markerOptions);
        }

        hideSoftKeyboard();
    }

    private void moveCameraVendor(ParseUser user) {
        LatLng coordinates = new LatLng(user.getDouble("Latitude"),user.getDouble("Longitude"));
        Log.d(TAG, "moveCamera : move the camera to : lat: " + user.getDouble("Latitude") + ",lng: " + user.getDouble("Longitude"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM));
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


                mMap.addMarker(markerOptions);
            }
        }
        else {
            if (!user.getUsername().equals("my Location")) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(coordinates)
                        .title(user.getUsername())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_personne));
                mMap.addMarker(markerOptions);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationDevice();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        init();
        // Add a marker in Sydney, Australia, and move the camera.
        getAllVendors();
    }

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
}
