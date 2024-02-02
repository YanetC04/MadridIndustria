package proyectointegrador.madridindustria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    String distritos[] = {"arganzuela", "centro", "moncloa", "chamberi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linear);

        // DINAMICAMENTE CREAR SCROLLVIEW PARA CADA DISTRITO
        for (String dist : distritos) {
            // INFLA EL DISEÑO external_layout.xml
            View externalLayoutView = LayoutInflater.from(this).inflate(R.layout.external_layout, null);
            LinearLayout internalLinear = externalLayoutView.findViewById(R.id.linearExternal);
            TextView distrito = externalLayoutView.findViewById(R.id.distrito);

            for (int i = 1; i<=5; i++){
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
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.home) {
                            // REDIRIGE A MainActivity
                            return true;
                        } else if (item.getItemId() == R.id.map) {
                            // REDIRIGE A Map
                            startActivity(new Intent(MainActivity.this, Map.class));
                            // SIN TRANSICION
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (item.getItemId() == R.id.add) {
                            // REDIRIGE A Gestor
                            // startActivity(new Intent(MainActivity.this, Gestor.class));
                            // SIN TRANSICION
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (item.getItemId() == R.id.like) {
                            // REDIRIGE A Favoritos
                            // startActivity(new Intent(MainActivity.this, Favoritos.class));
                            // SIN TRANSICION
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (item.getItemId() == R.id.profile) {
                            // REDIRIGE A Profile
                            // startActivity(new Intent(MainActivity.this, Profile.class));
                            // SIN TRANSICION
                            overridePendingTransition(0, 0);
                            return true;
                        }
                        return false;
                    }
                });
    }
}