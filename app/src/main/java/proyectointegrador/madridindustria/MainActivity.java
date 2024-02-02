package proyectointegrador.madridindustria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private BottomNavigationView bottomNavigationView;
    private String distritos[] = {"arganzuela", "centro", "moncloa", "chamberi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linear);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // DINAMICAMENTE CREAR SCROLLVIEW PARA CADA DISTRITO
        for (String dist : distritos) {
            // INFLA EL DISEÑO external_layout.xml
            View externalLayoutView = LayoutInflater.from(this).inflate(R.layout.external_layout, null);
            LinearLayout internalLinear = externalLayoutView.findViewById(R.id.linearExternal);
            TextView distrito = externalLayoutView.findViewById(R.id.distrito);

            for (int i = 1; i <= 5; i++) {
                View internalLayoutView = LayoutInflater.from(this).inflate(R.layout.internal_layout, null);
                ImageView imagen = internalLayoutView.findViewById(R.id.imagen);
                TextView texto = internalLayoutView.findViewById(R.id.texto);
                String value = String.valueOf(i);

                // BASE DE DATOS
                FirestoreDatabase firestoreDatabase = new FirestoreDatabase(dist, value, new FirestoreCallback() {
                    @Override
                    public void onCallback(FirestoreDatabase firestoreDatabase) {
                        // ESTABLECER INFORMACION
                        distrito.setText(firestoreDatabase.getDistrito());
                        texto.setText(firestoreDatabase.getNombre());

                        Glide.with(MainActivity.this)
                                .load(firestoreDatabase.getImagen())
                                .centerCrop()
                                .into(imagen);
                    }
                });

                // CONFIGURAMOS LA IMAGEN
                imagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, Patrimonio.class).putExtra("collection", dist).putExtra("document", value));
                    }
                });

                internalLinear.addView(internalLayoutView);
            }

            // AGREGA EL DISEÑO INFLADO AL LINEARLAYOUT DEL SCROLLVIEW
            linearLayout.addView(externalLayoutView);
        }


        // BARRA INFERIOR
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = null;
                        String source = getIntent().getStringExtra("source");

                        if (item.getItemId() == R.id.map) {
                            // Redirige a Map
                            intent = new Intent(MainActivity.this, Map.class).putExtra("source", source);
                        } else if (item.getItemId() == R.id.add) {
                            // Redirige a Add
                            if (source.equalsIgnoreCase("password") || source.equalsIgnoreCase("add") || source.equalsIgnoreCase("profile"))
                                intent = new Intent(MainActivity.this, Add.class);
                            else
                                showDialog("¿Tienes credenciales de Gestor para poder ingresar?");
                        } else if (item.getItemId() == R.id.like) {
                            // Redirige a Favorite
                            intent = new Intent(MainActivity.this, Favorite.class).putExtra("source", source);
                        } else if (item.getItemId() == R.id.profile) {
                            // Redirige a Profile
                            if (source.equalsIgnoreCase("password") || source.equalsIgnoreCase("add") || source.equalsIgnoreCase("profile"))
                                intent = new Intent(MainActivity.this, Profile.class);
                            else
                                showDialog("¿Tienes credenciales de Gestor para poder ingresar?");
                        }

                        if (intent != null) {
                            startActivity(intent);
                            // Sin transición
                            overridePendingTransition(0, 0);
                            return true;
                        }

                        return true;
                    }
                });
    }

    // Diálogo de error
    private void showDialog(String message) {
        // Builder de AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configuración del diálogo de error
        builder.setTitle("Atención")
                .setMessage(message)
                .setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Redirige a Login
                        startActivity(new Intent(MainActivity.this, Hall.class));
                        // Sin transición
                        overridePendingTransition(0, 0);
                    }
                })
                .setNegativeButton("NO", null);

        // Creación y visualización del diálogo
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