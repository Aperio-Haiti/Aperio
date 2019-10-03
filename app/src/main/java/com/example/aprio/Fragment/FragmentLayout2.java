package com.example.aprio.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aprio.MapSellerActivity;
import com.example.aprio.R;
import com.example.aprio.SignUser;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentLayout2 extends Fragment {
    @BindView(R.id.etAddressSignupSeller)
    EditText etAddressSignupSeller;
    @BindView(R.id.etCitySignupSeller)
    EditText etCitySignupSeller;
    @BindView(R.id.etStateSignupSeller)
    EditText etStateSignupSeller;
    @BindView(R.id.etCodeSignupSeller)
    EditText etCodeSignupSeller;
    @BindView(R.id.btnNextTwo)
    Button btnNextTwo;

    private static final String TAG = "FragmentLayout2";

    private String username;
    private String password;
    private String email;
    private String phone;

    private Double Lng;
    private Double Lat;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signlayout2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void updateEditText(CharSequence username, CharSequence password, CharSequence email, CharSequence phone) {
        Log.d("username", username.toString());
        this.username = username.toString();
        this.password = password.toString();
        this.email = email.toString();
        this.phone = phone.toString();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geoLocating");

        String searchString = etAddressSignupSeller.getText().toString()+","
                +etCitySignupSeller.getText().toString()+","
                +etStateSignupSeller.getText().toString();

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

            Lat = address.getLatitude();
            Lng = address.getLongitude();
        }
    }

    private byte[] DrawableToByteArray(int avatar) {
        Drawable d = getResources().getDrawable(avatar);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public void Sign_vendor(String username, String password, String email, String phone) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email",email);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                    return;
                }
                if(count!=0){
                    Toast.makeText(getActivity(),"This email already bound to an account.\n Please choose another email.",Toast.LENGTH_LONG).show();
                }else{
                    ParseUser user = new ParseUser();
                    String address = etAddressSignupSeller.getText().toString().trim();
                    geoLocate();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.put("Address", address);
                    user.put("Category", true);
                    user.put("Phone",Integer.valueOf(phone));
                    user.put("Latitude", Lat);
                    user.put("Longitude",Lng);

                    ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setTitle("Creating account...");
                    pd.setMessage("Please wait.");
                    pd.setCancelable(false);
                    pd.show();

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseUser user1 = ParseUser.getCurrentUser();
                                user1.put("ProfileImg",new ParseFile("avatar.jpg",DrawableToByteArray(R.drawable.avatar)));
                                user1.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        pd.dismiss();
                                        if(e!=null){

                                            e.printStackTrace();
                                            return;
                                        }
                                        Intent i = new Intent(getContext(), MapSellerActivity.class);
                                        startActivity(i);
                                        getActivity().finish();
                                    }
                                });

                            } else {
                                pd.dismiss();
                                ParseUser.logOut();
                                Log.e(TAG,e.getMessage());
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       /* Toolbar toolbar = view.findViewById(R.id.tb_fragmen2);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    @OnClick(R.id.btnNextTwo)
    public void onViewClicked() {
//        String address = etAddressSignupSeller.getText().toString().trim();
        Sign_vendor(username, password, email, phone);
    }
}
