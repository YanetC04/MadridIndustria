package proyectointegrador.madridindustria;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SignInMethodQueryResult;


public class Login extends AppCompatActivity  {

    private EditText email, contrasena;
    private Button inicio, olvidado;
    private TextInputLayout lay_pass, lay_mail;
    private FirebaseAuthHelper authHelper = new FirebaseAuthHelper(this);
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
                        authHelper.confirmEmail(mail, new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(Login.this, Password.class);
                                    startActivity(intent);
                                } else {
                                    showErrorDialog("Correo no existente en la base de datos. Registrarse.");
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