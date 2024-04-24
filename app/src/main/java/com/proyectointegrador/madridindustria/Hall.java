package com.proyectointegrador.madridindustria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Hall extends AppCompatActivity {
    private ImageView imagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);

        imagen = findViewById(R.id.ministerio);
        Button inicio = findViewById(R.id.inicio);
        Button registro = findViewById(R.id.registro);

        // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
        Glide.with(Hall.this)
                .load(R.drawable.ministerio)
                .into(imagen);

        inicio.setOnClickListener(v -> {
            Intent intent = new Intent(Hall.this, Login.class);
            startActivity(intent);
        });

        registro.setOnClickListener(v -> {
            Intent intent = new Intent(Hall.this, Register.class);
            startActivity(intent);
        });

        // VOLVER ATRAS
        TextView volverButton = findViewById(R.id.volver);
        volverButton.setOnClickListener(view -> volverAtras());
    }

    private void volverAtras(){
        startActivity(new Intent(Hall.this,MainActivity.class).putExtra("source", "cerrado"));
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.with(this).clear(imagen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Glide.with(Hall.this)
                .load(R.drawable.ministerio)
                .into(imagen);
    }

}