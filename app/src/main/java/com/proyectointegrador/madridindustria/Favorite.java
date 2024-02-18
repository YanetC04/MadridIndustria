package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.*;

import android.annotation.SuppressLint;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;

import java.util.Objects;

public class Favorite extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView textView;
    private ImageView imagenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        linearLayout = findViewById(R.id.linear);
        textView = findViewById(R.id.textView);

        // BASE DE DATOS LOCAL
        localDB dbHelper = new localDB(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("favorites", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                View favoriteCard = LayoutInflater.from(Favorite.this).inflate(R.layout.favorite_card, null);

                ImageView imagen = favoriteCard.findViewById(R.id.imagen);
                imagenId = favoriteCard.findViewById(R.id.imagen);
                TextView nombre = favoriteCard.findViewById(R.id.nombre);
                TextView inaguracion = favoriteCard.findViewById(R.id.inaguracion);
                TextView patrimonio = favoriteCard.findViewById(R.id.patrimonio);
                TextView metro = favoriteCard.findViewById(R.id.metro);
                TextView direccion = favoriteCard.findViewById(R.id.direccion);

                // Verificar si el cursor contiene la columna antes de obtener su índice
                int columnIndexNombre = cursor.getColumnIndex("nombre");
                int columnIndexInaguracion = cursor.getColumnIndex("inaguracion");
                int columnIndexPatrimonio = cursor.getColumnIndex("patrimonio");
                int columnIndexMetro = cursor.getColumnIndex("metro");
                int columnIndexDireccion = cursor.getColumnIndex("direccion");
                int columnIndexImagen = cursor.getColumnIndex("imagen");

                if (columnIndexNombre != -1) {
                    // Obtener los valores de cada columna
                    String nombreValor = cursor.getString(columnIndexNombre);
                    String inaguracionValor = cursor.getString(columnIndexInaguracion);
                    String patrimonioValor = cursor.getString(columnIndexPatrimonio);
                    String metroValor = cursor.getString(columnIndexMetro);
                    String direccionValor = cursor.getString(columnIndexDireccion);
                    String imagenValor = cursor.getString(columnIndexImagen);

                    // ESTABLECER INFORMACION
                    textView.setVisibility(View.INVISIBLE);
                    nombre.setText(nombreValor);
                    inaguracion.setText(inaguracionValor);
                    patrimonio.setText(patrimonioValor);
                    metro.setText(metroValor);
                    direccion.setText(direccionValor);

                    Glide.with(Favorite.this)
                            .load(imagenValor)
                            .centerCrop()
                            .into(imagen);

                    linearLayout.addView(favoriteCard);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // BARRA INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.like);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;
            String source = getIntent().getStringExtra("source");

            if (item.getItemId() == R.id.home) {
                intent = new Intent(Favorite.this, MainActivity.class).putExtra("source", source);
            }
            if (item.getItemId() == R.id.map) {
                intent = new Intent(Favorite.this, Map.class).putExtra("source", source);
            }
            if (item.getItemId() == R.id.add) {
                if (Objects.requireNonNull(source).equalsIgnoreCase("cerrado")) {
                    showDialog(Add.class);
                } else {
                    intent = new Intent(Favorite.this, Add.class);
                }
            }
            if (item.getItemId() == R.id.profile) {
                if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){
                    showDialog(Profile.class);
                } else {
                    intent = new Intent(Favorite.this, Profile.class);
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
                    startActivity(new Intent(Favorite.this, Hall.class).putExtra("intent", intent.getName()));
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

    @Override
    protected void onStop() {
        super.onStop();

        if (imagenId != null) {
            Glide.with(this).clear(imagenId);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}