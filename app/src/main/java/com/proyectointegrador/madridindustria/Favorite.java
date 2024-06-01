package com.proyectointegrador.madridindustria;
import androidx.appcompat.app.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public class Favorite extends AppCompatActivity {
    private ImageView imagenId;
    private String imagenV;
    private TextView textView;
    private String dist = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).contains("esEspanol")){
            setLocale(getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esEspanol", true) ? "es" : "en");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            reiniciarApp();
            swipeRefreshLayout.setRefreshing(false);
        });

        cargarDatos();

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
                    showDialog();
                } else {
                    intent = new Intent(Favorite.this, Add.class);
                }
            }
            if (item.getItemId() == R.id.profile) {
                if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){
                    intent = new Intent(Favorite.this, Profile.class).putExtra("source", "cerrado");
                } else {
                    intent = new Intent(Favorite.this, Profile.class).putExtra("source", source);
                }
            }

            if (intent != null) {
                startActivity(intent);
                // SIN TRANSICION
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }
    private void reiniciarApp() {
        recreate();
    }

    private void cargarDatos(){
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

                // VERIFICAR SI EL CURSOR CONTIENE LA COLUMNA ANTES DE OBTENER SU INDICE
                int columnIndexNombre = cursor.getColumnIndex("nombre");
                int columnIndexInaguracion = cursor.getColumnIndex("inaguracion");
                int columnIndexPatrimonio = cursor.getColumnIndex("patrimonio");
                int columnIndexMetro = cursor.getColumnIndex("metro");
                int columnIndexDireccion = cursor.getColumnIndex("direccion");
                int columnIndexDistrito = cursor.getColumnIndex("distrito");
                int columnIndexImagen = cursor.getColumnIndex("imagen");

                if (columnIndexNombre != -1) {
                    // OBTENER VALORES CADA COLUMNA
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
                    if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esEspanol", true)){
                        nombre.setText(nombreValor);
                        inaguracion.setText(inaguracionValor);
                        patrimonio.setText(patrimonioValor);
                        metro.setText(metroValor);
                        direccion.setText(direccionValor);
                    } else {
                        traducirTexto(nombre, nombreValor);
                        traducirTexto(inaguracion, inaguracionValor);
                        traducirTexto(patrimonio, patrimonioValor);
                        traducirTexto(metro, metroValor);
                        traducirTexto(direccion, direccionValor);
                    }


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

        // CONSULTA PARA BUSCAR DOC CON NOMBRE ESPECIFICO
        patrimoniosRef.whereEqualTo("nombre", nombreValor)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // OBTENER DOCUMENTO ID
                            String documentId = document.getId();
                            Intent intent = new Intent(Favorite.this, Patrimonio.class);
                            intent.putExtra("collection", dist);
                            intent.putExtra("document", documentId);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void traducirTexto(TextView view, String texto){
        Traductor.traducirTexto(texto, new Traductor.OnTranslationComplete() {
            @Override
            public void onTranslationComplete(String translatedText) {
                view.setText(translatedText);
            }

            @Override
            public void onTranslationFailed(String errorMessage) {

            }
        }, this);
    }


    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.gest))
                .setMessage(getResources().getString(R.string.mGestor))
                .setPositiveButton(getResources().getString(R.string.si), (dialog, which) -> {
                    startActivity(new Intent(Favorite.this, Hall.class));
                    overridePendingTransition(0, 0);
                })
                .setNegativeButton("NO", null);

        builder.create().show();
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
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

    private void setLocale(String idioma) {
        Locale nuevoLocale = new Locale(idioma);
        Locale.setDefault(nuevoLocale);

        Configuration configuracion = this.getResources().getConfiguration();
        configuracion.setLocale(nuevoLocale);

        getBaseContext().getResources().updateConfiguration(configuracion, getBaseContext().getResources().getDisplayMetrics());
    }
}