package com.example.aprio.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Models.Favorites;
import com.example.aprio.Models.Message;
import com.example.aprio.Models.Product;
import com.example.aprio.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.time.LocalDate;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.mViewholder> {
    Context context;
    List<Favorites> favoritesList;

    public FavoriteAdapter(Context context, List<Favorites> list) {
        this.context = context;
        this.favoritesList = list;
    }

    @NonNull
    @Override
    public mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item,parent,false);
        return new mViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewholder holder, int position) {
        Favorites favorites = favoritesList.get(position);

        Product product = (Product) favorites.getProduct();

        Glide.with(context)
                .load(product.get_Image_Product().getUrl())
                .apply(new RequestOptions().error(R.drawable.error))
                .into(holder.ivProdImg);

        holder.tvProdName.setText(product.get_Description());

        try {
            holder.tvProdVendor.setText(product.get_User().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.fabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorites.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null){
                            Log.d("FavoriteAdapter","Erreur delete: "+e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        favoritesList.remove(position);
                        notifyDataSetChanged();
                        Snackbar.make(view,"Product successfuly deleted!",Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void AddAllToList(List<Favorites> list){
        favoritesList.clear();
        favoritesList.addAll(list);
        notifyDataSetChanged();
    }

    public class mViewholder extends RecyclerView.ViewHolder {

        ImageView ivProdImg;
        TextView tvProdName;
        TextView tvProdVendor;
        FloatingActionButton fabRemove;

        public mViewholder(@NonNull View itemView) {
            super(itemView);
            ivProdImg = itemView.findViewById(R.id.ivFavProdImg);
            tvProdName = itemView.findViewById(R.id.tvFavProdName);
            tvProdVendor = itemView.findViewById(R.id.tvFavProdVendor);
            fabRemove = itemView.findViewById(R.id.fabFavProdClose);
        }
    }
}
