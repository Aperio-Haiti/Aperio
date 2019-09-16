package com.example.aprio;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsUserActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<ParseUser> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_user);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static Bitmap createCustomMarker(Context context, ParseFile img, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        Glide.with(context).load(img).apply(new RequestOptions().placeholder(R.drawable.avatar).error(R.drawable.avatar)).into(markerImage);
        markerImage.setBorderWidth(3);
        markerImage.setBorderColor(ContextCompat.getColor(context, R.color.colorPrimary));



        TextView txt_name = (TextView)marker.findViewById(R.id.name);
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void getAllVendors(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
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
        LatLng coordinates = new LatLng(user.getDouble("Latitude"),user.getDouble("Longitude"));

        MarkerOptions options = new MarkerOptions();
        options.position(coordinates)
                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsUserActivity.this,user.getParseFile("ProfileImg"),user.getUsername())));
        mMap.addMarker(options);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        list = new ArrayList<>();
        getAllVendors();

        //to remove the compass under the toolbar
        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(false);

        /*Test coordinates
        * 18.534275, -72.323027
        * 18.533444,-72.324748
        * 18.534899,-72.325964
        * */

        //sydney marker
        LatLng sydney = new LatLng(18.533817,-72.324272);

        /*create marker
        MarkerOptions options = new MarkerOptions();
        options.position(sydney)
                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsUserActivity.this,R.drawable.avatar,"Sydney")))
                .title("Marker in Sydney");
        mMap.addMarker(options);*/

        //Option camera to focus in target
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east// Sets the tilt of the camera to 30 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
