package com.example.aprio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUser extends AppCompatActivity {

    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.edt_cfm_password)
    EditText edtCfmPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    static final  String TAG = "SignUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_user);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.btnLogin, R.id.tvLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                String username = edtName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String password_confirm = edtCfmPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();

                if(password.contentEquals(password_confirm))
                    Sign_user(username, password, email);
                else
                    Toast.makeText(getApplicationContext(),"No Matched Password", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvLogin:
                finish();
                break;
        }
    }

    public void Sign_user(String username, String password,String email)
    {
        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("ProfileImg",new ParseFile(DrawableToByteArray(R.drawable.avatar)));

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Intent i = new Intent(getApplicationContext(),MapsUserActivity.class);
                    startActivity(i);
                }
                else{
                    ParseUser.logOut();
                    Log.e(TAG,e.getMessage());
                }
            }
        });
    }

    private byte[] DrawableToByteArray(int avatar) {
        Drawable d = getResources().getDrawable(avatar);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
