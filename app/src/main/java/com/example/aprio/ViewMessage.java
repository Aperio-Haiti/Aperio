package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.aprio.Adapters.MyMessageAdapter;
import com.example.aprio.Models.Conversation;
import com.example.aprio.Models.Message;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewMessage extends AppCompatActivity {

    @BindView(R.id.rvAllMyMessage)
    RecyclerView rvMessage;
    @BindView(R.id.toolbar2)
    Toolbar toolbar;

    MyMessageAdapter adapter;
    List<Conversation> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("Messages");

        list = new ArrayList<>();
        adapter = new MyMessageAdapter(this,list);
        rvMessage.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        rvMessage.setAdapter(adapter);

        /*ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.include(Conversation.KEY_USER);
        query.include(Conversation.KEY_PRODUCT);
        query.whereEqualTo(Conversation.KEY_VENDOR,ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if(e!=null){
                    Log.d("ViewMessage","Erreur :"+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                adapter.AddAllToList(objects);
            }
        });*/

        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(Conversation.KEY_USER,ParseUser.getCurrentUser());
        query.whereNotEqualTo(Conversation.KEY_VENDOR,ParseUser.getCurrentUser());

        ParseQuery<Conversation> query1 = ParseQuery.getQuery(Conversation.class);
        query1.whereEqualTo(Conversation.KEY_VENDOR,ParseUser.getCurrentUser());
        query1.whereNotEqualTo(Conversation.KEY_USER,ParseUser.getCurrentUser());

        List<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();
        queries.add(query);
        queries.add(query1);

        ParseQuery<Conversation> mainQuery = ParseQuery.or(queries);
        mainQuery.include(Conversation.KEY_VENDOR);
        mainQuery.include(Conversation.KEY_USER);
        mainQuery.include(Conversation.KEY_PRODUCT);
        mainQuery.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if(e!=null){
                    Log.d("ViewMessage","Erreur: "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                Log.d("ViewMessage","Count: "+objects.size());
                adapter.AddAllToList(objects);
                /*for(int i=0;i<objects.size();i++){
                    Log.d("ViewMessage","Sender: "+objects.get(i).getUser().getUsername()+" Reciever: "+objects.get(i).getVendor().getUsername());
                }*/
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
