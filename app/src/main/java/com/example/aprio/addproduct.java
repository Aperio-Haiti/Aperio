package com.example.aprio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class addproduct extends AppCompatActivity {

    @BindView(R.id.post)
    ImageView post;
    @BindView(R.id.imagePost)
    ImageView imagePost;
    @BindView(R.id.etProductTitle)
    EditText etProductTitle;
    @BindView(R.id.etCategory)
    EditText etCategory;
    @BindView(R.id.btnPost)
    Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);
        ButterKnife.bind(this);
    }
}
