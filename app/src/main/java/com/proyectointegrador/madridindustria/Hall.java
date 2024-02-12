package com.proyectointegrador.madridindustria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

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
        String source = getIntent().getStringExtra("intent");

        // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
        if (!isDestroyed()) {
            Glide.with(Hall.this)
                    .load(R.drawable.ministerio)
                    .into(imagen);
        }

        inicio.setOnClickListener(v -> {
            Intent intent = new Intent(Hall.this, Login.class).putExtra("intent", source);
            startActivity(intent);
        });

        registro.setOnClickListener(v -> {
            Intent intent = new Intent(Hall.this, Register.class).putExtra("intent", source);
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
    protected void onDestroy() {
        super.onDestroy();
    }
}