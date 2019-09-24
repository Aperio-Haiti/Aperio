package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Models.Category;
import com.example.aprio.Models.Product;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        objectId = getIntent().getStringExtra("Product");
        ParseQuery<Product> query = new ParseQuery<Product>(Product.class);
        query.include(Product.KEY_USER);
        query.include(Product.KEY_CATEGORY);
        query.getInBackground(objectId, new GetCallback<Product>() {
            @Override
            public void done(Product object, ParseException e) {
                if(e!=null){
                    Log.d("ProductDetail","Erreur : "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                product = object;
                init();
            }
        });

    }
    private void init() {
        Glide.with(getApplicationContext()).load(product.get_Image_Product().getUrl())
                .apply(new RequestOptions().error(R.drawable.error))
                .into(ivPoster);
        tvCategory.setText(product.get_Category().getString(Category.KEY_CATEGORY));
        tvDescription.setText(product.get_Description());
        tvSeller.setText(product.get_User().getUsername());
        btnCantactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: Intent to message activity
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
