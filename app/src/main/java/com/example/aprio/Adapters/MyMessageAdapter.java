package com.example.aprio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aprio.Models.Conversation;
import com.example.aprio.Models.Message;
import com.example.aprio.Models.Product;
import com.example.aprio.Negociation;
import com.example.aprio.R;
import com.parse.ParseUser;

import java.util.List;

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.mViewHolder> {
    
    private Context context;
    private List<Conversation> list;
    public static final int USER_SENDER = 1;
    public static final int VENDOR_SENDER = 0;

    public MyMessageAdapter(Context context, List<Conversation> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_list_item,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Conversation conversation = list.get(position);

        if(conversation.getUser().getObjectId().contentEquals(ParseUser.getCurrentUser().getObjectId())){
            //the current user is the message user
            //we want to show the vendor image and name
            ParseUser user = conversation.getVendor();
            Product product = (Product) conversation.getProduct();
            String productName = product.get_Description();
            Glide.with(context)
                    .load(user.getParseFile("ProfileImg").getUrl())
                    .into(holder.imageView);
            holder.textViewName.setText(user.getUsername());
            holder.textViewProductname.setText(productName);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Negociation.class);
                    intent.putExtra("Product",product.getObjectId());
                    intent.putExtra("Convo",conversation.getObjectId());
                    context.startActivity(intent);
                }
            });
        }else{
            //the current user is the message vendor
            //we want to show the user image, and name
            ParseUser user = conversation.getUser();
            Product product = (Product) conversation.getProduct();
            String productName = product.get_Description();
            Glide.with(context)
                    .load(user.getParseFile("ProfileImg").getUrl())
                    .into(holder.imageView);
            holder.textViewName.setText(user.getUsername());
            holder.textViewProductname.setText(productName);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Negociation.class);
                    intent.putExtra("Product",product.getObjectId());
                    intent.putExtra("Convo",conversation.getObjectId());
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void AddAllToList(List<Conversation> mList){
        list.clear();
        list.addAll(mList);
        notifyDataSetChanged();
    }

    public class mViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewName;
        public TextView textViewProductname;
        public RelativeLayout relativeLayout;
        
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivMessageItemImg);
            textViewName = itemView.findViewById(R.id.tvMessageItemName);
            textViewProductname = itemView.findViewById(R.id.tvMessageItemProduct);
            relativeLayout = itemView.findViewById(R.id.layMessageItem);
        }
    }
}
