package com.proyectointegrador.madridindustria;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Objects;

public class Add extends AppCompatActivity {

    private EditText nombreEditText, inaguracionEditText, patrimonioEditText,coordenadas_latEditText, coordenadas_lonEditText,  metroEditText, direccionEditText, descripcionEditText, imagenEditText;
    private TextInputLayout nombreInputLayout, inaguracionInputLayout, patrimonioInputLayout, coordenadas_latInputLayout, coordenadas_lonInputLayout, metroInputLayout, direccionInputLayout, imagenInputLayout, descripcionInputLayout;
    private String nombreText, inaguracionText, patrimonioText,coordenadas_latText, coordenadas_lonText,  metroText, direccionText, descripcionText, distritoText, imagenText;
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
        imagenEditText = findViewById(R.id.imagen);
        nombreEditText = findViewById(R.id.nombre);
        inaguracionEditText = findViewById(R.id.inaguracion);
        patrimonioEditText = findViewById(R.id.patrimonio);
        coordenadas_latEditText = findViewById(R.id.coordenadas_lat);
        coordenadas_lonEditText = findViewById(R.id.coordenadas_lon);
        metroEditText = findViewById(R.id.metro);
        distrito = findViewById(R.id.distrito);
        direccionEditText = findViewById(R.id.direccion);
        descripcionEditText = findViewById(R.id.descripcion);
        imagenInputLayout = findViewById(R.id.input_imagen);
        nombreInputLayout = findViewById(R.id.input_nombre);
        inaguracionInputLayout = findViewById(R.id.input_inaguracion);
        patrimonioInputLayout = findViewById(R.id.input_patrimonio);
        coordenadas_latInputLayout = findViewById(R.id.input_coordenadas_lat);
        coordenadas_lonInputLayout = findViewById(R.id.input_coordenadas_lon);
        metroInputLayout = findViewById(R.id.input_metro);
        direccionInputLayout = findViewById(R.id.input_direccion);
        descripcionInputLayout = findViewById(R.id.input_descripcion);
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
                imagenText = imagenEditText.getText().toString();
                nombreText = nombreEditText.getText().toString();
                inaguracionText = inaguracionEditText.getText().toString();
                patrimonioText = patrimonioEditText.getText().toString();
                coordenadas_latText = coordenadas_latEditText.getText().toString();
                coordenadas_lonText = coordenadas_lonEditText.getText().toString();
                metroText = metroEditText.getText().toString();
                direccionText = direccionEditText.getText().toString();
                descripcionText = descripcionEditText.getText().toString();
                distrito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                        if (position!=0) {
                            distrito.setBackground(defaultBorderDrawable);
                            distritoText = distrito.getSelectedItem().toString();
                            if (!distritoText.isEmpty()) {
                                String[] palabras = distritoText.split("\\s+");
                                if (palabras.length >= 2) {
                                    String distritoNombre = palabras[1].toLowerCase();
                                    distritoNombre = quitarAcentos(distritoNombre);

                                    if (distritoNombre.equals("san")) {
                                        dist = "sanblas";
                                    } else {
                                        dist = distritoNombre;
                                    }
                                }
                            }
                        } else
                            distritoText="";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        showErrorDialog("Seleccione un distrito.");
                    }
                });

                double latitude = 0.0;
                double longitude = 0.0;

                if (!coordenadas_latText.isEmpty() && !coordenadas_lonText.isEmpty()) {
                    try {
                        latitude = Double.parseDouble(coordenadas_latText);
                        longitude = Double.parseDouble(coordenadas_lonText);
                    } catch (NumberFormatException e) {
                        showErrorDialog("Formato inválido para latitud/longitud");
                        return;
                    }
                } else {
                    if (coordenadas_latText.isEmpty()) {
                        coordenadas_latInputLayout.setHint(R.string.lat);
                        coordenadas_latEditText.setBackground(redBorderDrawable);
                        coordenadas_latEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    coordenadas_latEditText.setBackground(defaultBorderDrawable);
                                    coordenadas_latInputLayout.setHint(R.string.lat);
                                }
                            }
                        });
                    }

                    if (coordenadas_lonText.isEmpty()) {
                        coordenadas_lonInputLayout.setHint(R.string.lon);
                        coordenadas_lonEditText.setBackground(redBorderDrawable);
                        coordenadas_lonEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    coordenadas_lonEditText.setBackground(defaultBorderDrawable);
                                    coordenadas_lonInputLayout.setHint(R.string.lon);
                                }
                            }
                        });
                    }
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (!imagenText.isEmpty() && !nombreText.isEmpty() && !inaguracionText.isEmpty() && !patrimonioText.isEmpty() && !metroText.isEmpty() && !direccionText.isEmpty() && !descripcionText.isEmpty() && !dist.isEmpty()) {
                    HashMap<String, Object> datos = new HashMap<>();
                    datos.put("imagen", imagenText);
                    datos.put("nombre", nombreText);
                    datos.put("inaguracion", inaguracionText);
                    datos.put("patrimonio", patrimonioText);
                    if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
                        datos.put("geo", new GeoPoint(latitude, longitude));
                    } else {
                        showErrorDialog("Valor inválido para latitud/longitud");
                        return;
                    }
                    datos.put("metro", metroText);
                    datos.put("direccion", direccionText);
                    datos.put("descripcion", descripcionText);
                    datos.put("distrito", distritoText);

                    db.collection(dist).whereEqualTo("nombre", nombreText).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                showErrorDialog("El patrimonio ya existe.");
                            } else {
                                db.collection(dist).document().set(datos);
                                new AlertDialog.Builder(Add.this)
                                        .setTitle("Success")
                                        .setMessage("Data correctamente enviada.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            // Reset input fields
                                            imagenEditText.getText().clear();
                                            nombreEditText.getText().clear();
                                            inaguracionEditText.getText().clear();
                                            patrimonioEditText.getText().clear();
                                            coordenadas_latEditText.getText().clear();
                                            coordenadas_lonEditText.getText().clear();
                                            metroEditText.getText().clear();
                                            direccionEditText.getText().clear();
                                            descripcionEditText.getText().clear();
                                            distrito.setSelection(0);
                                            dist="";
                                        })
                                        .show();
                            }
                        } else {
                            Log.e("FirestoreData", "Error checking for document existence: " + task.getException().getMessage());
                        }
                    });
                } else {
                    if(dist.isEmpty()){
                        distrito.setBackground(redBorderDrawable);
                    }

                    if (imagenText.isEmpty()) {
                        imagenInputLayout.setHint(R.string.ima);
                        imagenEditText.setBackground(redBorderDrawable);
                        imagenEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    imagenEditText.setBackground(defaultBorderDrawable);
                                    imagenInputLayout.setHint(R.string.ima);
                                }
                            }
                        });
                    }

                    if (nombreText.isEmpty()) {
                        nombreInputLayout.setHint(R.string.nom);
                        nombreEditText.setBackground(redBorderDrawable);
                        nombreEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    nombreEditText.setBackground(defaultBorderDrawable);
                                    nombreInputLayout.setHint(R.string.nom);
                                }
                            }
                        });
                    }
                    if (inaguracionText.isEmpty()) {
                        inaguracionInputLayout.setHint(R.string.ina);
                        inaguracionEditText.setBackground(redBorderDrawable);
                        inaguracionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    inaguracionEditText.setBackground(defaultBorderDrawable);
                                    inaguracionInputLayout.setHint(R.string.ina);
                                }
                            }
                        });
                    }

                    if (patrimonioText.isEmpty()) {
                        patrimonioInputLayout.setHint(R.string.pat);
                        patrimonioEditText.setBackground(redBorderDrawable);
                        patrimonioEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    patrimonioEditText.setBackground(defaultBorderDrawable);
                                    patrimonioInputLayout.setHint(R.string.pat);
                                }
                            }
                        });
                    }

                    if (metroText.isEmpty()) {
                        metroInputLayout.setHint(R.string.met);
                        metroEditText.setBackground(redBorderDrawable);
                        metroEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    metroEditText.setBackground(defaultBorderDrawable);
                                    metroInputLayout.setHint(R.string.met);
                                }
                            }
                        });
                    }

                    if (direccionText.isEmpty()) {
                        direccionInputLayout.setHint(R.string.dir);
                        direccionEditText.setBackground(redBorderDrawable);
                        direccionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    direccionEditText.setBackground(defaultBorderDrawable);
                                    direccionInputLayout.setHint(R.string.dir);
                                }
                            }
                        });
                    }

                    if (descripcionText.isEmpty()) {
                        descripcionInputLayout.setHint(R.string.des);
                        descripcionEditText.setBackground(redBorderDrawable);
                        descripcionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    descripcionEditText.setBackground(defaultBorderDrawable);
                                    descripcionInputLayout.setHint(R.string.des);
                                }
                            }
                        });
                    }
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

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }


    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}