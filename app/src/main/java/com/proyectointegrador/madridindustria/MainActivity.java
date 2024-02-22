package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.*;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout linearLayout = findViewById(R.id.linear);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // DINAMICAMENTE CREAR SCROLLVIEW PARA CADA DISTRITO
        for (String dist : distritos) {
            // INFLA EL DISEÑO external_layout.xml
            View externalLayoutView = LayoutInflater.from(this).inflate(R.layout.external_layout, null);
            LinearLayout internalLinear = externalLayoutView.findViewById(R.id.linearExternal);
            TextView distrito = externalLayoutView.findViewById(R.id.distrito);

            for (int i = 1; i <= 5; i++) {
                View internalLayoutView = LayoutInflater.from(this).inflate(R.layout.internal_layout, null);
                ImageView imagen = internalLayoutView.findViewById(R.id.imagen);
                TextView texto = internalLayoutView.findViewById(R.id.texto);
                String value = String.valueOf(i);

                // BASE DE DATOS
                new FirestoreDatabase(dist, value, firestoreDatabase -> {
                    // ESTABLECER INFORMACION
                    distrito.setText(firestoreDatabase.getDistrito());
                    texto.setText(firestoreDatabase.getNombre());

                    if (!isDestroyed()) {
                        Glide.with(MainActivity.this)
                                .load(firestoreDatabase.getImagen())
                                .centerCrop()
                                .into(imagen);
                    }
                });

                // CONFIGURAMOS LA IMAGEN
                imagen.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Patrimonio.class).putExtra("collection", dist).putExtra("document", value)));

                internalLinear.addView(internalLayoutView);
            }

            // AGREGA EL DISEÑO INFLADO AL LINEARLAYOUT DEL SCROLLVIEW
            linearLayout.addView(externalLayoutView);
        }


        // BARRA INFERIOR
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;
            String source = getIntent().getStringExtra("source");

            if (item.getItemId() == R.id.map) {
                intent = new Intent(MainActivity.this, Map.class).putExtra("source", source);
            }
            if (item.getItemId() == R.id.add) {
                if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){
                    showDialog(Add.class);
                } else {
                    intent = new Intent(MainActivity.this, Add.class);
                }
            }
            if (item.getItemId() == R.id.like) {
                intent = new Intent(MainActivity.this, Favorite.class).putExtra("source", source);
            }
            if (item.getItemId() == R.id.profile) {
                if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){
                    showDialog(Profile.class);
                } else {
                    intent = new Intent(MainActivity.this, Profile.class);
                }
            }

            if (intent != null) {
                startActivity(intent);
                // Sin transición
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    // Diálogo de error
    private void showDialog(Class intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Modo Gestor")
                .setMessage("¿Quieres activar el modo Gestor?")
                .setPositiveButton("SÍ", (dialog, which) -> {
                    startActivity(new Intent(MainActivity.this, Hall.class).putExtra("intent", intent.getName()));
                    overridePendingTransition(0, 0);
                })
                .setNegativeButton("NO", null);

        // Creación y visualización del diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}