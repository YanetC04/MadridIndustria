package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.Objects;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private Boolean llave = false, esNoche;
    private ImageView modo;
    private int nuevaImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String source = getIntent().getStringExtra("source");
        String correo = getIntent().getStringExtra("correo");

        TextView correoText = findViewById(R.id.correo);
        TextView nombre = findViewById(R.id.nombre);
        Button cerrarButton = findViewById(R.id.cerrar);
        Button contrasenaButton = findViewById(R.id.contrasena);
        Button borrarButton = findViewById(R.id.code);
        modo = findViewById(R.id.modo);
        CircleImageView anonimo = findViewById(R.id.usuario);

        if (source != null && source.equalsIgnoreCase("abierto")) {
            // CORREO
            correoText.setVisibility(View.VISIBLE);
            correoText.setText(correo);
            nombre.setText(getResources().getString(R.string.gestor));

            // BOTÓN PARA CERRAR SESIÓN
            cerrarButton.setVisibility(View.VISIBLE);
            cerrarButton.setOnClickListener(view -> cerrarSesion());

            // BOTÓN PARA CAMBIAR CONTRASEÑA
            contrasenaButton.setVisibility(View.VISIBLE);
            contrasenaButton.setOnClickListener(view -> cambiarContrasena());

            // BOTÓN PARA BORRAR CUENTA
            borrarButton.setVisibility(View.VISIBLE);
            borrarButton.setOnClickListener(view -> borrarCuenta());
            llave = true;
        }

        // MODO
        int configuracion = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        esNoche = configuracion == Configuration.UI_MODE_NIGHT_YES;

        Glide.with(Profile.this)
                .load(esNoche ? R.drawable.luna : R.drawable.sol)
                .into(modo);

        int colorTinte = esNoche ? getColor(R.color.white) : getColor(R.color.eire_black);
        anonimo.setColorFilter(colorTinte);

        modo.setOnClickListener(v -> {
            esNoche = !esNoche;

            // Cambiar el modo
            int nuevoModo = esNoche ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
            getApplication().getResources().getConfiguration().uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
            getApplication().getResources().getConfiguration().uiMode |= nuevoModo;

            // Cambiar la imagen
            nuevaImagen = esNoche ? R.drawable.sol : R.drawable.luna;
            Glide.with(Profile.this)
                    .load(nuevaImagen)
                    .into(modo);

            guardarModoNoche(esNoche);

            recreate();
        });

        // BOTÓN DE AYUDA
        Button ayudaButton = findViewById(R.id.ayuda);
        ayudaButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.github))
                    .setMessage(Html.fromHtml("**<b>Celeste Guillén:</b> @blue_c0de<br>**<b>Alex Mazariegos:</b> @Zan-40<br><b>**Yanet Camacho:</b> @YanetC04"))
                    .setPositiveButton("OK", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // BOTÓN DE QR
        Button QR = findViewById(R.id.qr);
        QR.setOnClickListener(view -> {
            Intent intent;

            if (llave)
                intent = new Intent(Profile.this, QR.class).putExtra("source", "abierto");
            else
                intent = new Intent(Profile.this, QR.class).putExtra("source", "cerrado");

            // INICIAR LA ACTIVIDAD SEGÚN EL INTENTO
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // BOTÓN PARA CAMBIAR IDIOMA
        Button idiomaButton = findViewById(R.id.idioma);
        idiomaButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Profile.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.language_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.english) {
                    setLocale("en");
                    return true;
                } else if (id == R.id.spanish) {
                    setLocale("es");
                    return true;
                }

                return false;
            });

            popupMenu.show();
        });

        // BARRA DE NAVEGACIÓN INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;

            // MANEJO DE INTENTO SEGÚN LA OPCIÓN SELECCIONADA
            if (item.getItemId() == R.id.home) {
                if (llave)
                    intent = new Intent(Profile.this, MainActivity.class).putExtra("source", "abierto");
                else
                    intent = new Intent(Profile.this, MainActivity.class).putExtra("source", "cerrado");
            } else if (item.getItemId() == R.id.map) {
                if (llave)
                    intent = new Intent(Profile.this, Map.class).putExtra("source", "abierto");
                else
                    intent = new Intent(Profile.this, Map.class).putExtra("source", "cerrado");
            } else if (item.getItemId() == R.id.like) {
                if (llave)
                    intent = new Intent(Profile.this, Favorite.class).putExtra("source", "abierto");
                else
                    intent = new Intent(Profile.this, Favorite.class).putExtra("source", "cerrado");
            } else if (item.getItemId() == R.id.add) {
                if (llave)
                    intent = new Intent(Profile.this, Add.class).putExtra("source", "abierto");
                else
                    showDialog();
            }

            // INICIAR LA ACTIVIDAD SEGÚN EL INTENTO
            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    private void guardarModoNoche(boolean esNoche) {
        SharedPreferences preferences = getSharedPreferences("ModoApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("esNoche", esNoche);
        editor.apply();
    }

    private void cambiarContrasena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.cambiarcon));

        View view = getLayoutInflater().inflate(R.layout.new_password, null);
        final EditText contrasenaActual = view.findViewById(R.id.etOldPassword);
        final EditText nuevaContrasena  = view.findViewById(R.id.etNewPassword);
        final EditText confirmarNuevaContrasena = view.findViewById(R.id.etConfirmPassword);

        builder.setView(view);

        builder.setPositiveButton(getResources().getString(R.string.cambiar), (dialog, which) -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                String actual = contrasenaActual.getText().toString();
                String nueva = nuevaContrasena .getText().toString();
                String confirmar = confirmarNuevaContrasena.getText().toString();

                if (!nueva.equals(confirmar)) {
                    showError(getResources().getString(R.string.contrano));
                    return;
                }

                mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), actual).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(nueva).addOnCompleteListener(passwordUpdateTask -> {
                            if (passwordUpdateTask.isSuccessful()) {
                                Toast.makeText(Profile.this, getResources().getString(R.string.contrasi), Toast.LENGTH_SHORT).show();
                            } else {
                                showError(getResources().getString(R.string.contraNo));
                            }
                        });
                    } else {
                        showError(getResources().getString(R.string.contraInc));
                    }
                });
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // MÉTODO PARA BORRAR LA CUENTA
    private void borrarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.conf))
                .setMessage(getResources().getString(R.string.confCont))
                .setPositiveButton(getResources().getString(R.string.si), (dialog, which) -> {
                    // ELIMINAR LA CUENTA DEL USUARIO
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        // OBTENER EL CORREO ELECTRÓNICO DEL USUARIO ANTES DE CERRAR SESIÓN
                        String userEmail = currentUser.getEmail();

                        // ELIMINAR LA CUENTA DEL USUARIO ACTUAL
                        currentUser.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // ACCEDER A FIRESTORE Y ELIMINAR EL DOCUMENTO DEL USUARIO DE LA COLECCIÓN 'users'
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        CollectionReference usersCollection = db.collection("users");

                                        // CONSULTA PARA ENCONTRAR EL DOCUMENTO DEL USUARIO CON EL CORREO ELECTRÓNICO CORRESPONDIENTE
                                        Query query = usersCollection.whereEqualTo("mail", userEmail);

                                        query.get().addOnCompleteListener(queryTask -> {
                                            if (queryTask.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : queryTask.getResult()) {
                                                    // ELIMINAR EL DOCUMENTO DEL USUARIO
                                                    usersCollection.document(document.getId()).delete();
                                                }
                                            }

                                            // CERRAR SESIÓN Y VOLVER A MainActivity DESPUÉS DE ELIMINAR LA CUENTA
                                            mAuth.signOut();
                                            llave = false;
                                            startActivity(new Intent(Profile.this, MainActivity.class).putExtra("source", "cerrado"));
                                            finish();
                                        });
                                    }
                                });
                    }
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    // EL USUARIO HIZO CLIC EN "No", DESCARTAR EL DIÁLOGO
                    dialog.dismiss();
                });

        // MOSTRAR EL DIÁLOGO DE CONFIRMACIÓN
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // MÉTODO PARA CERRAR SESIÓN
    private void cerrarSesion() {
        // OBTENER LA INSTANCIA DE FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // CERRAR SESIÓN DEL USUARIO ACTUAL
        mAuth.signOut();

        // INICIAR MainActivity DESPUÉS DE CERRAR SESIÓN
        startActivity(new Intent(Profile.this, MainActivity.class).putExtra("source", "cerrado"));

        // ACTUALIZAR LA BANDERA 'llave'
        llave = false;

        // FINALIZAR LA ACTIVIDAD ACTUAL
        finish();
    }

    // EVITAR QUE LA ACTIVIDAD VUELVA ATRÁS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // EVITAR QUE MainActivity VUELVA ATRÁS A Splash.java
        // NO LLAMAR AL super.onBackPressed();
    }

    private void showDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.gest))
                .setMessage(getResources().getString(R.string.mGestor))
                .setPositiveButton(getResources().getString(R.string.si), (dialog, which) -> {
                    startActivity(new Intent(Profile.this, Hall.class));
                    overridePendingTransition(0, 0);
                })
                .setNegativeButton("NO", null);

        builder.create().show();
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Método para cambiar el idioma de la aplicación
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Reiniciar la actividad para aplicar los cambios de idioma
        recreate();
    }
}
