package com.example.aprio;

import android.app.Application;

import com.example.aprio.Models.Category;
import com.example.aprio.Models.Conversation;
import com.example.aprio.Models.Favorites;
import com.example.aprio.Models.Message;
import com.example.aprio.Models.Product;
import com.parse.Parse;
import com.parse.ParseObject;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Product.class);
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Conversation.class);
        ParseObject.registerSubclass(Favorites.class);



        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.app_id))
                .clientKey(getString(R.string.client_key))
                .server(getString(R.string.server_url)).build());

    }
}
