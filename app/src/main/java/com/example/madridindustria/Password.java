package com.example.madridindustria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Password extends AppCompatActivity {

    private Button inicio, olvidado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        inicio = findViewById(R.id.inicio);
        olvidado = findViewById(R.id.olvidado);

        // NOS FALTA COMPROBAR CONSTRASEÑA Y CONECTARLA DE BASE DE DATOS

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Password.this, MainActivity.class);
                startActivity(intent);
            }
        });

        olvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Password.this, Password2.class);
                startActivity(intent);
            }
        });
    }
}