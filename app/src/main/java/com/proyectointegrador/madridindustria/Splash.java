package com.proyectointegrador.madridindustria;

import android.animation.*;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Random;

public class Splash extends AppCompatActivity {

    private TextView industria;
    private static final int SPLASH_DURATION = 2000;
    private static final String CHANNEL_ID = "splash_notification_channel";
    private static final long MESSAGE_INTERVAL = 10 * 60 * 1000;
    private final Handler MESSAGE_HANDLER = new Handler();
    private String[] sabiasQue;


    private final Runnable messageRunnable = new Runnable() {
        @Override
        public void run() {
            createNotification();
            MESSAGE_HANDLER.postDelayed(this, MESSAGE_INTERVAL);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView adrid = findViewById(R.id.adrid);
        industria = findViewById(R.id.industria);
        sabiasQue = getResources().getStringArray(R.array.sabias);

        // Cargar modo
        int nuevoModo = (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esNoche", false))? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        getApplication().getResources().getConfiguration().uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        getApplication().getResources().getConfiguration().uiMode |= nuevoModo;

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
            public void onAnimationStart(@NonNull Animator animation) {
                // No necesitamos hacer nada en el inicio de la animación
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                ObjectAnimator slideDownAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(Splash.this, R.animator.slide_down);
                slideDownAnimator.setTarget(industria);
                industria.setVisibility(View.VISIBLE);
                slideDownAnimator.start();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                // No necesitamos hacer nada si la animación se cancela
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
                // No necesitamos hacer nada en repeticiones de la animación
            }
        });

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, MainActivity.class).putExtra("source", "cerrado");
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);

        MESSAGE_HANDLER.postDelayed(messageRunnable, MESSAGE_INTERVAL);
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
                .setContentTitle(getResources().getString(R.string.sabiasque))
                .setContentText(sabiasQue[new Random().nextInt(sabiasQue.length)])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(0, builder.build());
    }
}
