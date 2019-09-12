package com.example.aprio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;

public class Login extends AppCompatActivity {

    @BindView(R.id.etEmailLogin) EditText email;
    @BindView(R.id.etPasswordLogin)EditText password;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.tvSignupAsSeller)TextView tvSaS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                ParseUser.logInInBackground(mEmail, mPassword, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e!=null){
                            Toast.makeText(Login.this,"Erreur : "+e.getMessage(),Toast.LENGTH_LONG).show();
                            return;
                        }
                        //todo: Check the category of user
                        GoToSpecificPage(user);
                        //todo: intent map distinct activity
                    }
                });
            }
        });

        tvSaS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Signup_as_vendor.class));
            }
        });
    }

    private void GoToSpecificPage(ParseUser user) {
        Boolean userType = (Boolean) user.get("Category");
        if(userType == true){
            //Vendor
            Toast.makeText(Login.this,"I'm a vendor",Toast.LENGTH_LONG).show();
        }else{
            //User
            Toast.makeText(Login.this,"I'm a user",Toast.LENGTH_LONG).show();
        }
    }


}
