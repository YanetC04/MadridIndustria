package com.proyectointegrador.madridindustria;

import androidx.appcompat.app.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.widget.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.Objects;

import java.util.Locale;

public class Profile extends AppCompatActivity {
    private Boolean llave = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // BOTÓN PARA CERRAR SESIÓN
        TextView cerrarButton = findViewById(R.id.cerrar);
        cerrarButton.setOnClickListener(view -> cerrarSesion());

        // BOTÓN PARA CERRAR SESIÓN
        TextView contrasenaButton = findViewById(R.id.contrasena);
        contrasenaButton.setOnClickListener(view -> cambiarContrasena());

        // BOTÓN PARA BORRAR CUENTA
        TextView borrarButton = findViewById(R.id.code);
        borrarButton.setOnClickListener(view -> borrarCuenta());
      
        // IDIOMA
        TextView idioma = findViewById(R.id.traduccion);
        idioma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(Profile.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.language_menu, popupMenu.getMenu());

                // Manejar la selección del usuario en el menú desplegable
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        // Cambiar el idioma de la aplicación según la opción seleccionada
                        if (id == R.id.english) {
                            setLocale("en"); // Método para cambiar el idioma a inglés
                            return true;
                        } else if (id == R.id.spanish) {
                            setLocale("es"); // Método para cambiar el idioma a español
                            return true;
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
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
                intent = new Intent(Profile.this, Add.class).putExtra("source", "abierto");
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

    private void cambiarContrasena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Contraseña");

        View view = getLayoutInflater().inflate(R.layout.new_password, null);
        final EditText contrasenaActual = view.findViewById(R.id.etOldPassword);
        final EditText nuevaContrasena  = view.findViewById(R.id.etNewPassword);
        final EditText confirmarNuevaContrasena = view.findViewById(R.id.etConfirmPassword);

        builder.setView(view);

        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                String actual = contrasenaActual.getText().toString();
                String nueva = nuevaContrasena .getText().toString();
                String confirmar = confirmarNuevaContrasena.getText().toString();

                if (!nueva.equals(confirmar)) {
                    // Las contraseñas no coinciden
                    showError("Las contraseñas no coinciden.");
                    return;
                }

                mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), actual).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(nueva).addOnCompleteListener(passwordUpdateTask -> {
                            if (passwordUpdateTask.isSuccessful()) {
                                Toast.makeText(Profile.this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                showError("Error al actualizar la contraseña. Asegúrate de que la nueva contraseña cumple con los requisitos de Firebase.");
                            }
                        });
                    } else {
                        showError("La contraseña actual es incorrecta.");
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // MÉTODO PARA BORRAR LA CUENTA
    private void borrarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres borrar tu cuenta?")
                .setPositiveButton("Sí", (dialog, which) -> {
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
                .setNegativeButton("No", (dialog, which) -> {
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
