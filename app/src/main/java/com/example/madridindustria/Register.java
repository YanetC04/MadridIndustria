package com.example.madridindustria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Register extends AppCompatActivity {
    private EditText correo, contraseña;
    private Button inicio, apple, facebook, gmail, olvidado;
    private TextView bienvenida, ini, text, o;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bienvenida = findViewById(R.id.bienvenida);
        ini = findViewById(R.id.ini);
        text = findViewById(R.id.text);
        contraseña = findViewById(R.id.contraseña);
        correo = findViewById(R.id.correo);
        inicio = findViewById(R.id.inicio);
        olvidado = findViewById(R.id.olvidado);
        o = findViewById(R.id.textView3);
        apple = findViewById(R.id.apple);
        facebook = findViewById(R.id.facebook);
        gmail = findViewById(R.id.gmail);
        imageView2 = findViewById(R.id.imageView2);

        ocultar();
        correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Oculta elementos cuando se escribe en el EditText de correo
                ocultarElementos();

            }
        });
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes manejar la lógica cuando se presiona el botón de inicio
                text.setVisibility(View.VISIBLE);
                contraseña.setVisibility(View.VISIBLE);
                correo.setVisibility(View.GONE);
                olvidado.setVisibility(View.VISIBLE);

            }
        });
        olvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Password.class);
                startActivity(intent);
            }
        });
    }


    private void ocultar() {
        ini.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        contraseña.setVisibility(View.GONE);
        olvidado.setVisibility(View.GONE);

    }

    private void ocultarElementos() {
        bienvenida.setVisibility(View.GONE);
        imageView2.setVisibility(View.GONE);
        ini.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        facebook.setVisibility(View.GONE);
        apple.setVisibility(View.GONE);
        gmail.setVisibility(View.GONE);
        o.setVisibility(View.GONE);
    }
}