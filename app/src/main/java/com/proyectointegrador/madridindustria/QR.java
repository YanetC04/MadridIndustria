package com.proyectointegrador.madridindustria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class QR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        // CONFIGURA ADAPTER
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // VIEWPAGER
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        // TABLAYOUT
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(getResources().getString(R.string.q));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.n));

        // ATRAS
        ImageView atras = findViewById(R.id.atras);
        atras.setOnClickListener(view -> {
            String source = getIntent().getStringExtra("source"); 
            Intent intent;

            if (source != null && source.equalsIgnoreCase("abierto")) {
                intent = new Intent(QR.this, Profile.class).putExtra("source", source);
            } else {
                intent = new Intent(QR.this, Profile.class).putExtra("source", source);
            }

            // INICIAR LA ACTIVIDAD SEGÚN EL INTENTO
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    // EVITAR QUE LA ACTIVIDAD VUELVA ATRÁS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // EVITAR QUE MainActivity VUELVA ATRÁS A Splash.java
        // NO LLAMAR AL super.onBackPressed();
    }
}