package com.example.madridindustria;

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
                if (valida(email.toString())){
                    Intent intent = new Intent(Login.this, Password2.class);
                    startActivity(intent);
                } else {
                    showErrorDialog("Correo no válido.");
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
    private boolean valida(String emailText) {
        // CONECTAR BASE DE DATOS Y VALIDAR
        return true; // de momento para que nos deje pasar
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
/*Apple hay que pedir la api
* import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class AppleSignIn {

    private static final String APPLE_CLIENT_ID = "tu_client_id_de_apple";
    private static final String APPLE_CLIENT_SECRET = "tu_client_secret_de_apple";
    private static final String APPLE_REDIRECT_URI = "tu_uri_de_redireccionamiento";

    public static void main(String[] args) {
        try {
            // Definir las credenciales codificadas en base64
            String credentials = APPLE_CLIENT_ID + ":" + APPLE_CLIENT_SECRET;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            // Configurar la URL de la API de Apple para obtener el token
            URL url = new URL("https://appleid.apple.com/auth/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            // Definir los parámetros del cuerpo de la solicitud
            String requestBody = "grant_type=authorization_code&code=your_authorization_code&redirect_uri=" + APPLE_REDIRECT_URI;
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // Leer la respuesta de la API de Apple
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Imprimir la respuesta
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

*
* */