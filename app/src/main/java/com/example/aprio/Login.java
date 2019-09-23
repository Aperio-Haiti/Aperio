package com.example.aprio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
            //todo: Check the category of user
            GoToSpecificPage(currentUser);
            //todo: intent map activity
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();

                Log.d("APP_MESS", "You clicked ! " + mEmail + " " + mPassword);
                if(!mEmail.contentEquals("") && !mPassword.contentEquals("")){
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("email",mEmail);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser object, ParseException e) {
                            if(e!=null){
                                Toast.makeText(getApplicationContext(),"Erreur :"+e.getMessage(),Toast.LENGTH_LONG).show();
                                return;
                            }
                            String username = object.getUsername();
                            //maintenant on login
                            ParseUser.logInInBackground(username, mPassword, new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (e != null) {
                                        Toast.makeText(Login.this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                        return;
                                    }
                                    //todo: Check the category of user
                                    GoToSpecificPage(user);
                                    //todo: intent map distinct activity
                                }
                            });
                        }
                    });
                }else {
                    if(mEmail.contentEquals("") && !mPassword.contentEquals(""))
                        Toast.makeText(Login.this,"Please enter email",Toast.LENGTH_LONG).show();
                    if (!mEmail.contentEquals("") && mPassword.contentEquals(""))
                        Toast.makeText(Login.this,"Please enter password",Toast.LENGTH_LONG).show();
                    if (mEmail.contentEquals("") && mPassword.contentEquals(""))
                        Toast.makeText(Login.this,"Please enter credentials",Toast.LENGTH_LONG).show();
                }

            }
        });

        tvSaS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup_as_vendor.class));
            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,SignUser.class));
            }
        });
    }

    private void GoToSpecificPage(ParseUser user) {
        if (user.getBoolean("Category")) {
            //Vendor
            Log.d("APP_MESS", "I'm a vendor");
            Intent intent = new Intent(Login.this, MapSellerActivity.class);
            startActivity(intent);
            finish();
            //Toast.makeText(Login.this,"I'm a vendor",Toast.LENGTH_LONG).show();
        } else {
            //User
            Log.d("APP_MESS", "I'm a user");
            //Toast.makeText(Login.this,"I'm a user",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Login.this,MapsUserActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
