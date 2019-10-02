package com.example.aprio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Models.Message;
import com.example.aprio.ProfileDetail;
import com.example.aprio.R;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.mViewholder> {

    Context context;
    List<ParseUser> userList;
    String searchType = "";

    public static final int SELLER_SEARCH = 0;
    public static final int CATEGORY_SEARCH = 1;
    public static final int REGION_SEARCH = 2;

    public SearchAdapter(Context context, List<ParseUser> list){
        //Constructor for user search
        this.context = context;
        this.userList = list;
        this.searchType = "seller";
    }

    @NonNull
    @Override
    public mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
        return new mViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewholder holder, int position) {
        if (searchType.contentEquals("seller")){

            ParseUser user = userList.get(position);
            Glide.with(context)
                    .load(user.getParseFile("ProfileImg").getUrl())
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.avatar).error(R.drawable.error))
                    .into(holder.imageView);
            holder.textView.setText(user.getUsername());
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileDetail.class);
                    intent.putExtra("Title",user.getUsername());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void clear(){
        userList.clear();
        notifyDataSetChanged();
    }

    public void AddAllToList(List<ParseUser> list){
        userList.clear();
        userList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        switch (searchType){
            case "seller":
                return userList.size();
            default:
                return 0;
        }
    }

    public class mViewholder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textView;
        RelativeLayout relativeLayout;
        public mViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSearchResultImg);
            textView = itemView.findViewById(R.id.tvSearchResultText);
            relativeLayout = itemView.findViewById(R.id.rlSearchResult);
        }
    }
}
