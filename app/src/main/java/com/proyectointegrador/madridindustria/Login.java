package com.proyectointegrador.madridindustria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.util.concurrent.Executors;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.*;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class Login extends AppCompatActivity  {
    private static final int REQUEST_CODE_PERMISSION = 100;
    private EditText email, contrasena;
    private TextInputLayout lay_pass, lay_mail;
    private String mail, pass;
    private ImageView imagen, huella;
    private Drawable redBorderDrawable, defaultBorderDrawable;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        huella = findViewById(R.id.huella);
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
            Glide.with(Login.this)
                    .load(R.drawable.redmadi)
                    .into(imagen);
        }

        inicio.setOnClickListener(v -> {
            mail = email.getText().toString().trim();
            pass = contrasena.getText().toString().trim();
            saveUserData(mail, pass);

            if (!mail.isEmpty() && !pass.isEmpty()){
                if (valida(mail)) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SharedPreferences preferences = getSharedPreferences("ModoApp", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("esRegistrado", true);
                            editor.apply();
                            finish();
                            startActivity(new Intent(Login.this, Add.class).putExtra("source", "abierto").putExtra("correo", mail));
                        } else {
                            SharedPreferences preferences = getSharedPreferences("ModoApp", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("esRegistrado", false);
                            editor.apply();
                            showErrorDialog(getResources().getString(R.string.noRes));
                        }
                    });
                } else {
                    showErrorDialog(getResources().getString(R.string.corNo));
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
                            Intent intent = new Intent(Login.this, Password.class);
                            startActivity(intent);
                        } else {
                            showErrorDialog(getResources().getString(R.string.corNoRes));
                        }
                    });
                } else {
                    showErrorDialog(getResources().getString(R.string.corNo));
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

        // Verificar si el dispositivo soporta la autenticación biométrica
        BiometricManager biometricManager = getSystemService(BiometricManager.class);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "La autenticación biométrica no está disponible en este dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_BIOMETRIC}, REQUEST_CODE_PERMISSION);
        } else if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).contains("esRegistrado")) {
            if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esRegistrado", true)) {
                huella.setVisibility(View.VISIBLE);
            }
        }

        huella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBiometricAuthentication();
            }
        });
    }

    private void saveUserData(String email, String pass) {
        SharedPreferences sharedPreferences = getSharedPreferences("ModoApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.putString("password", pass);
        editor.apply();
    }

    private void startBiometricAuthentication() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación biométrica")
                .setSubtitle("Utiliza tu huella dactilar para iniciar sesión")
                .setNegativeButtonText("Cancelar")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                Executors.newSingleThreadExecutor(),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String user = getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getString("userEmail", "");
                        String pass = getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getString("password", "");

                        if (!user.isEmpty() && !pass.isEmpty()) {
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(user,pass).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    finish();
                                    startActivity(new Intent(Login.this,Add.class).putExtra("source", "abierto").putExtra("correo", mail));
                                } else {
                                    Log.e("Biometric", "No se pudo iniciar sesión automáticamente");
                                }
                            });
                        } else {
                            Log.e("Biometric", "No se encontró información asociada a la huella");
                        }
                        Log.e("Biometric", "Autenticación exitosa");
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Log.e("Biometric", "Error en la autenticación:");
                    }
                });

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBiometricAuthentication();
            } else {
                // Permiso denegado, muestra un mensaje o toma alguna otra acción apropiada
                Toast.makeText(this, "Se requiere permiso para usar la autenticación biométrica", Toast.LENGTH_SHORT).show();
            }
        }
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
    protected void onStart() {
        super.onStart();
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
    }

}