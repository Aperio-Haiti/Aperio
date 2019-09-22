package com.example.aprio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentProductsSeller extends Fragment {
    @BindView(R.id.rvproduct)
    RecyclerView rvproduct;
    @BindView(R.id.fab)
    FloatingActionButton fab;
//    private RecyclerviewAdapter rvAdapter;
//    private RecyclerView recyclerView;
//    private ArrayList<String> item;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.activitymyproductfragment, container, false);
        View view = inflater.inflate(R.layout.activitymyproductfragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),addproduct.class));
            }
        });
//        item = new ArrayList<>();
//        item.add("1");
//        item.add("2");
//        item.add("4");
//        item.add("5");
//        item.add("7");
//        recyclerView = view.findViewById(R.id.rvproduct);
//
//        rvAdapter = new RecyclerviewAdapter(getActivity(), item);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
//
//        recyclerView.setAdapter(rvAdapter);
    }
}

