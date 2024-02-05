package proyectointegrador.madridindustria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Favorite extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView textView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        linearLayout = findViewById(R.id.linear);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        textView = findViewById(R.id.textView);

        // BASE DE DATOS
        FirebaseFirestore.getInstance().collection("favorites").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    View favoriteCard = LayoutInflater.from(Favorite.this).inflate(R.layout.favorite_card, null);

                    ImageView imagen = favoriteCard.findViewById(R.id.imagen);
                    TextView nombre = favoriteCard.findViewById(R.id.nombre);
                    TextView inaguracion = favoriteCard.findViewById(R.id.inaguracion);
                    TextView patrimonio = favoriteCard.findViewById(R.id.patrimonio);
                    TextView metro = favoriteCard.findViewById(R.id.metro);
                    TextView direccion = favoriteCard.findViewById(R.id.direccion);
                    String numeroDeReferencia = document.getReference().getId();

                    new FirestoreDatabase("favorites", numeroDeReferencia, new FirestoreCallback() {
                        @Override
                        public void onCallback(FirestoreDatabase firestoreDatabase) {
                            // ESTABLECER INFORMACION
                            textView.setVisibility(View.INVISIBLE);
                            nombre.setText(firestoreDatabase.getNombre());
                            inaguracion.setText(firestoreDatabase.getInaguracion());
                            patrimonio.setText(firestoreDatabase.getPatrimonio());
                            metro.setText(firestoreDatabase.getMetro());
                            direccion.setText(firestoreDatabase.getDireccion());
                            Glide.with(Favorite.this)
                                    .load(firestoreDatabase.getImagen())
                                    .centerCrop()
                                    .into(imagen);
                        }
                    });

                    linearLayout.addView(favoriteCard);
                }
            }
        });

        // BARRA INFERIOR
        bottomNavigationView = findViewById(R.id.bottom_navigation);
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
                                showDialog("¿Quieres activar el modo Gestor?");
                        } else if (item.getItemId() == R.id.profile) {
                            if (source.equalsIgnoreCase("password") || source.equalsIgnoreCase("add") || source.equalsIgnoreCase("profile"))
                                intent = new Intent(Favorite.this, Profile.class);
                            else
                                showDialog("¿Quieres activar el modo Gestor?");
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

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modo Gestor")
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

    public void getCount(final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection("favorites").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = task.getResult().size();
                    countCallback.onCallback(count);
                } else {
                    Log.e("FirestoreData", "Error getting document count: " + task.getException().getMessage());
                    countCallback.onCallback(-1); // Indicates an error
                }
            }
        });
    }

    // NO VOLVER ATRAS
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }
}