package com.example.aprio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class addproduct extends AppCompatActivity {

    @BindView(R.id.Imgpost)
    ImageView Imgpost;
    @BindView(R.id.imgTakeImg)
    ImageView imgTakeImg;
    @BindView(R.id.etProductTitle)
    EditText etProductTitle;
    @BindView(R.id.etCategory)
    EditText etCategory;
    @BindView(R.id.btnPost)
    Button btnPost;

    private static final String TAG = "addproduct";

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);
        ButterKnife.bind(this);

        imgTakeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = ParseUser.getCurrentUser();
                String description = etProductTitle.getText().toString().trim();
                String category = etCategory.getText().toString().trim();
                
                if(photoFile != null) {
                    saveProduct(description, user, category, photoFile);
                }
                else{
                    Log.e(TAG, "onClick: no photo submit");
                    Toast.makeText(getApplicationContext(),"there is no photo please take a photo",Toast.LENGTH_SHORT).show();
                    return;
                }
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
        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "com.codepath.fileprovider", photoFile);
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
                // Load the taken image into a preview
                Imgpost.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveProduct(String description, ParseUser user,String category, File photoFile) {
        Product product = new Product();

        product.set_Description(description);
        product.set_Category(category);
        product.set_User(user);
        product.set_Image_Product(new ParseFile(photoFile));

//        ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
//        pb.setVisibility(ProgressBar.VISIBLE);

        product.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, "done: failed");                   
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "done: Success");
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
//                pb.setVisibility(ProgressBar.INVISIBLE);
//                et_description.setText("");
//                iv_post.setImageResource(0);
            }
        });
    }
}
