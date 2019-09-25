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
import butterknife.OnClick;

public class Negociation extends AppCompatActivity {

    /*private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    EditText etMessage;
    ImageButton btSend;
    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;*/

    // Keep track of initial load to scroll to the bottom of the ListView
    /*boolean mFirstLoad;
    public final static String USER_ID_KEY="user";
    public final static String BODY_KEY="body";*/

    // Create a handler which can run code periodically
    /*static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };*/
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

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negociation);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        /*// User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);*/
    }

    private void init() {
        String objectId = getIntent().getStringExtra("Product");
        ParseQuery<Product> query = new ParseQuery<>(Product.class);
        query.include("Vendor");
        query.include("Category");
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
                    }
                });
            }
        });
    }

    /*private void login() {
        ParseAnonymousUtils.logIn((user, e) -> {
            if (e != null) {
                Log.e("Erreur", "Anonymous login failed: ", e);
            } else {
                startWithCurrentUser();
            }
        });
    }*/


    /*void startWithCurrentUser() {
        setupMessagePosting();
    }*/
    // Setup message field and posting
    /*void setupMessagePosting() {
        // Find the text field and button
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        rvChat = findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(Negociation.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Negociation.this);
        rvChat.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(v -> {
            String data = etMessage.getText().toString();
            //ParseObject message = ParseObject.create("Message");
            //message.put(Message.USER_ID_KEY, userId);
            //message.put(Message.BODY_KEY, data);
            // Using new `Message` Parse-backed model now
            Message message = new Message();
            message.setBody(data);
            message.setUserId(ParseUser.getCurrentUser().getObjectId());
            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(Negociation.this, "Successfully created message on Parse",
                            Toast.LENGTH_SHORT).show();
                    refreshMessages();
                }
            });
            etMessage.setText(null);
        });
    }*/

    /*private void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground((messages, e) -> {
            if (e == null) {
                mMessages.clear();
                mMessages.addAll(messages);
                mAdapter.notifyDataSetChanged(); // update adapter
                // Scroll to the bottom of the list on initial load
                if (mFirstLoad) {
                    rvChat.scrollToPosition(0);
                    mFirstLoad = false;
                }
            } else {
                Log.e("message", "Error Loading Messages" + e);
            }
        });
    }*/
}
