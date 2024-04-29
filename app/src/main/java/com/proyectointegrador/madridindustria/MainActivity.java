package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.*;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde", "retiro", "tetuan", "fuencarral", "vallecas", "barajas", "hortaleza", "latina", "salamanca"};

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

            getCount(dist, count -> {
                for (int i = 1; i <= count; i++) {
                    View internalLayoutView = LayoutInflater.from(this).inflate(R.layout.internal_layout, null);
                    ImageView imagen = internalLayoutView.findViewById(R.id.imagen);
                    TextView texto = internalLayoutView.findViewById(R.id.texto);
                    String value = String.valueOf(i);

                    // BASE DE DATOS
                    new FirestoreDatabase(dist, value, firestoreDatabase -> {
                        // ESTABLECER INFORMACION TRADUCIDA
                        if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esEspanol", true)){
                            distrito.setText(firestoreDatabase.getDistrito());
                            texto.setText(firestoreDatabase.getNombre());
                        } else {
                            distrito.setText(obtenerDistrito(firestoreDatabase.getDistrito()));

                            Traductor.traducirTexto(firestoreDatabase.getNombre(), new Traductor.OnTranslationComplete() {
                                @Override
                                public void onTranslationComplete(String translatedText) {
                                    texto.setText(translatedText);
                                }

                                @Override
                                public void onTranslationFailed(String errorMessage) {

                                }
                            });
                        }

                        if (!isDestroyed()) {
                            Glide.with(MainActivity.this)
                                    .load(firestoreDatabase.getImagen())
                                    .centerCrop()
                                    .into(imagen);
                        }
                    });

                    // CONFIGURAMOS LA IMAGEN
                    imagen.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Patrimonio.class).putExtra("collection", dist).putExtra("document", value).putExtra("source", getIntent().getStringExtra("source"))));

                    internalLinear.addView(internalLayoutView);
                }
            });

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
                    showDialog();
                } else {
                    intent = new Intent(MainActivity.this, Add.class);
                }
            }
            if (item.getItemId() == R.id.like) {
                intent = new Intent(MainActivity.this, Favorite.class).putExtra("source", source);
            }
            if (item.getItemId() == R.id.profile) {
                if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){
                    intent = new Intent(MainActivity.this, Profile.class).putExtra("source", "cerrado");
                } else {
                    intent = new Intent(MainActivity.this, Profile.class).putExtra("source", source);
                }
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    private String obtenerDistrito(String distritoText) {
        String distritoNombre = distritoText.split("\\s+")[1];
        distritoNombre = quitarAcentos(distritoNombre);

        if (distritoNombre.equals("San")) {
            return "Distrito San Blas-Canillejas";
        }

        return distritoNombre + " District";
    }

    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public void getCount(String dist, final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection(dist).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size();
                countCallback.onCallback(count);
            } else {
                countCallback.onCallback(-1);
            }
        });
    }

    // DIALOGO DE ERROR
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.gest))
                .setMessage(getResources().getString(R.string.mGestor))
                .setPositiveButton(getResources().getString(R.string.si), (dialog, which) -> {
                    startActivity(new Intent(MainActivity.this, Hall.class));
                    overridePendingTransition(0, 0);
                })
                .setNegativeButton("NO", null);

        builder.create().show();
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}