package com.proyectointegrador.madridindustria;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import proyectointegrador.madridindustria.MainActivity;

public class Splash extends AppCompatActivity {

    private ImageView logo;
    private TextView adrid, industria;
    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);

        adrid = findViewById(R.id.adrid);
        industria = findViewById(R.id.industria);

        Animation moveAnimation = AnimationUtils.loadAnimation(this, R.anim.centrar);
        // Aplicar la animación de desvanecimiento a adrid
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.desvanecer);
        // Aplicar la animación de desvanecimiento a industria
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.aparecer);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // No necesitamos hacer nada en el inicio de la animación
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                adrid.setVisibility(View.INVISIBLE); // Hacer que la vista sea invisible al finalizar la animación
                industria.startAnimation(fadeIn); // Iniciar la animación de aparición
                logo.startAnimation(moveAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No necesitamos hacer nada en repeticiones de la animación
            }
        });
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                industria.setVisibility(View.VISIBLE); // Hacer que la vista sea invisible al finalizar la animación
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        adrid.startAnimation(fadeOut); // Iniciar la animación de desvanecimiento

        // Manejar el tiempo de espera antes de iniciar la siguiente actividad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la actividad principal
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finalizar la actividad actual
            }
        }, SPLASH_DURATION);
    }
}
