package proyectointegrador.madridindustria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class Hall extends AppCompatActivity {
    private ImageView imagen;
    private Button inicio, registro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);

        imagen = findViewById(R.id.ministerio);
        inicio = findViewById(R.id.inicio);
        registro = findViewById(R.id.registro);

        // UTILIZAMOS GLIDE PARA CARGAR LA IMAGEN
        Glide.with(Hall.this)
                .load(R.drawable.ministerio)
                .into(imagen);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Hall.this, Login.class);
                startActivity(intent);
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Hall.this, Register.class);
                startActivity(intent);
            }
        });
    }
}