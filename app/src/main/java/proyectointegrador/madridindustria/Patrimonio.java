package proyectointegrador.madridindustria;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Patrimonio extends AppCompatActivity {

    private String nombreText, inaguracionText, patrimonioText, metroText, direccionText, imagenText, numero;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarCollapse;
    private Typeface boldTypeface;
    private ImageView imagen;
    private FloatingActionButton boton;
    private boolean heart = true;
    private TextView direccion, inaguracion, metro, descripcion, patrimonio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrimonio);

        toolbar = findViewById(R.id.toolbar);
        toolbarCollapse = findViewById(R.id.toolbarCollapse);
        imagen = findViewById(R.id.imagen);
        boton = findViewById(R.id.boton);
        inaguracion = findViewById(R.id.inaguracion);
        patrimonio = findViewById(R.id.patrimonio);
        metro = findViewById(R.id.metro);
        direccion = findViewById(R.id.direccion);
        descripcion = findViewById(R.id.descripcion);

        // ESTABLECEMOS ESTE TOOLBAR COMO PREDETERMINADO
        setSupportActionBar(toolbar);
        toolbarCollapse.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));

        // Verifica si el tema actual es oscuro
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            toolbarCollapse.setExpandedTitleColor(getResources().getColor(R.color.white));
        } else {
            toolbarCollapse.setExpandedTitleColor(getResources().getColor(R.color.white));
        }

        // ESTABLECEMOS EL FONTFAMILY Y GROSOR
        boldTypeface = Typeface.create(ResourcesCompat.getFont(this, R.font.inter_bold), Typeface.BOLD);
        toolbarCollapse.setExpandedTitleTypeface(boldTypeface);

        // ESTABLECEMOS CUANDO APARECE O DESAPARECE EL BOTON
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset==0){
                    boton.show();
                } else {
                    boton.hide();
                }
            }
        });

        // ESTABLECEMOS EL ESTADO DEL BOTON
        getCount(new CountCallback() {
            @Override
            public void onCallback(int count) {
                if (count >= 0) {
                    FirebaseFirestore.getInstance().collection("favorites").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtener el número de referencia del documento
                                String numeroDeReferencia = document.getReference().getId();

                                new FirestoreDatabase("favorites", numeroDeReferencia, new FirestoreCallback() {
                                    @Override
                                    public void onCallback(FirestoreDatabase firestoreDatabase) {
                                        String nombre = firestoreDatabase.getNombre();
                                        String like = firestoreDatabase.getLike();

                                        Log.e("dentro", nombre + " " + like);

                                        if (nombre != null && like != null && nombre.equals(nombreText) && like.equals("true")) {
                                            Log.e("Nombre", nombre);
                                            boton.setImageDrawable(getDrawable(R.drawable.heart_fill));
                                            heart = false;
                                        } else {
                                            boton.setImageDrawable(getDrawable(R.drawable.heart));
                                            heart = true;
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        // BOTON FLOTANTE
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCount(new CountCallback() {
                    @Override
                    public void onCallback(int count) {
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
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.e("Firestore", "Número de referencia actualizado correctamente");
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("Firestore", "Error al actualizar el número de referencia", e);
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Error al agregar el nuevo documento", e);
                                        });


                                Log.e("Estado", String.valueOf(heart));
                                // INVERTIR ESTADO
                                heart = !heart;
                                Log.e("Estado", String.valueOf(heart));
                                boton.setImageDrawable(getDrawable(R.drawable.heart_fill));
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

                                            new FirestoreDatabase("favorites", numeroDeReferencia, new FirestoreCallback() {
                                                @Override
                                                public void onCallback(FirestoreDatabase firestoreDatabase) {
                                                    String nombre = firestoreDatabase.getNombre();

                                                    Log.e("Click", nombre);
                                                    if (nombre != null && nombre.equals(nombreText)) {
                                                        db.collection("favorites").document(numeroDeReferencia).delete()
                                                                .addOnCompleteListener(deleteTask -> {
                                                                    if (deleteTask.isSuccessful()) {
                                                                        Log.e("Borrar", nombre);
                                                                    } else {
                                                                        Log.e("Borrar", "Error al borrar documento");
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Log.e("Firestore", "Error al obtener la colección de favoritos", task.getException());
                                    }
                                });

                                boton.setImageDrawable(getDrawable(R.drawable.heart));
                                Log.e("Estado", String.valueOf(heart));
                                // INVERTIR ESTADO
                                heart = !heart;
                                Log.e("Estado", String.valueOf(heart));
                            }
                        }
                    }
                });
            }
        });

        // BASE DE DATOS
        new FirestoreDatabase(getIntent().getStringExtra("collection"), getIntent().getStringExtra("document"), new FirestoreCallback() {
            @Override
            public void onCallback(FirestoreDatabase firestoreDatabase) {
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
                Glide.with(Patrimonio.this)
                        .load(imagenText)
                        .into(imagen);
            }
        });


    }

    public void getCount(final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection("favorites").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = task.getResult().size();
                    countCallback.onCallback(count);
                } else {
                    Log.e("FirestoreData", "Error getting document count: " + task.getException().getMessage());
                    countCallback.onCallback(-1); // Indicates an error
                }
            }
        });
    }
}