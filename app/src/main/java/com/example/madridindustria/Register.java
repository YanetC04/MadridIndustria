package com.example.madridindustria;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;


public class Register extends AppCompatActivity {
    private EditText email, password;
    private Button inicio, apple, facebook, gmail;
    private LinearLayout l1, l2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        l1 = findViewById(R.id.li1);
        l2 = findViewById(R.id.li2);

        password = findViewById(R.id.contrasena);
        email = findViewById(R.id.correo);

        inicio = findViewById(R.id.inicio);
        apple = findViewById(R.id.apple);
        facebook = findViewById(R.id.facebook);
        gmail = findViewById(R.id.gmail);



        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Validacion

                valida(charSequence.toString());
                    inicio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            l1.setVisibility(View.INVISIBLE);
                            l2.setVisibility(View.VISIBLE);

                        }
                    });

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        // iniciar secion en apple, facebook y gmail
        //***********Falata agregar la de appple
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enlace("https://m.facebook.com/login/?locale=es_ES");
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enlace("https://accounts.google.com/v3/signin/identifier?continue=https%3A%2F%2Fmail.google.com%2Fmail%2Fu%2F0%2F&emr=1&followup=https%3A%2F%2Fmail.google.com%2Fmail%2Fu%2F0%2F&ifkv=ASKXGp0GUVEa7ZtQyUrjM_cTgYYCRQyBuqG_jJ9bIz0rh0tVEJW8tub73nRJgd8GWBCGc1O-WRXrrQ&osid=1&passive=1209600&service=mail&flowName=GlifWebSignIn&flowEntry=ServiceLogin&dsh=S-1174382267%3A1706773316759199&theme=glif");
            }
        });

    }
    private void valida(String emailText) {
        boolean isValid = Patterns.EMAIL_ADDRESS.matcher(emailText).matches();

        if (!isValid) {
            // Si no es válido, cambia el color del texto a rojo
            email.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            inicio.setEnabled(false); // Deshabilita el botón de inicio mientras el correo no sea válido
        } else {
            // Si es válido, restaura el color del texto y habilita el botón de inicio
            email.setTextColor(getResources().getColor(android.R.color.black));
            inicio.setEnabled(true);
        }
    }
    public void openPassword(View v) {
        Intent intent = new Intent(Register.this, Password.class);
        startActivity(intent);
    }
    private void enlace(String enlace) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(enlace));
        startActivity(browserIntent);
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