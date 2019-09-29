package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.aprio.Adapters.FavoriteAdapter;
import com.example.aprio.Models.Favorites;
import com.example.aprio.Models.Product;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewFavorites extends AppCompatActivity {

    @BindView(R.id.toolbarFavorites)
    Toolbar toolbar;
    @BindView(R.id.rvFavorites)
    RecyclerView rvFavorites;
    List<Favorites> favoritesList;
    FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorites);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Favorites");

        favoritesList = new ArrayList<>();
        adapter = new FavoriteAdapter(this,favoritesList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        rvFavorites.setAdapter(adapter);


        ParseQuery<Favorites> query= ParseQuery.getQuery(Favorites.class);
        query.include(Favorites.KEY_PRODUCT);
        query.whereEqualTo(Favorites.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Favorites>() {
            @Override
            public void done(List<Favorites> objects, ParseException e) {
                if(e!=null){
                    Log.d("ViewFavorites","Erreur : "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                Log.d("ViewFavorites","Favorites fetched with "+objects.size()+" row(s)");
                adapter.AddAllToList(objects);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
