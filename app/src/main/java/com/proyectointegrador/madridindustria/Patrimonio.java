package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Patrimonio extends AppCompatActivity {

    private String nombreText, inaguracionText, patrimonioText, metroText, direccionText, distritoText, imagenText;
    private CollapsingToolbarLayout toolbarCollapse;
    private ImageView imagen;
    private FloatingActionButton boton;
    private boolean heart = true;
    private TextView direccion, inaguracion, metro, descripcion, patrimonio;
    private Drawable heartDrawable, heartFillDrawable;
    private final localDB localDB = new localDB(this);
    private RatingBar ratingBar;
    private TextView rating_value;
    private TextView total_reviews;
    private ProgressBar progressBar5;
    private ProgressBar progressBar4;
    private ProgressBar progressBar3;
    private ProgressBar progressBar2;
    private ProgressBar progressBar1;
    private TextView percentage5;
    private TextView percentage4;
    private TextView percentage3;
    private TextView percentage2;
    private TextView percentage1;

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
        LinearLayout dir = findViewById(R.id.dir);

        // REDIRIGE AL MAPA
        dir.setOnClickListener(v -> startActivity(new Intent(Patrimonio.this, Map.class).putExtra("source", getIntent().getStringExtra("source"))));

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

        // OBTENER DATOS DE FIREBASE
        new FirestoreDatabase(getIntent().getStringExtra("collection"), getIntent().getStringExtra("document"), firestoreDatabase -> {
            nombreText = firestoreDatabase.getNombre();
            inaguracionText = firestoreDatabase.getInaguracion();
            patrimonioText = firestoreDatabase.getPatrimonio();
            metroText = firestoreDatabase.getMetro();
            direccionText = firestoreDatabase.getDireccion();
            distritoText = firestoreDatabase.getDistrito();
            imagenText = firestoreDatabase.getImagen();

            // ESTABLECEMOS EL ESTADO DEL BOTON
            SQLiteDatabase db = localDB.getWritableDatabase();
            Cursor cursor = db.query("favorites", new String[]{"love"}, "nombre = ?", new String[]{nombreText}, null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();

            if (exists){
                boton.setImageDrawable(heartFillDrawable);
                heart = false;
            } else {
                boton.setImageDrawable(heartDrawable);
                heart = true;
            }

            // ESTABLECEMOS LA INFORMACION
            if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esEspanol", true)){
                inaguracion.setText(inaguracionText);
                patrimonio.setText(patrimonioText);
                metro.setText(metroText);
                direccion.setText(direccionText);
                descripcion.setText(firestoreDatabase.getDescripcion());

                // ESTABLECEMOS EL TITULO
                toolbarCollapse.setTitle(nombreText);
            } else {
                traducirTexto(inaguracion, inaguracionText);
                traducirTexto(patrimonio, patrimonioText);
                traducirTexto(metro, metroText);
                traducirTexto(direccion, direccionText);
                traducirTexto(descripcion, firestoreDatabase.getDescripcion());
                Traductor.traducirTexto(nombreText, new Traductor.OnTranslationComplete() {
                    @Override
                    public void onTranslationComplete(String translatedText) {
                        toolbarCollapse.setTitle(translatedText);
                    }

                    @Override
                    public void onTranslationFailed(String errorMessage) {

                    }
                }, this);
            }

            // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
            Glide.with(Patrimonio.this)
                    .load(imagenText)
                    .into(imagen);
        });

        // ESTABLECEMOS CUANDO APARECE O DESAPARECE EL BOTON
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (verticalOffset == 0) {
                boton.show();
            } else {
                boton.hide();
            }
        });

        // BOTON FLOTANTE
        boton.setOnClickListener(v -> {
            if (heart) {
                // CORAZON LLENO
                agregarPatrimonio(nombreText, inaguracionText, patrimonioText, metroText, direccionText, distritoText, imagenText);
                boton.setImageDrawable(heartFillDrawable);
            } else {
                // CORAZON VACIO
                eliminarPatrimonio();
                boton.setImageDrawable(heartDrawable);
            }

            heart = !heart;
        });

        // Calificaciones
        ratingBar = findViewById(R.id.ratingBar);
        rating_value = findViewById(R.id.rating_value);
        total_reviews = findViewById(R.id.total_reviews);
        progressBar5 = findViewById(R.id.progressBar5);
        progressBar4 = findViewById(R.id.progressBar4);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar1 = findViewById(R.id.progressBar1);
        percentage5 = findViewById(R.id.percentage5);
        percentage4 = findViewById(R.id.percentage4);
        percentage3 = findViewById(R.id.percentage3);
        percentage2 = findViewById(R.id.percentage2);
        percentage1 = findViewById(R.id.percentage1);

        // Cargar estado de la calificación
        ratingBar.setRating(0);

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                guardarCalificacionEnFirestore(nombreText, rating);
            }
        });

        cargarCalificacionesDeFirestore(nombreText);
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

    private void agregarPatrimonio(String nombre, String inaguracion, String patrimonio, String metro, String direccion, String distrito, String imagen) {
        SQLiteDatabase db = localDB.getWritableDatabase();

        // CREA UN NUEVO REGISTRO DE PATRIMONIO
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("love", "true");
        values.put("inaguracion", inaguracion);
        values.put("patrimonio", patrimonio);
        values.put("metro", metro);
        values.put("direccion", direccion);
        values.put("distrito", distrito);
        values.put("imagen", imagen);

        // INSERTAR EL NUEVO REGISTRO
        db.insert("favorites", null, values);

        db.close();
    }

    private void eliminarPatrimonio(){
        SQLiteDatabase db = localDB.getWritableDatabase();
        db.delete("favorites", "nombre = ?", new String[]{nombreText});
        db.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.with(this).clear(imagen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Glide.with(Patrimonio.this)
                .load(imagenText)
                .into(imagen);
    }

    private void guardarCalificacionEnFirestore(String nombrePatrimonio, float calificacion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference valoracionesRef = db.collection("valoraciones");

        HashMap<String, Object> valoracion = new HashMap<>();
        valoracion.put("nombrePatrimonio", nombrePatrimonio);
        valoracion.put("calificacion", calificacion);

        valoracionesRef.add(valoracion).addOnSuccessListener(documentReference -> {
            cargarCalificacionesDeFirestore(nombrePatrimonio);
        });
    }

    private void cargarCalificacionesDeFirestore(String nombrePatrimonio) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference valoracionesRef = db.collection("valoraciones");

        valoracionesRef.whereEqualTo("nombrePatrimonio", nombrePatrimonio).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    float sumaCalificaciones = 0;
                    int totalCalificaciones = querySnapshot.size();
                    int[] calificacionesContador = new int[5];

                    for (DocumentSnapshot document : querySnapshot) {
                        Double calificacion = document.getDouble("calificacion");
                        if (calificacion != null) {
                            sumaCalificaciones += calificacion;
                            int index = calificacion.intValue() - 1;
                            if (index >= 0 && index < calificacionesContador.length) {
                                calificacionesContador[index]++;
                            }
                        }
                    }

                    float promedioCalificaciones = sumaCalificaciones / totalCalificaciones;
                    rating_value.setText(String.format(Locale.getDefault(), "%.1f", promedioCalificaciones));
                    total_reviews.setText(String.format(Locale.getDefault(), "%d opiniones", totalCalificaciones));

                    actualizarProgressBar(calificacionesContador, totalCalificaciones);
                }
            }
        });
    }

    private void actualizarProgressBar(int[] calificacionesContador, int totalCalificaciones) {
        if (totalCalificaciones == 0) return;

        int porcentaje1 = (calificacionesContador[0] * 100) / totalCalificaciones;
        int porcentaje2 = (calificacionesContador[1] * 100) / totalCalificaciones;
        int porcentaje3 = (calificacionesContador[2] * 100) / totalCalificaciones;
        int porcentaje4 = (calificacionesContador[3] * 100) / totalCalificaciones;
        int porcentaje5 = (calificacionesContador[4] * 100) / totalCalificaciones;

        progressBar1.setProgress(porcentaje1);
        progressBar2.setProgress(porcentaje2);
        progressBar3.setProgress(porcentaje3);
        progressBar4.setProgress(porcentaje4);
        progressBar5.setProgress(porcentaje5);

        percentage1.setText(String.format(Locale.getDefault(), "%d%%", porcentaje1));
        percentage2.setText(String.format(Locale.getDefault(), "%d%%", porcentaje2));
        percentage3.setText(String.format(Locale.getDefault(), "%d%%", porcentaje3));
        percentage4.setText(String.format(Locale.getDefault(), "%d%%", porcentaje4));
        percentage5.setText(String.format(Locale.getDefault(), "%d%%", porcentaje5));
    }
}
