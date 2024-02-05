package proyectointegrador.madridindustria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        // BARRA INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = null;

                        if (item.getItemId() == R.id.home) {
                            intent = new Intent(Profile.this, MainActivity.class).putExtra("source", "profile");
                        } else if (item.getItemId() == R.id.map) {
                            intent = new Intent(Profile.this, Map.class).putExtra("source", "profile");
                        }else if (item.getItemId() == R.id.like) {
                            intent = new Intent(Profile.this, Favorite.class).putExtra("source", "profile");
                        } else if (item.getItemId() == R.id.add) {
                            intent = new Intent(Profile.this, Add.class);
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
                        startActivity(new Intent(Profile.this, Hall.class));
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