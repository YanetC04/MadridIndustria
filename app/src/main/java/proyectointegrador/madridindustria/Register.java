package proyectointegrador.madridindustria;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Objects;

public class Register extends AppCompatActivity {
    private EditText code, mail, first_pass, confirm_pass;
    private TextInputLayout lay_code, lay_mail, lay_first_pass, lay_confirm_pass;
    private final Drawable redBorderDrawable = ContextCompat.getDrawable(this, R.drawable.red_border);
    private final Drawable defaultBorderDrawable = ContextCompat.getDrawable(this, R.drawable.default_border);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        code = findViewById(R.id.code);
        mail = findViewById(R.id.email);
        first_pass = findViewById(R.id.password);
        confirm_pass = findViewById(R.id.password2);
        lay_code = findViewById(R.id.input_name);
        lay_mail = findViewById(R.id.input_email);
        lay_first_pass = findViewById(R.id.input_password);
        lay_confirm_pass = findViewById(R.id.input_password2);
    }

    public void openMain(View view){
        // OBTENGO LOS VALORES
        String codeText = code.getText().toString();
        String mailText = mail.getText().toString();
        String firstPassText = first_pass.getText().toString();
        String confirmPassText = confirm_pass.getText().toString();

        // COMPRUEBO SI NO ESTAN VACIOS
        if (!codeText.isEmpty() && !mailText.isEmpty() && !firstPassText.isEmpty() && !confirmPassText.isEmpty()) {
            if (codeText.equalsIgnoreCase("gestorDAM2")){
                if(valida(mailText)){
                    if (firstPassText.equals(confirmPassText)){
                        // AGREGAMOS EL USUARIO AL FIREBASE
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(mailText, firstPassText).addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                getCount(count -> {
                                    if (count >= 0) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        HashMap<String, Object> datos = new HashMap<>();
                                        datos.put("mail", mailText);
                                        datos.put("password", firstPassText);

                                        DocumentReference documentReference = db.collection("users").document(String.valueOf(count+1));
                                        documentReference.set(datos)
                                                .addOnSuccessListener(aVoid -> {
                                                    // REDIRIGE AL LOGIN
                                                    Intent intent = new Intent(Register.this, Login.class);
                                                    startActivity(intent);
                                                });
                                    } else {
                                        Log.e("FirestoreData", "Error");
                                    }
                                });
                            } else {
                                Log.e("User", "No Creado");
                            }
                        });
                    } else {
                        showErrorDialog("Las contraseñas no coinciden. Por favor, verifique.");
                        first_pass.setBackground(redBorderDrawable);
                        confirm_pass.setBackground(redBorderDrawable);
                    }
                } else {
                    showErrorDialog("Correo electrónico no válido. Por favor, verifique.");
                    mail.setBackground(redBorderDrawable);
                }
            }
        } else {
            if (codeText.isEmpty()) {
                lay_code.setHint("");
                code.setBackground(redBorderDrawable);

                // COMPROBAMOS QUE SI AGREGA TEXTO LUEGO, VOLVEMOS A PONER EL BORDE EN PREDETERMINADO
                code.setOnFocusChangeListener((v, hasFocus) -> {
                    if(!hasFocus){
                        code.setBackground(defaultBorderDrawable);
                        lay_code.setHint(R.string.code);
                    }
                });
            }

            if (mailText.isEmpty()) {
                lay_mail.setHint("");
                mail.setBackground(redBorderDrawable);
                mail.setOnFocusChangeListener((v, hasFocus) -> {
                    if(!hasFocus){
                        mail.setBackground(defaultBorderDrawable);
                        lay_mail.setHint(R.string.email);
                    }
                });
            }

            if (firstPassText.isEmpty()) {
                lay_first_pass.setHint("");
                first_pass.setBackground(redBorderDrawable);
                first_pass.setOnFocusChangeListener((v, hasFocus) -> {
                    if(!hasFocus){
                        first_pass.setBackground(defaultBorderDrawable);
                        lay_first_pass.setHint(R.string.password);
                    }
                });
            }

            if (confirmPassText.isEmpty()) {
                lay_confirm_pass.setHint("");
                confirm_pass.setBackground(redBorderDrawable);
                confirm_pass.setOnFocusChangeListener((v, hasFocus) -> {
                    if(!hasFocus){
                        confirm_pass.setBackground(defaultBorderDrawable);
                        lay_confirm_pass.setHint(R.string.password2);
                    }
                });
            }
        }
    }

    private boolean valida(String mail) {
        return mail.endsWith("@gmail.com") || mail.endsWith("@hotmail.com") || mail.endsWith("@outlook.com") || mail.endsWith("@icloud.com");
    }

    public void getCount(final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size();
                countCallback.onCallback(count);
            } else {
                Log.e("FirestoreData", "Error getting document count: " + Objects.requireNonNull(task.getException()).getMessage());
                countCallback.onCallback(-1); // Indicates an error
            }
        });
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}