package proyectointegrador.madridindustria;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class Login extends AppCompatActivity  {

    private EditText email, contrasena;
    private Button inicio, olvidado;
    private TextInputLayout lay_pass, lay_mail;
    private FirestoreDatabase data = null;
    private FirebaseAuthHelper authHelper = new FirebaseAuthHelper();
    private String mail, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.correo);
        inicio = findViewById(R.id.inicio);
        olvidado = findViewById(R.id.olvidado);
        contrasena = findViewById(R.id.contrasena);
        lay_mail = findViewById(R.id.input_email);
        lay_pass = findViewById(R.id.input_password);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString().trim();
                pass = contrasena.getText().toString().trim();

                if (!mail.isEmpty() && !pass.isEmpty()){
                    if (valida(mail)) {
                        authHelper.signInWithEmailAndPassword(mail, pass, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    finish();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("source", "password");
                                    startActivity(intent);
                                } else {
                                    showErrorDialog("Usuario o Correo no registrados.");
                                }
                            }
                        });
                    } else {
                        showErrorDialog("Correo no válido.");
                    }
                } else {
                    if (pass.isEmpty()){
                        lay_pass.setHint("");
                        contrasena.setBackground(getResources().getDrawable(R.drawable.red_border));
                        contrasena.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    contrasena.setBackground(getResources().getDrawable(R.drawable.default_border));
                                    lay_pass.setHint(R.string.password);
                                }
                            }
                        });
                    }

                    if (mail.isEmpty()){
                        lay_mail.setHint("");
                        email.setBackground(getResources().getDrawable(R.drawable.red_border));
                        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    email.setBackground(getResources().getDrawable(R.drawable.default_border));
                                    lay_mail.setHint(R.string.email);
                                }
                            }
                        });
                    }
                }
            }
        });

        olvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString().trim();

                if (!mail.isEmpty()) {
                    if (valida(mail)) {
                        getCount(new CountCallback() {
                            @Override
                            public void onCallback(int count) {
                                if (count >= 0) {
                                    for (int i = 1; i <= count; i++) {
                                        final int currentIndex = i;

                                        FirestoreDatabase firestoreDatabase = new FirestoreDatabase("users", String.valueOf(i), new FirestoreCallback() {
                                            @Override
                                            public void onCallback(FirestoreDatabase firestoreDatabase) {
                                                String userMail = firestoreDatabase.getMail();
                                                Log.d(TAG, userMail);
                                                Log.d(TAG, mail);
                                                if (userMail != null && userMail.equalsIgnoreCase(mail)) {
                                                    authHelper.getmAuth().sendPasswordResetEmail(mail);
                                                    finish();
                                                    Intent intent = new Intent(Login.this, Password.class);
                                                    startActivity(intent);
                                                } else {
                                                    showErrorDialog("Correo no registrado en la base de datos.");
                                                }
                                            }
                                        });
                                    }

                                } else {
                                    Log.e("FirestoreData", "Error");
                                }
                            }
                        });
                    } else {
                        showErrorDialog("Correo no válido.");
                    }
                } else {
                    if (mail.isEmpty()){
                        lay_mail.setHint("");
                        email.setBackground(getResources().getDrawable(R.drawable.red_border));
                        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(!hasFocus){
                                    email.setBackground(getResources().getDrawable(R.drawable.default_border));
                                    lay_mail.setHint(R.string.email);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void getCount(final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private boolean valida(String mail) {
        if(mail.endsWith("@gmail.com") || mail.endsWith("@hotmail.com") || mail.endsWith("@outlook.com") || mail.endsWith("@icloud.com")){
            return true;
        }
        return false;
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
}