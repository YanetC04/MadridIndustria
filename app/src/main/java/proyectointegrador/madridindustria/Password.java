package proyectointegrador.madridindustria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class Password extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        TextView txt = findViewById(R.id.txt4);
        Button inicio = findViewById(R.id.iniciar);

        txt.setOnClickListener(v -> {
            Intent intent = new Intent(Password.this, Login.class);
            startActivity(intent);
        });
        inicio.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.google.com/v3/signin/identifier?continue=https%3A%2F%2Fmail.google.com%2Fmail%2Fu%2F0%2F&emr=1&followup=https%3A%2F%2Fmail.google.com%2Fmail%2Fu%2F0%2F&ifkv=ASKXGp0GUVEa7ZtQyUrjM_cTgYYCRQyBuqG_jJ9bIz0rh0tVEJW8tub73nRJgd8GWBCGc1O-WRXrrQ&osid=1&passive=1209600&service=mail&flowName=GlifWebSignIn&flowEntry=ServiceLogin&dsh=S-1174382267%3A1706773316759199&theme=glif"));
            startActivity(browserIntent);
        });

    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}