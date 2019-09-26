package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.aprio.Adapters.MyMessageAdapter;
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
    List<Message> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        list = new ArrayList<>();
        adapter = new MyMessageAdapter(this,list);
        rvMessage.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        rvMessage.setAdapter(adapter);

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_USER);
        query.include(Message.KEY_PRODUCT);
        query.whereEqualTo(Message.KEY_VENDOR, ParseUser.getCurrentUser());
        query.orderByDescending(Message.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if(e!=null){
                    Log.d("ViewMessage","Erreur:"+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                adapter.AddAllToList(objects);
            }
        });
    }
}
