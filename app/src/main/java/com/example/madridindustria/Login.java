package com.example.madridindustria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
    public void openLogin(View v) {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }
}