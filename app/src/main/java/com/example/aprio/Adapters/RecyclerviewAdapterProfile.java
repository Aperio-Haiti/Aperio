package com.example.aprio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Models.Product;
import com.example.aprio.ProductDetail;
import com.example.aprio.R;

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
        });
        //Bookmark
        holder.ivBookmark.setOnClickListener(view -> {
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
