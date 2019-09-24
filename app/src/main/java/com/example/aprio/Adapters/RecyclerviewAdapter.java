package com.example.aprio.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aprio.GlideApp;
import com.example.aprio.Models.Product;
import com.example.aprio.R;
import com.parse.ParseException;

import java.io.File;
import java.util.List;


public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHOlder> {

    private Context context;
    private List<Product> data;

   public RecyclerviewAdapter(Context context, List<Product> data) {
        this.context = context;
        this.data=data;
    }

    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_mpview,parent,false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHOlder holder, int position) {
        Product p = data.get(position);
        holder.bind(p);
//        String title=data.get(position);

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

        public void bind(Product p) {
            name.setText(p.get_Description());
            Log.d("Category", "bind: "+p.get_Category().getString(p.KEY_CATEGORY));
            category.setText(p.get_Category().getString(p.KEY_CATEGORY));
            File photoFile = null;
            try {
                photoFile = p.get_Image_Product().getFile();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            GlideApp.with(context)
                    .load(takenImage)
                    .into(imgpost);
        }
    }

    // Clean all elements of the recycler
    public void clear() {

        data.clear();

        notifyDataSetChanged();

    }

    // Add a list of items -- change to type used

    public void addProduct(List<Product> list) {

        data.addAll(list);

        notifyDataSetChanged();

    }
}
