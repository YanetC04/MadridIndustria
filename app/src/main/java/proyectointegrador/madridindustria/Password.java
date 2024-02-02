package proyectointegrador.madridindustria;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Password extends AppCompatActivity {
    private TextView txt;
    private Button inicio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        txt =  findViewById(R.id.txt4);
        inicio =  findViewById(R.id.iniciar);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Password.this, Hall.class);
                startActivity(intent);
            }
        });
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.google.com/v3/signin/identifier?continue=https%3A%2F%2Fmail.google.com%2Fmail%2Fu%2F0%2F&emr=1&followup=https%3A%2F%2Fmail.google.com%2Fmail%2Fu%2F0%2F&ifkv=ASKXGp0GUVEa7ZtQyUrjM_cTgYYCRQyBuqG_jJ9bIz0rh0tVEJW8tub73nRJgd8GWBCGc1O-WRXrrQ&osid=1&passive=1209600&service=mail&flowName=GlifWebSignIn&flowEntry=ServiceLogin&dsh=S-1174382267%3A1706773316759199&theme=glif"));
                startActivity(browserIntent);
            }
        });

    }
}