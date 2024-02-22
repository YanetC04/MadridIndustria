package com.proyectointegrador.madridindustria;
import androidx.appcompat.app.*;

import android.annotation.SuppressLint;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;

import java.text.Normalizer;
import java.util.Objects;

public class Favorite extends AppCompatActivity {
    private ImageView imagenId;
    private String imagenV;
    private TextView textView;
    private String dist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        LinearLayout linearLayout = findViewById(R.id.linear);
        textView = findViewById(R.id.textView);

        // BASE DE DATOS LOCAL
        localDB dbHelper = new localDB(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("favorites", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                View favoriteCard = LayoutInflater.from(Favorite.this).inflate(R.layout.favorite_card, null);

                ImageView imagen = favoriteCard.findViewById(R.id.imagen);
                imagenId = imagen;
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
                int columnIndexDistrito = cursor.getColumnIndex("distrito");
                int columnIndexImagen = cursor.getColumnIndex("imagen");

                if (columnIndexNombre != -1) {
                    // Obtener los valores de cada columna
                    String nombreValor = cursor.getString(columnIndexNombre);
                    String inaguracionValor = cursor.getString(columnIndexInaguracion);
                    String patrimonioValor = cursor.getString(columnIndexPatrimonio);
                    String metroValor = cursor.getString(columnIndexMetro);
                    String direccionValor = cursor.getString(columnIndexDireccion);
                    String distritoValor = cursor.getString(columnIndexDistrito);
                    String imagenValor = cursor.getString(columnIndexImagen);
                    imagenV = imagenValor;

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

                    favoriteCard.setOnClickListener(v -> verPatrimonio(distritoValor, nombreValor));
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

    private void verPatrimonio(String distritoValor, String nombreValor) {
        String[] palabras = distritoValor.split("\\s+");

        if (palabras.length >= 2) {
            String distritoNombre = palabras[1].toLowerCase();
            distritoNombre = quitarAcentos(distritoNombre);

            if (distritoNombre.equals("san")) {
                dist = "sanblas";
            } else {
                dist = distritoNombre;
            }
        }

        CollectionReference patrimoniosRef = FirebaseFirestore.getInstance().collection(dist);

        // Realizar una consulta para buscar el documento con el nombre específico
        patrimoniosRef.whereEqualTo("nombre", nombreValor)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // La consulta fue exitosa
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener el Document ID
                            String documentId = document.getId();

                            // Ahora, puedes usar el Document ID como sea necesario
                            // En este ejemplo, podrías iniciar la actividad Patrimonio con el Document ID como Extra
                            Intent intent = new Intent(Favorite.this, Patrimonio.class);
                            intent.putExtra("collection", dist);
                            intent.putExtra("document", documentId);
                            startActivity(intent);
                        }
                    } else {
                        // Handle errors
                        Log.e("Firebase", "Error al realizar la consulta", task.getException());
                    }
                });
    }

    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
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
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imagenId != null) {
            Glide.with(Favorite.this)
                    .load(imagenV)
                    .into(imagenId);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}