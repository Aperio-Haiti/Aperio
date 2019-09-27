package com.example.aprio.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Models.Message;
import com.example.aprio.R;
import com.parse.ParseUser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import static android.view.Gravity.LEFT;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MY_MESSAGE = 1;
    private static final int THEIR_MESSAGE = 0;
    private List<Message> messageList;
    private Context mContext;

    public  ChatAdapter(Context context, List<Message> messages) {
        messageList = messages;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getUser().getObjectId().contentEquals(ParseUser.getCurrentUser().getObjectId())){
            return MY_MESSAGE;
        }else {
            return THEIR_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        switch (viewType){
            case MY_MESSAGE:
                View view = layoutInflater.inflate(R.layout.item_message,parent,false);
                viewHolder = new meViewHolder(view);
                break;
            case THEIR_MESSAGE:
                View view1 = layoutInflater.inflate(R.layout.item_message_other,parent,false);
                viewHolder = new otherViewHolder(view1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case MY_MESSAGE:
                meViewHolder viewHolder = (meViewHolder) holder;
                bindMyMessages(viewHolder,position);
                break;
            case THEIR_MESSAGE:
                otherViewHolder viewHolder1 = (otherViewHolder) holder;
                bindTheirMessages(viewHolder1,position);
                break;
        }
    }

    public void AddAllToList(List<Message> list){
        messageList.clear();
        messageList.addAll(list);
        notifyDataSetChanged();
    }

    private void bindTheirMessages(otherViewHolder holder, int position) {
        Message message = messageList.get(position);
        Glide.with(mContext)
                .load(message.getVendor().getParseFile("ProfileImg").getUrl())
                .apply(new RequestOptions().error(R.drawable.error).override(50,50))
                .into(holder.ivOtherAvatar);
        holder.tvMessage.setText(message.getMessage());
    }

    private void bindMyMessages(meViewHolder holder, int position) {
        Message message = messageList.get(position);
        Glide.with(mContext)
                .load(message.getUser().getParseFile("ProfileImg").getUrl())
                .apply(new RequestOptions().error(R.drawable.error).override(50,50))
                .into(holder.ivMyAvatar);
        holder.tvMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class otherViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivOtherAvatar;
        public TextView tvMessage;

        public otherViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOtherAvatar = itemView.findViewById(R.id.ivVendorAvatar);
            tvMessage = itemView.findViewById(R.id.tvMRText);
        }
    }

    public class meViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivMyAvatar;
        public TextView tvMessage;

        public meViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageText);
            ivMyAvatar = itemView.findViewById(R.id.ivUserAvatar);
        }
    }
}
