package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Patrimonio extends AppCompatActivity {

    private String nombreText, inaguracionText, patrimonioText, metroText, direccionText, distritoText, imagenText;
    private CollapsingToolbarLayout toolbarCollapse;
    private ImageView imagen;
    private FloatingActionButton boton;
    private boolean heart = true;
    private TextView direccion, inaguracion, metro, descripcion, patrimonio;
    private Drawable heartDrawable;
    private Drawable heartFillDrawable;
    private final localDB localDB = new localDB(this);

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

            if (exists){
                boton.setImageDrawable(heartFillDrawable);
                heart = false;
            } else {
                boton.setImageDrawable(heartDrawable);
                heart = true;
            }

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
        });

        // ESTABLECEMOS CUANDO APARECE O DESAPARECE EL BOTON
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (verticalOffset==0){
                boton.show();
            } else {
                boton.hide();
            }
        });

        // BOTON FLOTANTE
        boton.setOnClickListener(v -> {
            if (heart) {
                // CORAZON LLENO
                Log.e("Click", "true");
                agregarPatrimonio(nombreText, inaguracionText, patrimonioText, metroText, direccionText, distritoText, imagenText);
                boton.setImageDrawable(heartFillDrawable);
            } else {
                // CORAZON VACIO
                Log.e("Click", "false");
                eliminarPatrimonio();
                boton.setImageDrawable(heartDrawable);
            }

            Log.e("Estado", String.valueOf(heart));
            heart = !heart;
            Log.e("Estado", String.valueOf(heart));
        });
    }

    private void agregarPatrimonio(String nombre, String inaguracion, String patrimonio, String metro, String direccion, String distrito, String imagen) {
        SQLiteDatabase db = localDB.getWritableDatabase();

        // Crea un nuevo registro de patrimonio
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("love", "true");
        values.put("inaguracion", inaguracion);
        values.put("patrimonio", patrimonio);
        values.put("metro", metro);
        values.put("direccion", direccion);
        values.put("distrito", distrito);
        values.put("imagen", imagen);

        // Inserta el nuevo registro en la tabla patrimonio
        db.insert("favorites", null, values);

        db.close();
    }

    private void eliminarPatrimonio(){
        SQLiteDatabase db = localDB.getWritableDatabase();
        db.delete("favorites", "nombre = ?", new String[]{nombreText});
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.with(this).clear(imagen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
        Glide.with(Patrimonio.this)
                .load(imagenText)
                .into(imagen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}