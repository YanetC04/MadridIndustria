package com.proyectointegrador.madridindustria;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.*;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Login extends AppCompatActivity  {

    private EditText email, contrasena;
    private TextInputLayout lay_pass, lay_mail;
    private String mail, pass;
    private ImageView imagen;
    private Drawable redBorderDrawable;
    private Drawable defaultBorderDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.correo);
        Button inicio = findViewById(R.id.inicio);
        Button olvidado = findViewById(R.id.olvidado);
        contrasena = findViewById(R.id.contrasena);
        lay_mail = findViewById(R.id.input_email);
        lay_pass = findViewById(R.id.input_password);
        imagen = findViewById(R.id.imageView2);
        redBorderDrawable = ContextCompat.getDrawable(this, R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(this, R.drawable.default_border);

        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
            Glide.with(Login.this)
                    .load(R.drawable.whitemadi)
                    .into(imagen);
        } else {
            // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
            Glide.with(Login.this)
                    .load(R.drawable.redmadi)
                    .into(imagen);
        }

        inicio.setOnClickListener(v -> {
            mail = email.getText().toString().trim();
            pass = contrasena.getText().toString().trim();

            if (!mail.isEmpty() && !pass.isEmpty()){
                if (valida(mail)) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            finish();
                            try {
                                startActivity(new Intent(Login.this,Class.forName(Objects.requireNonNull(getIntent().getStringExtra("intent")))).putExtra("source", "abierto"));
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            showErrorDialog("Usuario o Correo no registrados.");
                        }
                    });
                } else {
                    showErrorDialog("Correo no válido.");
                }
            } else {
                if (pass.isEmpty()){
                    lay_pass.setHint(R.string.password);
                    contrasena.setBackground(redBorderDrawable);
                    contrasena.setOnFocusChangeListener((v1, hasFocus) -> {
                        if(!hasFocus){
                            contrasena.setBackground(defaultBorderDrawable);
                            lay_pass.setHint(R.string.password);
                        }
                    });
                }

                if (mail.isEmpty()){
                    lay_mail.setHint(R.string.email);
                    email.setBackground(redBorderDrawable);
                    email.setOnFocusChangeListener((v12, hasFocus) -> {
                        if(!hasFocus){
                            email.setBackground(defaultBorderDrawable);
                            lay_mail.setHint(R.string.email);
                        }
                    });
                }
            }
        });

        olvidado.setOnClickListener(v -> {
            mail = email.getText().toString().trim();

            if (!mail.isEmpty()) {
                if (valida(mail)) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, Password.class).putExtra("intent", getIntent().getStringExtra("intent"));
                            startActivity(intent);
                        } else {
                            showErrorDialog("Correo no registrado en la base de datos.");
                        }
                    });
                } else {
                    showErrorDialog("Correo no válido.");
                }
            } else {
                lay_mail.setHint(R.string.email);
                email.setBackground(redBorderDrawable);
                email.setOnFocusChangeListener((v13, hasFocus) -> {
                    if(!hasFocus){
                        email.setBackground(defaultBorderDrawable);
                        lay_mail.setHint(R.string.email);
                    }
                });
            }
        });
    }

    private boolean valida(String mail) {
        return mail.endsWith("@gmail.com") || mail.endsWith("@hotmail.com") || mail.endsWith("@outlook.com") || mail.endsWith("@icloud.com");
    }

    // DIALOGO DE ERROR
    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.with(this).clear(imagen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}