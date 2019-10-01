package com.example.aprio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity {

    @BindView(R.id.etEmailLogin)
    EditText email;
    @BindView(R.id.etPasswordLogin)
    EditText password;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvSignupAsSeller)
    TextView tvSaS;
    @BindView(R.id.tvSignup)
    TextView tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //Session
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            GoToSpecificPage(currentUser);
        }

        btnLogin.setOnClickListener(view -> {
            String mEmail = email.getText().toString();
            String mPassword = password.getText().toString();

            ProgressDialog pd = new ProgressDialog(Login.this);
            pd.setTitle("Logging in...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);

            Log.d("APP_MESS", "You clicked ! " + mEmail + " " + mPassword);
            if(!mEmail.contentEquals("") && !mPassword.contentEquals("")){
                pd.show();
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("email",mEmail);
                query.getFirstInBackground((object, e) -> {
                    pd.dismiss();
                    if(e!=null){
                        Toast.makeText(getApplicationContext(),"Erreur :"+e.getMessage(),Toast.LENGTH_LONG).show();
                        return;
                    }
                    String username = object.getUsername();
                    //maintenant on login
                    ParseUser.logInInBackground(username, mPassword, (user, e1) -> {
                        if (e1 != null) {
                            Toast.makeText(Login.this, "Erreur : " + e1.getMessage(), Toast.LENGTH_LONG).show();
                            e1.printStackTrace();
                            return;
                        }
                        GoToSpecificPage(user);
                    });
                });
            }else {
                if(mEmail.contentEquals("") && !mPassword.contentEquals(""))
                    Toast.makeText(Login.this,"Please enter email",Toast.LENGTH_LONG).show();
                if (!mEmail.contentEquals("") && mPassword.contentEquals(""))
                    Toast.makeText(Login.this,"Please enter password",Toast.LENGTH_LONG).show();
                if (mEmail.contentEquals("") && mPassword.contentEquals(""))
                    Toast.makeText(Login.this,"Please enter credentials",Toast.LENGTH_LONG).show();
            }

        });

        tvSaS.setOnClickListener(view -> startActivity(new Intent(Login.this, Signup_as_vendor.class)));

        tvSignup.setOnClickListener(view -> startActivity(new Intent(Login.this,SignUser.class)));
    }

    private void GoToSpecificPage(ParseUser user) {
        if (user.getBoolean("Category")) {
            //Vendor
            Log.d("APP_MESS", "I'm a vendor");
            Intent intent = new Intent(Login.this, MapSellerActivity.class);
            startActivity(intent);
            finish();
        } else {
            //User
            Log.d("APP_MESS", "I'm a user");
            Intent intent = new Intent(Login.this,MapsUserActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
