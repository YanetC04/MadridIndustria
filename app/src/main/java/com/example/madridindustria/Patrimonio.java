package com.example.madridindustria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrimonio);

        toolbar = findViewById(R.id.toolbar);
        toolbarCollapse = findViewById(R.id.toolbarCollapse);
        imagen = findViewById(R.id.imagen);
        boton = findViewById(R.id.boton);

        // ESTABLECEMOS ESTE TOOLBAR COMO PREDETERMINADO
        setSupportActionBar(toolbar);

        // ESTABLECEMOS EL TITULO
        toolbarCollapse.setTitle("Estación Atocha");

        // ESTABLECEMOS EL COLOR
        toolbarCollapse.setExpandedTitleColor(getResources().getColor(R.color.red));

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

        // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
        Glide.with(this)
                .load("https://yaldahpublishing.com/wp-content/uploads/2021/04/Como-ir-de-Atocha-a-Chamartin3-min.jpg")
                .into(imagen);

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
    }

    // NO FUNCIONA 
    private void abrirUbicacion() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=plaza+eliptica"));
        startActivity(intent);
    }
}