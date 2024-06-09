package com.proyectointegrador.madridindustria;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Busqueda extends AppCompatActivity {
    private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde", "retiro", "tetuan", "fuencarral", "vallecas", "barajas", "hortaleza", "latina", "salamanca"};
    private ImageView buscar;
    private EditText nombre;
    private ImageView imagenId;
    private LinearLayout linearLayout;
    private String imagenV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        buscar = findViewById(R.id.imagen);
        nombre = findViewById(R.id.nombre);
        linearLayout = findViewById(R.id.linear);

        buscar.setOnClickListener(v -> realizarBusqueda());

        nombre.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                realizarBusqueda();
                return true;
            }
            return false;
        });
    }

    private void realizarBusqueda() {
        linearLayout.removeAllViews();

        for (String dist : distritos) {
            FirebaseFirestore.getInstance().collection(dist)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String nombreF = document.getString("nombre");
                                    String parteDelNombreABuscar = nombre.getText().toString();

                                    if (nombreF.toLowerCase().contains(parteDelNombreABuscar.toLowerCase())) {
                                        agregarFila(document, document.getId(), dist);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void agregarFila(QueryDocumentSnapshot queryDocumentSnapshot, String documento, String coleccion) {
        View favoriteCard = LayoutInflater.from(Busqueda.this).inflate(R.layout.favorite_card, null);

        ImageView imagen = favoriteCard.findViewById(R.id.imagen);
        imagenId = imagen;
        TextView nombre = favoriteCard.findViewById(R.id.nombre);
        TextView inaguracion = favoriteCard.findViewById(R.id.inaguracion);
        TextView patrimonio = favoriteCard.findViewById(R.id.patrimonio);
        TextView metro = favoriteCard.findViewById(R.id.metro);
        TextView direccion = favoriteCard.findViewById(R.id.direccion);
        imagenV = queryDocumentSnapshot.getString("imagen");

        // ESTABLECER INFORMACION
        if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esEspanol", true)){
            nombre.setText(queryDocumentSnapshot.getString("nombre"));
            inaguracion.setText(queryDocumentSnapshot.getString("inaguracion"));
            patrimonio.setText(queryDocumentSnapshot.getString("patrimonio"));
            metro.setText(queryDocumentSnapshot.getString("metro"));
            direccion.setText(queryDocumentSnapshot.getString("direccion"));
        } else {
            traducirTexto(nombre, queryDocumentSnapshot.getString("nombre"));
            traducirTexto(inaguracion, queryDocumentSnapshot.getString("inaguracion"));
            traducirTexto(patrimonio, queryDocumentSnapshot.getString("patrimonio"));
            traducirTexto(metro, queryDocumentSnapshot.getString("metro"));
            traducirTexto(direccion, queryDocumentSnapshot.getString("direccion"));
        }

        Glide.with(this)
                .load(queryDocumentSnapshot.getString("imagen"))
                .centerCrop()
                .into(imagen);

        linearLayout.addView(favoriteCard);

        nombre.setOnClickListener(v -> {
            startActivity(new Intent(Busqueda.this, Patrimonio.class).putExtra("collection", coleccion).putExtra("document", documento).putExtra("source", getIntent().getStringExtra("source")));
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

    @Override
    protected void onStop() {
        super.onStop();
        if (imagenId != null) {
            Glide.with(this).clear(imagenId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imagenId != null) {
            Glide.with(this)
                    .load(imagenV)
                    .into(imagenId);
        }
    }
}