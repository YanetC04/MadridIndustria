package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Objects;

public class Add extends AppCompatActivity {

    private EditText nombreEditText, inaguracionEditText, patrimonioEditText,coordenadas_latEditText, coordenadas_lonEditText,  metroEditText, direccionEditText, descripcionEditText;
    private TextInputLayout nombreInputLayout, inaguracionInputLayout, patrimonioInputLayout, coordenadas_latInputLayout, coordenadas_lonInputLayout, metroInputLayout, direccionInputLayout, descripcionInputLayout;
    private String nombreText, inaguracionText, patrimonioText,coordenadas_latText, coordenadas_lonText,  metroText, direccionText, descripcionText, distritoText;
    private Spinner distrito;
    private Button enviar;
    private String dist = "";
    private Drawable redBorderDrawable;
    private Drawable defaultBorderDrawable;


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
        distrito = findViewById(R.id.distrito);
        direccionEditText = findViewById(R.id.direccion);
        descripcionEditText = findViewById(R.id.descripcion);
        nombreInputLayout = findViewById(R.id.input_nombre);
        inaguracionInputLayout = findViewById(R.id.input_inaguracion);
        patrimonioInputLayout = findViewById(R.id.input_patrimonio);
        coordenadas_latInputLayout = findViewById(R.id.input_coordenadas_lat);
        coordenadas_lonInputLayout = findViewById(R.id.input_coordenadas_lon);
        metroInputLayout = findViewById(R.id.input_metro);
        direccionInputLayout = findViewById(R.id.input_direccion);
        redBorderDrawable = ContextCompat.getDrawable(this, R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(this, R.drawable.default_border);

        // CREAMOS UN ARRAY DE LOS DATOS QUE QUEREMOS QUE MUESTRE AL SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.distritos, android.R.layout.simple_spinner_item
        );

        // ESPECIFICAMOS EL DISEÑO DE LA LISTA QUE VAMOS A MOSTRAR
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // ASIGNAMOS EL ADAPTADOR A NUESTRO SPINNER
        distrito.setAdapter(adapter);

        // ENVIAR
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreText = nombreEditText.getText().toString();
                inaguracionText = inaguracionEditText.getText().toString();
                patrimonioText = patrimonioEditText.getText().toString();
                coordenadas_latText = coordenadas_latEditText.getText().toString();
                coordenadas_lonText = coordenadas_lonEditText.getText().toString();
                metroText = metroEditText.getText().toString();
                direccionText = direccionEditText.getText().toString();
                descripcionText = descripcionEditText.getText().toString();
                distritoText = distrito.getSelectedItem().toString();
                double latitude = 0.0;
                double longitude = 0.0;

                if (!coordenadas_latText.isEmpty() && !coordenadas_lonText.isEmpty()) {
                    latitude = Double.parseDouble(coordenadas_latText);
                    longitude = Double.parseDouble(coordenadas_lonText);
                } else {
                    if (coordenadas_latText.isEmpty()) {
                        coordenadas_latInputLayout.setHint("");
                        coordenadas_latEditText.setBackground(redBorderDrawable);
                        coordenadas_latEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    coordenadas_latEditText.setBackground(defaultBorderDrawable);
                                    coordenadas_latInputLayout.setHint(R.string.email);
                                }
                            }
                        });
                    }
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (!(nombreText.isEmpty() || inaguracionText.isEmpty() || patrimonioText.isEmpty() || metroText.isEmpty() || direccionText.isEmpty() || descripcionText.isEmpty() || distritoText.isEmpty())) {
                    HashMap<String, Object> datos = new HashMap<>();
                    datos.put("nombre", nombreText);
                    datos.put("inaguracion", inaguracionText);
                    datos.put("patrimonio", patrimonioText);
                    datos.put("geo", new GeoPoint(latitude, longitude));
                    datos.put("metro", metroText);
                    datos.put("direccion", direccionText);
                    datos.put("descripcion", descripcionText);
                    datos.put("distrito", distritoText);

                    if (!distritoText.isEmpty()) {
                        String[] palabras = distritoText.split("\\s+");
                        if (palabras.length >= 2) {
                            dist = palabras[1].toLowerCase();
                        }
                    }

                    getCount(count -> {
                        if (count >= 0) {
                            db.collection(dist).document(String.valueOf(count+1)).set(datos);
                        }
                    });
                } else {
                    /**if (coordenadas_latText.isEmpty()) {
                        coordenadas_latInputLayout.setHint("");
                        coordenadas_latEditText.setBackground(redBorderDrawable);
                        coordenadas_latEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    coordenadas_latEditText.setBackground(defaultBorderDrawable);
                                    coordenadas_latInputLayout.setHint(R.string.email);
                                }
                            }
                        });
                    }**/
                }
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

    public void getCount(final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection("favorites").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size();
                countCallback.onCallback(count);
            } else {
                Log.e("FirestoreData", "Error getting document count: " + Objects.requireNonNull(task.getException()).getMessage());
                countCallback.onCallback(-1); // Indicates an error
            }
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