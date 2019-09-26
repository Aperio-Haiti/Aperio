package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Adapters.ChatAdapter;
import com.example.aprio.Models.Category;
import com.example.aprio.Models.Message;
import com.example.aprio.Models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.GetCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Negociation extends AppCompatActivity {

    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    @BindView(R.id.toolbarNegociation)
    Toolbar toolbar;
    @BindView(R.id.imgSelectedProductImg)
    ImageView ivProductImg;
    @BindView(R.id.tvSelectedProductCategory)
    TextView tvCategory;
    @BindView(R.id.tvSelectedProductDescription)
    TextView tvDescription;
    @BindView(R.id.tvSelectedProductVendor)
    TextView tvVendor;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.fabSend)
    FloatingActionButton fabSend;
    @BindView(R.id.rvChat)
    RecyclerView rvChat;

    Product product;
    ChatAdapter adapter;
    ArrayList<Message> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negociation);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        messageArrayList = new ArrayList<>();
    }

    private void init() {
        String objectId = getIntent().getStringExtra("Product");
        ParseQuery<Product> query = new ParseQuery<>(Product.class);
        query.include(Product.KEY_USER);
        query.include(Product.KEY_CATEGORY);
        query.getInBackground(objectId, new GetCallback<Product>() {
            @Override
            public void done(Product object, ParseException e) {
                if(e!=null){
                    Log.d("Negociation","Erreur :"+e.getMessage());
                    e.printStackTrace();
                    return;
                }

                product = object;
                Glide.with(Negociation.this)
                        .load(product.get_Image_Product().getUrl())
                        .apply(new RequestOptions().centerCrop().error(R.drawable.error))
                        .into(ivProductImg);
                Category category = (Category) product.get_Category();
                ParseUser vendor = product.get_User();
                tvCategory.setText(category.getCategory());
                tvDescription.setText(product.get_Description());
                tvVendor.setText(vendor.getUsername());

                adapter = new ChatAdapter(Negociation.this,messageArrayList);
                rvChat.setLayoutManager(new LinearLayoutManager(Negociation.this,RecyclerView.VERTICAL,false));
                rvChat.setAdapter(adapter);

                refreshMessages();
            }
        });

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etMess = etMessage.getText().toString();
                if(etMess.isEmpty()){
                    Snackbar.make(view,"Please write message.",Snackbar.LENGTH_LONG).show();
                    return;
                }
                //todo:Send message
                Message message = new Message();
                message.setUser(ParseUser.getCurrentUser());
                message.setVendor(product.get_User());
                message.setProduct(product);
                message.setMessage(etMess);
                message.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d("Negociation","Erreur :"+e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        //todo:refresh RecyclerView
                        refreshMessages();
                    }
                });
            }
        });
    }

    private void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.include(Message.KEY_PRODUCT);
        query.include(Message.KEY_USER);
        query.include(Message.KEY_VENDOR);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByAscending("createdAt");
        query.whereEqualTo(Message.KEY_VENDOR,product.get_User());
        query.whereEqualTo(Message.KEY_USER,ParseUser.getCurrentUser());
        query.whereEqualTo(Message.KEY_PRODUCT,product);
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground((messages, e) -> {
            if (e == null) {
                messageArrayList.clear();
                messageArrayList.addAll(messages);
                adapter.notifyDataSetChanged(); // update adapter
            } else {
                Log.e("message", "Error Loading Messages" + e);
            }
        });
    }
}
