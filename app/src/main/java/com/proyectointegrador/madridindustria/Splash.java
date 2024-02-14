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

public class Splash extends AppCompatActivity {

    private ImageView logo;
    private TextView adrid, industria;
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        adrid = findViewById(R.id.adrid);
        industria = findViewById(R.id.industria);

        // Manejar el tiempo de espera antes de iniciar las animaciones
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar las animaciones después del retraso
                startAnimations();
            }
        }, 500); // 500 milisegundos (medio segundo) de retraso
    }

    private void startAnimations() {
        Animation moveAnimation = AnimationUtils.loadAnimation(this, R.anim.centrar);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.desvanecer);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.aparecer);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                adrid.setVisibility(View.INVISIBLE);
                industria.startAnimation(fadeIn);
                logo.startAnimation(moveAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                industria.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        adrid.startAnimation(fadeOut);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la actividad principal
                Intent intent = new Intent(Splash.this, MainActivity.class).putExtra("source", "cerrado");
                startActivity(intent);
                finish(); // Finalizar la actividad actual
            }
        }, SPLASH_DURATION);
    }

}
