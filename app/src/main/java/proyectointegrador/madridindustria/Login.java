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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    private EditText email;
    private Button inicio, olvidado;
    private EditText contrasena;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.correo);
        inicio = findViewById(R.id.inicio);
        olvidado = findViewById(R.id.olvidado);
        contrasena = findViewById(R.id.contrasena);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                if (!mail.isEmpty()){
                    if (valida(mail)){
                        String pass = contrasena.getText().toString().trim();
                        if (pass.isEmpty()){
                            showErrorDialog("Contraseña está vacía.");
                        } else{
                            login(mail, pass);
                        }
                    } else {
                        showErrorDialog("Correo no válido.");
                    }
                } else {
                    showErrorDialog("Correo Electrónico está vacío.");
                }
            }
        });

        olvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Password.class);
                startActivity(intent);
            }
        });
    }

    // COMPROBAMOS QUE EL CORREO Y LA CONTRASEÑA ESTEN REGISTRADOS EN LA BASE DE DATOS
    private void login(String mail, String pass) {
        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("source", "password");
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorDialog("Usuario o Correo no registrados.");
            }
        });
    }

    private boolean valida(String mail) {
        if(mail.contains("@gmail.com") || mail.contains("@hotmail.com") || mail.contains("@outlook.com") || mail.contains("@icloud.com")){
            return true;
        }
        return false;
    }

    // DIALOGO DE ERROR
    private void showErrorDialog(String message) {
        // LLAMAMOS AL BUILER DE ALERT DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // ESTABLECEMOS QUE NUESTRO BUILDER ES DE ERROR, IMPRIMIRA EL MENSAJE DE ERROR, Y HAY QUE DARLE AL OK
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null);

        // CREAMOS EL DIALOGO
        AlertDialog dialog = builder.create();

        // MOSTRAMOS EL DIALOGO
        dialog.show();
    }
}