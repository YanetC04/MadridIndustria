package proyectointegrador.madridindustria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Password extends AppCompatActivity {

    private Button inicio, olvidado;
    private EditText contrasena;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        inicio = findViewById(R.id.inicio);
        olvidado = findViewById(R.id.olvidado);
        contrasena = findViewById(R.id.contrasena);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = getIntent().getStringExtra("mail");
                String pass = contrasena.getText().toString().trim();

                if (mail.isEmpty() & pass.isEmpty()){
                    Toast.makeText(Password.this, "Correo o contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                } else{
                    login(mail, pass);
                }
            }
        });

        olvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Password.this, Password2.class);
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
                    Intent intent = new Intent(Password.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Password.this, "Error: Usuario o Correo no registrados.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Password.this, "Error al iniciar sesión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}