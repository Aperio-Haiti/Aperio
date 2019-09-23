package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.aprio.Adapters.RecyclerviewAdapterProfile;

import java.util.ArrayList;

public class ProfileDetail extends AppCompatActivity {
    private RecyclerviewAdapterProfile rvAdapter;
    private RecyclerView recyclerView;
    private ArrayList<String> item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profildetail);

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
}
