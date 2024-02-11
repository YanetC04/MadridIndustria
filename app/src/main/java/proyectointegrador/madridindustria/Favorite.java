package proyectointegrador.madridindustria;

import androidx.appcompat.app.*;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;

import java.util.Objects;

public class Favorite extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView textView;
    private ImageView imagenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        linearLayout = findViewById(R.id.linear);
        textView = findViewById(R.id.textView);

        // BASE DE DATOS
        FirebaseFirestore.getInstance().collection("favorites").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    View favoriteCard = LayoutInflater.from(Favorite.this).inflate(R.layout.favorite_card, null);

                    ImageView imagen = favoriteCard.findViewById(R.id.imagen);
                    imagenId = favoriteCard.findViewById(R.id.imagen);
                    TextView nombre = favoriteCard.findViewById(R.id.nombre);
                    TextView inaguracion = favoriteCard.findViewById(R.id.inaguracion);
                    TextView patrimonio = favoriteCard.findViewById(R.id.patrimonio);
                    TextView metro = favoriteCard.findViewById(R.id.metro);
                    TextView direccion = favoriteCard.findViewById(R.id.direccion);
                    String numeroDeReferencia = document.getReference().getId();

                    new FirestoreDatabase("favorites", numeroDeReferencia, firestoreDatabase -> {
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

                    });

                    linearLayout.addView(favoriteCard);
                }
            }
        });

        // BARRA INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.like);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;
            String source = getIntent().getStringExtra("source");

            if (item.getItemId() == R.id.home) {
                intent = new Intent(Favorite.this, MainActivity.class).putExtra("source", source);
            }
            if (item.getItemId() == R.id.map) {
                intent = new Intent(Favorite.this, Map.class).putExtra("source", source);
            }
            if (item.getItemId() == R.id.add) {
                if (Objects.requireNonNull(source).equalsIgnoreCase("cerrado")) {
                    showDialog(Add.class);
                } else {
                    intent = new Intent(Favorite.this, Add.class);
                }
            }
            if (item.getItemId() == R.id.profile) {
                if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){
                    showDialog(Profile.class);
                } else {
                    intent = new Intent(Favorite.this, Profile.class);
                }
            }

            if (intent != null) {
                startActivity(intent);
                // Sin transición
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    // Diálogo de error
    private void showDialog(Class intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Modo Gestor")
                .setMessage("¿Quieres activar el modo Gestor?")
                .setPositiveButton("SÍ", (dialog, which) -> {
                    startActivity(new Intent(Favorite.this, Hall.class).putExtra("intent", intent.getName()));
                    overridePendingTransition(0, 0);
                })
                .setNegativeButton("NO", null);

        // Creación y visualización del diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (imagenId != null) {
            Glide.with(this).clear(imagenId);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}