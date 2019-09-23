package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Adapters.RecyclerviewAdapterProfile;
import com.google.android.material.circularreveal.cardview.CircularRevealCardView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetail extends AppCompatActivity {
    private RecyclerviewAdapterProfile rvAdapter;
    private RecyclerView recyclerView;

    @BindView(R.id.imgProfileDetail)
    CircleImageView imgProfile;

    @BindView(R.id.tvnameProfil)
    TextView tvNameProfile;

    @BindView(R.id.tvAddressProfil)
    TextView tvAddressProfile;

    private ArrayList<String> item;
    ParseUser selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profildetail);
        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        setTitle("");
        init();

        item=new ArrayList<>();
        item.add("1");
        item.add("2");
        item.add("4");
        item.add("5");
        item.add("7");
        recyclerView =findViewById(R.id.rvprofil);

        rvAdapter= new RecyclerviewAdapterProfile(this,item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        recyclerView.setAdapter(rvAdapter);
    }

    private void init() {
        String name = getIntent().getStringExtra("Title");

        if(!name.contentEquals("")){
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username",name);
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if(e!=null){
                        Log.d("ProfileDetails","Erreur : "+e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                    Log.d("ProfileDetails","Fetched : "+object.getDouble("Latitude"));

                    selectedUser = object;

                    setTitle(selectedUser.getUsername());
                    Glide.with(getApplicationContext())
                            .load(selectedUser.getParseFile("ProfileImg").getUrl())
                            .apply(new RequestOptions().error(R.drawable.error).placeholder(R.drawable.error))
                            .into(imgProfile);
                    tvNameProfile.setText(selectedUser.getUsername());
                    tvAddressProfile.setText(selectedUser.getString("Address"));
                }
            });
        }else{
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
