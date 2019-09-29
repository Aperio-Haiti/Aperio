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
import com.example.aprio.Models.Conversation;
import com.example.aprio.Models.Message;
import com.example.aprio.Models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
    String convo;
    ParseUser sender;
    ParseUser reciever;
    Conversation conversation;
    Boolean hasConvo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negociation);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        convo = getIntent().getStringExtra("Convo");

        init();
        messageArrayList = new ArrayList<>();
    }

    private void checkConversationEntities(Product product){
        if(!convo.contentEquals("")){
            ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
            query.include(Conversation.KEY_USER);
            query.include(Conversation.KEY_VENDOR);
            query.whereEqualTo(Conversation.KEY_OBJECT_ID,convo);
            query.getFirstInBackground(new GetCallback<Conversation>() {
                @Override
                public void done(Conversation object, ParseException e) {
                    if(e!=null){
                        Log.d("Negociation","Erreur :"+e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                    conversation = object;
                    if(ParseUser.getCurrentUser().getObjectId().contentEquals(object.getVendor().getObjectId())){
                        sender = object.getVendor();
                        reciever = object.getUser();
                        hasConvo = true;
                        refreshMessages();
                    }else{
                        sender = object.getUser();
                        reciever = object.getVendor();
                        hasConvo = true;
                        refreshMessages();
                    }
                    Log.d("Negociation","Sender is: "+sender.getObjectId()+" "+", Reciever is: "+reciever.getObjectId());

                }
            });
        }else{
            //conversation = new Conversation();
            sender = ParseUser.getCurrentUser();
            reciever = product.get_User();
            hasConvo = false;
            Log.d("Negociation","Sender is: "+sender.getObjectId()+" "+", Reciever is: "+reciever.getObjectId());
        }
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

                checkConversationEntities(product);
            }
        });

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etMess = etMessage.getText().toString();
                if(etMess.contentEquals("")){
                    Snackbar.make(view,"Please write message.",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(hasConvo){
                    Log.d("Negociation","Has Convo");
                    Log.d("Negociation","Convo ID: "+conversation.getObjectId());
                    Message message = new Message();
                    message.setUser(sender);
                    message.setVendor(reciever);
                    message.setProduct(product);
                    message.setMessage(etMess);
                    message.setConversation(conversation);
                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e!=null){
                                Log.d("Negociation","Erreur :"+e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                            etMessage.setText("");
                            refreshMessages();
                        }
                    });
                }else{
                    Log.d("Negociation","Has no Convo");
                    Conversation conversation1 = new Conversation();
                    conversation1.setProduct(product);
                    conversation1.setUser(sender);
                    conversation1.setVendor(reciever);
                    conversation1.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ParseQuery<Conversation> queryCount = ParseQuery.getQuery(Conversation.class);
                            queryCount.whereEqualTo(Conversation.KEY_USER,sender);
                            queryCount.whereEqualTo(Conversation.KEY_VENDOR,reciever);
                            queryCount.whereEqualTo(Conversation.KEY_PRODUCT,product);
                            queryCount.getFirstInBackground(new GetCallback<Conversation>() {
                                @Override
                                public void done(Conversation object, ParseException e) {
                                    if(e!=null){
                                        Log.d("Negociation","Erreur: "+e.getMessage());
                                        e.printStackTrace();
                                        return;
                                    }
                                    conversation = object;
                                    hasConvo = true;
                                    Log.d("Negociation","Has Convo: Convo Fetched!");
                                    Message message = new Message();
                                    message.setUser(sender);
                                    message.setVendor(reciever);
                                    message.setProduct(product);
                                    message.setMessage(etMess);
                                    message.setConversation(conversation);
                                    message.saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e!=null){
                                                Log.d("Negociation","Erreur :"+e.getMessage());
                                                e.printStackTrace();
                                                return;
                                            }
                                            Log.d("Negociation","Message saved!");
                                            etMessage.setText("");
                                            refreshMessages();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    private void refreshMessages(){

        /*ParseQuery<Conversation> queryCount = ParseQuery.getQuery(Conversation.class);
        queryCount.whereEqualTo(Conversation.KEY_USER,ParseUser.getCurrentUser());
        queryCount.whereEqualTo(Conversation.KEY_VENDOR,product.get_User());
        queryCount.whereEqualTo(Conversation.KEY_PRODUCT,product);
        queryCount.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException ex) {
                if(ex!=null){
                    Log.d("Negociation","Erreur : "+ex.getMessage());
                    ex.printStackTrace();
                    return;
                }

                if(objects.size()!=0){
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
                    query.whereEqualTo(Message.KEY_CONVERSATION,objects.get(0));
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
        });*/

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_VENDOR);
        query.include(Message.KEY_USER);
        query.whereEqualTo(Message.KEY_CONVERSATION, conversation);
        query.orderByAscending(Conversation.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if(e!=null){
                    Log.d("Negociation","Erreur:"+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                if(objects.size()!=0){
                    adapter.AddAllToList(objects);
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
