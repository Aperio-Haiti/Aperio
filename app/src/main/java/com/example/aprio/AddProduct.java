package com.example.aprio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aprio.Adapters.CategorySpinnerAdapter;
import com.example.aprio.Models.Category;
import com.example.aprio.Models.Product;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddProduct extends AppCompatActivity {

    @BindView(R.id.Imgpost)
    ImageView Imgpost;

    @BindView(R.id.imgTakeImg)
    ImageView imgTakeImg;

    @BindView(R.id.etProductTitle)
    EditText etProductTitle;

    @BindView(R.id.btnSaveProduct)
    Button btnPost;

    @BindView(R.id.spinnerCategory)
    Spinner spnCategory;

    private static final String TAG = "AddProduct";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    CategorySpinnerAdapter adapter;
    ArrayList<Category> arrayList = new ArrayList<>();
    Category category = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);
        ButterKnife.bind(this);

        NavigationInit();

        imgTakeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        ParseQuery<Category> query = new ParseQuery<Category>(Category.class);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                if(e!=null){
                    Log.d("AddProduct","Erreur : "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                arrayList.clear();
                arrayList.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new CategorySpinnerAdapter(AddProduct.this,android.R.layout.simple_spinner_dropdown_item,arrayList);
        spnCategory.setAdapter(adapter);
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapter.getItem(i);
                Log.d("AddProduct","You selected : "+category.getCategory()+" , ID : "+category.getObjectId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = ParseUser.getCurrentUser();
                String description = etProductTitle.getText().toString().trim();
                
                if(photoFile != null) {
                    saveProduct(description, user, category, photoFile);
                    finish();
                }
                else{
                    Log.e(TAG, "onClick: no photo submit");
                    Toast.makeText(getApplicationContext(),"there is no photo please take a photo",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void NavigationInit(){
        CircleImageView imageView = findViewById(R.id.ivUserAvatar);
        Glide.with(this).load(ParseUser.getCurrentUser().getParseFile("ProfileImg").getUrl())
                .apply(new RequestOptions().placeholder(R.drawable.error).error(R.drawable.error))
                .into(imageView);

        //navigationview
        NavigationView navigationView = findViewById(R.id.vendor_navigation_view);
        View headerLayout = navigationView.getHeaderView(0);

        CircleImageView ivHeaderPhoto = (CircleImageView) headerLayout.findViewById(R.id.ivUser);
        ivHeaderPhoto.setBorderColor(Color.WHITE);
        ivHeaderPhoto.setBorderWidth(10);

        Glide.with(this).load(ParseUser.getCurrentUser().getParseFile("ProfileImg").getUrl())
                .apply(new RequestOptions().placeholder(R.drawable.error).error(R.drawable.error))
                .into(ivHeaderPhoto);

        TextView tvHeaderName = headerLayout.findViewById(R.id.tvUserName);
        tvHeaderName.setText(ParseUser.getCurrentUser().getUsername());

        TextView tvHeaderEmail = headerLayout.findViewById(R.id.tvUserEmail);
        tvHeaderEmail.setText(ParseUser.getCurrentUser().getEmail());

        TextView tvLogout = headerLayout.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        drawerLayout= findViewById(R.id.drawer1);
        actionBarDrawerToggle=new ActionBarDrawerToggle(AddProduct.this,drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //img on click to open drawer
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 20);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(photoFileName + "_resized");
                try {
                    resizedFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(resizedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // Write the bytes of the bitmap to file
                try {
                    fos.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the taken image into a preview
                Imgpost.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProduct(String description, ParseUser user,Category category, File photoFile) {
        Product product = new Product();

        product.set_Description(description);
        product.set_Category(category);
        product.set_User(user);
        product.set_Image_Product(new ParseFile(photoFile));

        ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

        product.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, "done: failed"+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "done: Success");
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                pb.setVisibility(ProgressBar.INVISIBLE);
                etProductTitle.setText("");
                Imgpost.setImageResource(0);
                Imgpost.setColorFilter(R.color.colorAccent);
            }
        });
    }
}
