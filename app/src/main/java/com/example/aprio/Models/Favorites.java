package com.example.aprio.Models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Favorites")
public class Favorites extends ParseObject {
    public static final String KEY_USER = "User";
    public static final String KEY_PRODUCT = "Product";

    public void setUser(ParseUser user){put(KEY_USER,user);}
    public ParseUser getUser(){return getParseUser(KEY_USER);}
    public void setProduct(ParseObject product){put(KEY_PRODUCT,product);}
    public ParseObject getProduct(){return getParseObject(KEY_PRODUCT);}
}
