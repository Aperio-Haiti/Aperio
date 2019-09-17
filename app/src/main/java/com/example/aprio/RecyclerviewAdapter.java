package com.example.aprio;

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


public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHOlder> {

    private Context context;




    public RecyclerviewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_mpview,parent,false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHOlder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    //Define the viewholder
    public class ViewHOlder extends RecyclerView.ViewHolder{

        public ImageView imgpost;
        public TextView name,category;


        ViewHOlder(@NonNull View itemView) {
            super(itemView);
            imgpost=itemView.findViewById(R.id.imgMyproduct);
            name=itemView.findViewById(R.id.tvMyproductname);
            category=itemView.findViewById(R.id.tvMyproductcategory);
        }
    }
}
