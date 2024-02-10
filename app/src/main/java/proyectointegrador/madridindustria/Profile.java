package proyectointegrador.madridindustria;

import androidx.appcompat.app.*;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity {
    private Boolean llave = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView cerrarButton = findViewById(R.id.cerrar);
        cerrarButton.setOnClickListener(view -> cerrarSesion());

        // BARRA INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.home) {
                if(llave)
                    intent = new Intent(Profile.this, MainActivity.class).putExtra("source", "abierto");
                else
                    intent = new Intent(Profile.this, MainActivity.class).putExtra("source", "cerrado");
            } else if (item.getItemId() == R.id.map) {
                if(llave)
                    intent = new Intent(Profile.this, Map.class).putExtra("source", "abierto");
                else
                    intent = new Intent(Profile.this, Map.class).putExtra("source", "cerrado");
            } else if (item.getItemId() == R.id.like) {
                if(llave)
                    intent = new Intent(Profile.this, Favorite.class).putExtra("source", "abierto");
                else
                    intent = new Intent(Profile.this, Favorite.class).putExtra("source", "cerrado");
            } else if (item.getItemId() == R.id.add) {
                intent = new Intent(Profile.this, Add.class).putExtra("source", "abierto");
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });

    }

    private void cerrarSesion(){
        // Obtener la instancia de FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Cerrar sesión del usuario actual
        mAuth.signOut();
        startActivity(new Intent(Profile.this,MainActivity.class).putExtra("source", "cerrado"));
        llave=false;
        finish();
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}