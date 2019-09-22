package com.example.aprio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerviewAdapterProfile extends RecyclerView.Adapter<RecyclerviewAdapterProfile.ViewHOlder> {

    private Context context;
    private List<String> data;




    RecyclerviewAdapterProfile(Context context, List<String> data) {
        this.context = context;
        this.data=data;
    }

    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_profile,parent,false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHOlder holder, int position) {
String title=data.get(position);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //Define the viewholder
    class ViewHOlder extends RecyclerView.ViewHolder{

        ImageView imgpost;
        TextView name,category;


        ViewHOlder(@NonNull View itemView) {
            super(itemView);
            imgpost=itemView.findViewById(R.id.imgMyproduct);
            name=itemView.findViewById(R.id.tvMyproductname);
            category=itemView.findViewById(R.id.tvMyproductcategory);
        }
    }
}
