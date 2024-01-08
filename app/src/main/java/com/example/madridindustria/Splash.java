package com.example.madridindustria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.*;
import android.os.Bundle;
import android.widget.*;

public class Splash extends AppCompatActivity {

    private ImageView madrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Variables
        VideoView videoView = findViewById(R.id.splash);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.madrid_splash;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        videoView.setOnCompletionListener(mp -> {
            // Call Activity Main when the animation ends
            startActivity(new Intent(Splash.this, MainActivity.class));
        });

        // Start the animation
        videoView.start();
    }
}