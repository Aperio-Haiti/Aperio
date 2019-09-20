package com.example.aprio;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentMapSeller  extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "FragmentMapSeller";
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final float DEFAULT_ZOOM = 15f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void getLocationDevice(){
        Log.d(TAG,"getDeviceLocation: getting the devices current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Log.d(TAG,"onComplete : found location!");
                        Location currentLocation = (Location) task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);

                    }
                    else
                    {
                        Log.d(TAG,"onComplete : current location is null!");
                        Toast.makeText(getContext(),"unable to get current location",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (SecurityException e){
            Log.e(TAG,"getDeviceLocation: SecurityError "+e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng,float zoom){
        Log.d(TAG,"moveCamera : move the camera to : lat: "+latLng.latitude+",lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationDevice();
        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mMap.getUiSettings().isZoomControlsEnabled();

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng port_au_prince = new LatLng(18.547863, -72.325246);
        mMap.addMarker(new MarkerOptions().position(port_au_prince).title("Marker in port-au-prince"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(port_au_prince));

    }
}
