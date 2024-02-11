package proyectointegrador.madridindustria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Objects;

public class Patrimonio extends AppCompatActivity {

    private String nombreText, inaguracionText, patrimonioText, metroText, direccionText, imagenText, numero;
    private CollapsingToolbarLayout toolbarCollapse;
    private ImageView imagen;
    private FloatingActionButton boton;
    private boolean heart = true;
    private TextView direccion, inaguracion, metro, descripcion, patrimonio;
    private Drawable heartDrawable;
    private Drawable heartFillDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrimonio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarCollapse = findViewById(R.id.toolbarCollapse);
        imagen = findViewById(R.id.imagen);
        boton = findViewById(R.id.boton);
        inaguracion = findViewById(R.id.inaguracion);
        patrimonio = findViewById(R.id.patrimonio);
        metro = findViewById(R.id.metro);
        direccion = findViewById(R.id.direccion);
        descripcion = findViewById(R.id.descripcion);
        heartDrawable = ContextCompat.getDrawable(this, R.drawable.heart);
        heartFillDrawable = ContextCompat.getDrawable(this, R.drawable.heart_fill);

        // ESTABLECEMOS ESTE TOOLBAR COMO PREDETERMINADO
        setSupportActionBar(toolbar);
        toolbarCollapse.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));

        // Verifica si el tema actual es oscuro
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            toolbarCollapse.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        } else {
            toolbarCollapse.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        }

        // ESTABLECEMOS EL FONTFAMILY Y GROSOR
        Typeface boldTypeface = Typeface.create(ResourcesCompat.getFont(this, R.font.inter_bold), Typeface.BOLD);
        toolbarCollapse.setExpandedTitleTypeface(boldTypeface);

        // ESTABLECEMOS CUANDO APARECE O DESAPARECE EL BOTON
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (verticalOffset==0){
                boton.show();
            } else {
                boton.hide();
            }
        });

        // ESTABLECEMOS EL ESTADO DEL BOTON
        getCount(count -> {
            if (count >= 0) {
                FirebaseFirestore.getInstance().collection("favorites").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Obtener el número de referencia del documento
                            String numeroDeReferencia = document.getReference().getId();

                            new FirestoreDatabase("favorites", numeroDeReferencia, firestoreDatabase -> {
                                String nombre = firestoreDatabase.getNombre();
                                String like = firestoreDatabase.getLike();

                                Log.e("dentro", nombre + " " + like);

                                if (nombre != null && like != null && nombre.equals(nombreText) && like.equals("true")) {
                                    Log.e("Nombre", nombre);
                                    boton.setImageDrawable(heartFillDrawable);
                                    heart = false;
                                } else {
                                    boton.setImageDrawable(heartDrawable);
                                    heart = true;
                                }
                            });
                        }
                    }
                });
            }
        });

        // BOTON FLOTANTE
        boton.setOnClickListener(v -> getCount(count -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            HashMap<String, Object> datos = new HashMap<>();

            if (count >= 0) {
                if (heart) {
                    // CORAZON LLENO
                    Log.e("Click", "true");

                    datos.put("nombre", nombreText);
                    datos.put("like", "true");
                    datos.put("inaguracion", inaguracionText);
                    datos.put("patrimonio", patrimonioText);
                    datos.put("metro", metroText);
                    datos.put("direccion", direccionText);
                    datos.put("imagen", imagenText);

                    // Agregar un nuevo documento a la colección "favorites"
                    db.collection("favorites")
                            .add(datos)
                            .addOnSuccessListener(documentReference -> {
                                // Obtener el número de referencia del nuevo documento
                                numero = documentReference.getId();
                                Log.e("Nuevo Documento", "Número de referencia: " + numero);

                                // Actualizar el documento recién agregado con el número de referencia
                                db.collection("favorites").document(numero).update("id_patrimonio", numero)
                                        .addOnSuccessListener(aVoid -> Log.e("Firestore", "Número de referencia actualizado correctamente"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar el número de referencia", e));
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al agregar el nuevo documento", e));


                    Log.e("Estado", String.valueOf(heart));
                    // INVERTIR ESTADO
                    heart = !heart;
                    Log.e("Estado", String.valueOf(heart));
                    boton.setImageDrawable(heartFillDrawable);
                } else {
                    // CORAZON VACIO
                    Log.e("Click", "false");
                    Log.e("count", String.valueOf(count));

                    // Obtener una referencia a la colección
                    CollectionReference coleccion = db.collection("favorites");

                    coleccion.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtener el número de referencia del documento
                                String numeroDeReferencia = document.getReference().getId();
                                Log.e("numRef", numeroDeReferencia);

                                new FirestoreDatabase("favorites", numeroDeReferencia, firestoreDatabase -> {
                                    String nombre = firestoreDatabase.getNombre();

                                    Log.e("Click", nombre);
                                    if (nombre.equals(nombreText)) {
                                        db.collection("favorites").document(numeroDeReferencia).delete()
                                                .addOnCompleteListener(deleteTask -> {
                                                    if (deleteTask.isSuccessful()) {
                                                        Log.e("Borrar", nombre);
                                                    } else {
                                                        Log.e("Borrar", "Error al borrar documento");
                                                    }
                                                });
                                    }
                                });
                            }
                        } else {
                            Log.e("Firestore", "Error al obtener la colección de favoritos", task.getException());
                        }
                    });

                    boton.setImageDrawable(heartDrawable);
                    Log.e("Estado", String.valueOf(heart));
                    // INVERTIR ESTADO
                    heart = !heart;
                    Log.e("Estado", String.valueOf(heart));
                }
            }
        }));

        // BASE DE DATOS
        new FirestoreDatabase(getIntent().getStringExtra("collection"), getIntent().getStringExtra("document"), firestoreDatabase -> {
            nombreText = firestoreDatabase.getNombre();
            inaguracionText = firestoreDatabase.getInaguracion();
            patrimonioText = firestoreDatabase.getPatrimonio();
            metroText = firestoreDatabase.getMetro();
            direccionText = firestoreDatabase.getDireccion();
            imagenText = firestoreDatabase.getImagen();

            // ESTABLECEMOS EL TITULO
            toolbarCollapse.setTitle(nombreText);

            // ESTABLECEMOS LA INFORMACION
            inaguracion.setText(inaguracionText);
            patrimonio.setText(patrimonioText);
            metro.setText(metroText);
            direccion.setText(direccionText);
            descripcion.setText(firestoreDatabase.getDescripcion());

            // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
            if (!isDestroyed()) {
                Glide.with(Patrimonio.this)
                        .load(imagenText)
                        .into(imagen);
            }
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

    @Override
    protected void onStop() {
        super.onStop();
        Glide.with(this).clear(imagen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}