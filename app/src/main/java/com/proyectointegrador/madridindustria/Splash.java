package com.proyectointegrador.madridindustria;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class Splash extends AppCompatActivity {

    private ImageView logo;
    private TextView adrid, industria;
    private static final int SPLASH_DURATION = 2000;
    private static final String CHANNEL_ID = "splash_notification_channel";
    private static final long MESSAGE_INTERVAL = 10 * 60 * 1000;
    private Handler messageHandler = new Handler();
    private Runnable messageRunnable = new Runnable() {
        @Override
        public void run() {
            createNotification();
            messageHandler.postDelayed(this, MESSAGE_INTERVAL);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        adrid = findViewById(R.id.adrid);
        industria = findViewById(R.id.industria);

        //Animation moveAnimation = AnimationUtils.loadAnimation(this, R.anim.centrar);
        // Aplicar la animación de desvanecimiento a adrid
        //Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.desvanecer);
        // Aplicar la animación de desvanecimiento a industria
       // Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.aparecer);

        /*fadeOut.setAnimationListener(new Animation.AnimationListener() {
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
        adrid.startAnimation(fadeOut); // Iniciar la animación de desvanecimiento*/

        // Cargar animaciones desde recursos XML
        ObjectAnimator slideRightX = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.slide_right_x);
        ObjectAnimator slideRightAlpha = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.slide_right_alpha);
        ObjectAnimator slideLeft = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.slide_left);

        // Establecer el objetivo de las animaciones
        slideRightX.setTarget(adrid);
        slideRightAlpha.setTarget(adrid);
        slideLeft.setTarget(logo);

        // Crear un conjunto y agregar los animadores
        AnimatorSet slideRightSet = new AnimatorSet();
        slideRightSet.playTogether(slideRightX, slideRightAlpha);

        // Iniciar las animaciones
        slideRightSet.start();
        slideLeft.start();

        slideRightSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // No necesitamos hacer nada en el inicio de la animación
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator slideDownAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(Splash.this, R.animator.slide_down);
                slideDownAnimator.setTarget(industria);
                industria.setVisibility(View.VISIBLE);
                slideDownAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // No necesitamos hacer nada si la animación se cancela
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // No necesitamos hacer nada en repeticiones de la animación
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class).putExtra("source", "cerrado");
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);

        messageHandler.postDelayed(messageRunnable, MESSAGE_INTERVAL);
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MADi";
            String description = "MADi";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Sabías que...")
                .setContentText("La estación de Atocha fue construida en 1851.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(0, builder.build());
    }
}
