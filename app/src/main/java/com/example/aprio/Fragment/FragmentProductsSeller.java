package com.example.aprio.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.aprio.Adapters.RecyclerviewAdapter;
import com.example.aprio.Models.Product;
import com.example.aprio.R;
import com.example.aprio.AddProduct;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentProductsSeller extends Fragment {
    @BindView(R.id.rvproduct)
    RecyclerView rvproduct;
    @BindView(R.id.fab)
    FloatingActionButton fab;

//    @BindView(R.id.swipeContainer)
//    SwipeRefreshLayout swipeContainer;

    //ok
    private RecyclerviewAdapter rvAdapter;
//    private RecyclerView recyclerView;
    private List<Product> item;
    Unbinder unbinder ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.activity_myproductfragment, container, false);
        View view = inflater.inflate(R.layout.activity_myproductfragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddProduct.class));
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

//
        item = new ArrayList<>();
//        item.add("1");
//        item.add("2");
//        item.add("4");
//        item.add("5");
//        item.add("7");
//        recyclerView = view.findViewById(R.id.rvproduct);
//
        rvAdapter = new RecyclerviewAdapter(getContext(), item);
        rvproduct.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
//
        rvproduct.setAdapter(rvAdapter);
            queryProduct();

//            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                    android.R.color.holo_green_light,
//                    android.R.color.holo_orange_light,
//                    android.R.color.holo_red_light);
//
//            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    queryProduct();
//
//                }
//            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder .unbind();
    }

    protected void queryProduct() {
        ParseQuery<Product> postQuery = ParseQuery.getQuery(Product.class);
        postQuery.include(Product.KEY_USER);
        postQuery.include(Product.KEY_CATEGORY);
        postQuery.whereEqualTo(Product.KEY_USER, ParseUser.getCurrentUser());

        postQuery.findInBackground(new FindCallback<Product>() {
            @Override
            public void done(List<Product> objects, ParseException e) {
                List<Product>  ProductToAdd = new ArrayList<>();
                if (e == null) {

                    for (int i = 0; i < objects.size(); i++) {
                        Product p = objects.get(i);
                        ProductToAdd.add(p);
                    }
//                    posts.addAll(objects);
//                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("Error", "Error : " + e.getMessage());
                    e.printStackTrace();
                    return;
                }

                rvAdapter.clear();
                rvAdapter.addProduct(ProductToAdd);
//                swipeContainer.setRefreshing(false);
            }
        });
    }

}

