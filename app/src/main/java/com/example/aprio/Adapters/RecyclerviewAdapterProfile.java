package com.example.aprio.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.aprio.Models.Conversation;
import com.example.aprio.Models.Favorites;
import com.example.aprio.Models.Product;
import com.example.aprio.Negociation;
import com.example.aprio.ProductDetail;
import com.example.aprio.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class  RecyclerviewAdapterProfile extends RecyclerView.Adapter<RecyclerviewAdapterProfile.mViewHolder> {

    private Context context;
    private List<Product> myProducts;

    public RecyclerviewAdapterProfile(Context context, List<Product> myProducts) {
        this.context = context;
        this.myProducts = myProducts;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_profile, parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Product product = myProducts.get(position);
        //Imageview
        Glide.with(context).load(product.get_Image_Product().getUrl())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.error).placeholder(R.drawable.error))
                .into(holder.imgPoster);
        holder.imgPoster.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("Product",product.getObjectId());
            context.startActivity(intent);
        });
        //Contact Seller
        holder.tvContact.setOnClickListener(view -> {
            ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
            query.whereEqualTo(Conversation.KEY_VENDOR,product.get_User());
            query.whereEqualTo(Conversation.KEY_USER, ParseUser.getCurrentUser());
            query.whereEqualTo(Conversation.KEY_PRODUCT,product);
            query.findInBackground((objects, e) -> {
                if(e!=null){
                    Log.d("ProductDetail","Erreur :"+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                if(objects.size()!=0){
                    Intent intent = new Intent(context, Negociation.class);
                    intent.putExtra("Product",product.getObjectId());
                    intent.putExtra("Convo",objects.get(0).getObjectId());
                    Log.d("ProductDetail","Id: "+objects.get(0).getObjectId());
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context,Negociation.class);
                    intent.putExtra("Product",product.getObjectId());
                    intent.putExtra("Convo","");
                    Log.d("ProductDetail","Id: nothing");
                    context.startActivity(intent);
                }
            });
        });
        //Bookmark
        holder.ivBookmark.setOnClickListener(view -> {
            ParseQuery<Favorites> query = ParseQuery.getQuery(Favorites.class);
            query.whereEqualTo(Favorites.KEY_USER, ParseUser.getCurrentUser());
            query.whereEqualTo(Favorites.KEY_PRODUCT,product);
            query.findInBackground(new FindCallback<Favorites>() {
                @Override
                public void done(List<Favorites> objects, ParseException e) {
                    if (e!=null){
                        Log.d("Adapter","Erreur:"+e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                    if(objects.size()!=0){
                        //we already save this one!
                        Snackbar.make(view,"This product has been already saved in favorites!",Snackbar.LENGTH_SHORT).show();
                    }else {
                        Favorites favorites = new Favorites();
                        favorites.setUser(ParseUser.getCurrentUser());
                        favorites.setProduct(product);
                        favorites.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e!=null){
                                    Log.d("Adapter","Erreur:"+e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }
                                Snackbar.make(view,"This product is saved in favorites!",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return myProducts.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{

        ImageView imgPoster;
        TextView tvContact;
        ImageView ivBookmark;

        mViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgMyproduct);
            tvContact = itemView.findViewById(R.id.tvMyproductname);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
        }
    }
}
