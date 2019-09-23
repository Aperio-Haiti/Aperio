package com.example.aprio.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Product")
public class Product extends ParseObject {

    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_PRODUCT= "Product";
    public static final String KEY_CATEGORY = "Category";
    public static final String KEY_USER = "Vendor";

    public String get_Description()
    {
        return getString(KEY_DESCRIPTION);
    }

    public void set_Description(String description){
        put(KEY_DESCRIPTION,description);
    }

    public String get_Category()
    {
        return getString(KEY_CATEGORY);
    }

    public void set_Category(String category){
        put(KEY_CATEGORY,category);
    }

    public ParseFile get_Image_Product(){
        return getParseFile(KEY_PRODUCT);
    }

    public void set_Image_Product(ParseFile image_product){
        put(KEY_PRODUCT,image_product);
    }

    public ParseUser get_User(){
        return getParseUser(KEY_USER);
    }

    public void set_User(ParseUser user){
        put(KEY_USER,user);
    }
}
