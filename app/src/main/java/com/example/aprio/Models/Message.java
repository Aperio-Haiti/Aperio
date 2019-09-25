package com.example.aprio.Models;

import com.example.aprio.Models.Product;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_VENDOR = "vendor";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_PRODUCT = "product";

    public void setUser(ParseUser user){put(KEY_USER,user);}
    public ParseUser getUser(){return getParseUser(KEY_USER);}
    public void setVendor(ParseUser user){put(KEY_VENDOR,user);}
    public ParseUser getVendor(){return getParseUser(KEY_VENDOR);}
    public void setMessage(String message){put(KEY_MESSAGE,message);}
    public String getMessage(){return getString(KEY_MESSAGE);}
    public void setProduct(ParseObject product){put(KEY_PRODUCT,product);}
    public ParseObject getProduct(){return getParseObject(KEY_PRODUCT);}
}
