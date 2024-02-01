package proyectointegrador.madridindustria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class Login extends AppCompatActivity {

    private EditText email, password;
    private Button inicio, apple, facebook, gmail;
    private LinearLayout l1, l2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.correo);

        inicio = findViewById(R.id.inicio);
        apple = findViewById(R.id.apple);
        facebook = findViewById(R.id.facebook);
        gmail = findViewById(R.id.gmail);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                if (!mail.isEmpty()){
                    if (valida(mail)){
                        Intent intent = new Intent(Login.this, Password.class);
                        intent.putExtra("mail", email.getText().toString().trim());
                        startActivity(intent);
                    }
                } else {
                    showErrorDialog("Correo no válido.");
                    email.setBackground(getResources().getDrawable(R.drawable.red_border));
                    email.setHintTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        // FALTAN APIS
        apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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