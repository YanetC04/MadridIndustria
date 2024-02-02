package proyectointegrador.madridindustria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Favorite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // BARRA INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.like);
        bottomNavigationView.setOnNavigationItemSelectedListener(

                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = null;
                        String source = getIntent().getStringExtra("source");

                        if (item.getItemId() == R.id.home) {
                            intent = new Intent(Favorite.this, MainActivity.class).putExtra("source", source);
                        } else if (item.getItemId() == R.id.map) {
                            intent = new Intent(Favorite.this, Map.class).putExtra("source", source);
                        }else if (item.getItemId() == R.id.add) {
                            if (source.equalsIgnoreCase("password") || source.equalsIgnoreCase("add") || source.equalsIgnoreCase("profile"))
                                intent = new Intent(Favorite.this, Add.class);
                            else
                                showDialog("¿Tienes credenciales de Gestor para poder ingresar?");
                        } else if (item.getItemId() == R.id.profile) {
                            if (source.equalsIgnoreCase("password") || source.equalsIgnoreCase("add") || source.equalsIgnoreCase("profile"))
                                intent = new Intent(Favorite.this, Profile.class);
                            else
                                showDialog("¿Tienes credenciales de Gestor para poder ingresar?");
                        }

                        if (intent != null) {
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            return true;
                        }

                        return true;
                    }
                });
    }

    // Diálogo de error
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atención")
                .setMessage(message)
                .setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Favorite.this, Hall.class));
                        overridePendingTransition(0, 0);
                    }
                })
                .setNegativeButton("NO", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // NO VOLVER ATRAS
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}