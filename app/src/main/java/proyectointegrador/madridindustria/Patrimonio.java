package proyectointegrador.madridindustria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Patrimonio extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarCollapse;
    private Typeface boldTypeface;
    private ImageView imagen;
    private FloatingActionButton boton;
    private boolean heart = false;
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



        // BOTON FLOTANTE
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heart) {
                    // CORAZON VACIO
                    boton.setImageResource(R.drawable.heart);
                } else {
                    // CORAZON LLENO
                    boton.setImageResource(R.drawable.heart_fill);
                }

                // INVERTIR ESTADO
                heart = !heart;
            }
        });

        // BASE DE DATOS
        FirestoreDatabase firestoreDatabase = new FirestoreDatabase(getIntent().getStringExtra("collection"), getIntent().getStringExtra("document"), new FirestoreCallback() {
            @Override
            public void onCallback(FirestoreDatabase firestoreDatabase) {
                // ESTABLECEMOS EL TITULO
                toolbarCollapse.setTitle(firestoreDatabase.getNombre());

                // ESTABLECEMOS LA INFORMACION
                inaguracion.setText(firestoreDatabase.getInaguracion());
                patrimonio.setText(firestoreDatabase.getPatrimonio());
                metro.setText(firestoreDatabase.getMetro());
                direccion.setText(firestoreDatabase.getDireccion());
                descripcion.setText(firestoreDatabase.getDescripcion());

                // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
                Glide.with(Patrimonio.this)
                        .load(firestoreDatabase.getImagen())
                        .into(imagen);
            }
        });
    }
}