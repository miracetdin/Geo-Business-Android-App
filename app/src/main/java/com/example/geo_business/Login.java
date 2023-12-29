package com.example.geo_business;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    EditText username, password;
    Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.login_et_username);
        password = (EditText) findViewById(R.id.login_et_password);

        login = (Button) findViewById(R.id.login_bt_login);
        register = (Button) findViewById(R.id.login_bt_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFunc();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), Register.class);
                //startActivity(intent);
                Toast.makeText(Login.this, "REGISTER SAYFA", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginFunc(){
        if((!TextUtils.isEmpty(username.getText().toString())) && (!TextUtils.isEmpty(password.getText().toString()))){
            Toast.makeText(Login.this, "Login successfully!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Login.this, "Form inputs cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }
}