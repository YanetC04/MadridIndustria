package com.proyectointegrador.madridindustria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.*;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Add extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Spinner modo = findViewById(R.id.modo);

        // SPINNER MODO
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.modo, android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modo.setAdapter(adapter);

        modo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.WHITE);
                ((TextView) view).setTextSize(19);

                switch (parent.getItemAtPosition(position).toString()) {
                    case "Añadir": case "Add":
                        redirFragment(new Add_Fragment());
                        break;
                    case "Modificar": case "Modify":
                        redirFragment(new Edit_Fragment());
                        break;
                    case "Eliminar": case "Delete":
                        redirFragment(new Delete_Fragment());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        barraInferior();
    }

    private void barraInferior(){
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
                intent = new Intent(Add.this, Profile.class).putExtra("source", "abierto").putExtra("correo", getIntent().getStringExtra("correo"));
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    private void redirFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}