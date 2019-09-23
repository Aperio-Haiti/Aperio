package com.example.aprio.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Category")
public class Category extends ParseObject {

    public static final String KEY_CATEGORY = "Category";

    public void setCategory(String category){put(KEY_CATEGORY,category);}
    public String getCategory(){return getString(KEY_CATEGORY);}
}
