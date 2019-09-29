package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Models.Category;
import com.example.aprio.Models.Conversation;
import com.example.aprio.Models.Product;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetail extends AppCompatActivity {

    @BindView(R.id.toolbarProductDetail)
    Toolbar toolbar;
    @BindView(R.id.ivPosterProductDetail)
    ImageView ivPoster;
    @BindView(R.id.tvCategoryProductDetail)
    TextView tvCategory;
    @BindView(R.id.tvDescriptionProduct)
    TextView tvDescription;
    @BindView(R.id.tvSellerProduct)
    TextView tvSeller;
    @BindView(R.id.ivBookmarkProduct)
    ImageView ivBookmark;
    @BindView(R.id.btnContactSeller)
    Button btnCantactSeller;
    String objectId;
    Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        objectId = getIntent().getStringExtra("Product");
        ParseQuery<Product> query = new ParseQuery<>(Product.class);
        query.include(Product.KEY_USER);
        query.include(Product.KEY_CATEGORY);
        query.getInBackground(objectId, (object, e) -> {
            if(e!=null){
                Log.d("ProductDetail","Erreur : "+e.getMessage());
                e.printStackTrace();
                return;
            }
            product = object;
            init();
        });

    }
    private void init() {
        Glide.with(getApplicationContext()).load(product.get_Image_Product().getUrl())
                .apply(new RequestOptions().error(R.drawable.error))
                .into(ivPoster);
        tvCategory.setText(product.get_Category().getString(Category.KEY_CATEGORY));
        tvDescription.setText(product.get_Description());
        tvSeller.setText(product.get_User().getUsername());
        btnCantactSeller.setOnClickListener(view -> {
            ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
            query.whereEqualTo(Conversation.KEY_VENDOR,product.get_User());
            query.whereEqualTo(Conversation.KEY_USER, ParseUser.getCurrentUser());
            query.whereEqualTo(Conversation.KEY_PRODUCT,product);
            query.findInBackground((objects, e) -> {
                if(e!=null){
                    Log.d("ProductDetail","Erreur :"+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                if(objects.size()!=0){
                    Intent intent = new Intent(ProductDetail.this,Negociation.class);
                    intent.putExtra("Product",product.getObjectId());
                    intent.putExtra("Convo",objects.get(0).getObjectId());
                    Log.d("ProductDetail","Id: "+objects.get(0).getObjectId());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(ProductDetail.this,Negociation.class);
                    intent.putExtra("Product",product.getObjectId());
                    intent.putExtra("Convo","");
                    Log.d("ProductDetail","Id: nothing");
                    startActivity(intent);
                }
            });

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void Contact(View view) {
        startActivity(new Intent(ProductDetail.this,Negociation.class));
    }
}
