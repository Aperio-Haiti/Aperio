package com.example.aprio.Models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String KEY_USER="user";
    public static final String KEY_VENDOR="vendor";
    public static final String KEY_PRODUCT="product";

    public void setUser(ParseUser user){put(KEY_USER,user);}
    public ParseUser getUser(){return getParseUser(KEY_USER);}
    public void setVendor(ParseUser user){put(KEY_VENDOR,user);}
    public ParseUser getVendor(){return getParseUser(KEY_VENDOR);}
    public void setProduct(ParseObject product){put(KEY_PRODUCT,product);}
    public ParseObject getProduct(){return getParseObject(KEY_PRODUCT);}
}
