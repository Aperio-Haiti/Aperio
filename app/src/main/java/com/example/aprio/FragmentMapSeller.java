package com.example.aprio;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

    private static final String TAG = "FragmentMapSeller";
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final float DEFAULT_ZOOM = 15f;

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationDevice();
        mMap.setMyLocationEnabled(true);

        init();
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mMap.getUiSettings().isZoomControlsEnabled();

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng Depot_Christ_Roi = new LatLng(18.546174, -72.322111);
        mMap.addMarker(new MarkerOptions().position(Depot_Christ_Roi).title("Christ-Roi Dépôt"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Depot_Christ_Roi));

        LatLng Union_Baptiste = new LatLng(18.548707, -72.320770);
        mMap.addMarker(new MarkerOptions().position(Union_Baptiste).title("Union Baptiste"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Union_Baptiste));

        LatLng CentreSportifdeCarrefour = new LatLng(18.536234, -72.406154);
        mMap.addMarker(new MarkerOptions().position(CentreSportifdeCarrefour).title("Centre Sportif de Carrefour"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CentreSportifdeCarrefour));
    }

    private void getAllVendors(){

    }
}
