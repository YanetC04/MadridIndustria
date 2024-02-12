package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Add extends AppCompatActivity {

    private EditText nombreEditText, inaguracionEditText, patrimonioEditText,coordenadas_latEditText, coordenadas_lonEditText,  metroEditText, distritoEditText, direccionEditText, descripcionEditText;
    private Button enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        enviar = findViewById(R.id.enviar);

        nombreEditText = findViewById(R.id.nombre);
        inaguracionEditText = findViewById(R.id.inaguracion);
        patrimonioEditText = findViewById(R.id.patrimonio);
        coordenadas_latEditText = findViewById(R.id.coordenadas_lat);
        coordenadas_lonEditText = findViewById(R.id.coordenadas_lon);
        metroEditText = findViewById(R.id.metro);
        distritoEditText = findViewById(R.id.distrito);
        direccionEditText = findViewById(R.id.direccion);
        descripcionEditText = findViewById(R.id.descripcion);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto de los EditText
                String nombre = nombreEditText.getText().toString();
                String inaguracion = inaguracionEditText.getText().toString();
                String patrimonio = patrimonioEditText.getText().toString();
                String coordenadas_lat = coordenadas_latEditText.getText().toString();
                String coordinadas_lon = coordenadas_lonEditText.getText().toString();
                String metro = metroEditText.getText().toString();
                String distrito = distritoEditText.getText().toString();
                String direccion = direccionEditText.getText().toString();
                String descripcion = descripcionEditText.getText().toString();

            }
        });



        // BARRA INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.add);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.home) {
                intent = new Intent(Add.this, MainActivity.class).putExtra("source", "abierto");
            } else if (item.getItemId() == R.id.map) {
                intent = new Intent(Add.this, Map.class).putExtra("source", "abierto");
            }else if (item.getItemId() == R.id.like) {
                intent = new Intent(Add.this, Favorite.class).putExtra("source", "abierto");
            } else if (item.getItemId() == R.id.profile) {
                intent = new Intent(Add.this, Profile.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}