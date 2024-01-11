package com.example.madridindustria;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.*;

public class Map extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Comprueba y solicita permisos
        //checkLocationPermission();

        //provisional
        initMap();
    }

    private void initMap() {
        // Inicializa el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // El mapa está listo para ser utilizado
                // Puedes personalizarlo según tus necesidades
            }
        });
    }

}