package proyectointegrador.madridindustria;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText code, mail, first_pass, confirm_pass;
    private TextInputLayout lay_code, lay_mail, lay_first_pass, lay_confirm_pass;

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
                        mAuth.createUserWithEmailAndPassword(mailText, firstPassText).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    getCount(new CountCallback() {
                                        @Override
                                        public void onCallback(int count) {
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
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.w(TAG, "Error al agregar el documento", e);
                                                        });
                                            } else {
                                                Log.e("FirestoreData", "Error");
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("User", "No Creado");
                                }
                            }
                        });
                    } else {
                        showErrorDialog("Las contraseñas no coinciden. Por favor, verifique.");
                        first_pass.setBackground(getResources().getDrawable(R.drawable.red_border));
                        confirm_pass.setBackground(getResources().getDrawable(R.drawable.red_border));
                    }
                } else {
                    showErrorDialog("Correo electrónico no válido. Por favor, verifique.");
                    mail.setBackground(getResources().getDrawable(R.drawable.red_border));
                }
            }
        } else {
            if (codeText.isEmpty()) {
                lay_code.setHint("");
                code.setBackground(getResources().getDrawable(R.drawable.red_border));

                // COMPROBAMOS QUE SI AGREGA TEXTO LUEGO, VOLVEMOS A PONER EL BORDE EN PREDETERMINADO
                code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus){
                            code.setBackground(getResources().getDrawable(R.drawable.default_border));
                            lay_code.setHint(R.string.code);
                        }
                    }
                });
            }

            if (mailText.isEmpty()) {
                lay_mail.setHint("");
                mail.setBackground(getResources().getDrawable(R.drawable.red_border));
                mail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus){
                            mail.setBackground(getResources().getDrawable(R.drawable.default_border));
                            lay_mail.setHint(R.string.email);
                        }
                    }
                });
            }

            if (firstPassText.isEmpty()) {
                lay_first_pass.setHint("");
                first_pass.setBackground(getResources().getDrawable(R.drawable.red_border));
                first_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus){
                            first_pass.setBackground(getResources().getDrawable(R.drawable.default_border));
                            lay_first_pass.setHint(R.string.password);
                        }
                    }
                });
            }

            if (confirmPassText.isEmpty()) {
                lay_confirm_pass.setHint("");
                confirm_pass.setBackground(getResources().getDrawable(R.drawable.red_border));
                confirm_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus){
                            confirm_pass.setBackground(getResources().getDrawable(R.drawable.default_border));
                            lay_confirm_pass.setHint(R.string.password2);
                        }
                    }
                });
            }
        }
    }

    private boolean valida(String mail) {
        if(mail.endsWith("@gmail.com") || mail.endsWith("@hotmail.com") || mail.endsWith("@outlook.com") || mail.endsWith("@icloud.com")){
            return true;
        }
        return false;
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