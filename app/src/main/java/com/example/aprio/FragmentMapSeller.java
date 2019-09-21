package com.example.aprio;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.BindView;


public class FragmentMapSeller extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.ic_magnify)
    ImageView icMagnify;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.ic_gps)
    ImageView icGps;

    GoogleMap map;

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private  boolean mLocationPermissionGranted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfragment, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    private void getLocationPermission(){
        String[] permissions = {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                                };

        if((ContextCompat.checkSelfPermission(getActivity(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(getActivity(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
        {
                mLocationPermissionGranted = true;
        }
        else{
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
