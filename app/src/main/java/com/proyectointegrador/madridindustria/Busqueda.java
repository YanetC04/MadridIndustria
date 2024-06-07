package com.proyectointegrador.madridindustria;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;

public class Busqueda extends AppCompatActivity {
    private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde", "retiro", "tetuan", "fuencarral", "vallecas", "barajas", "hortaleza", "latina", "salamanca"};
    private GridLayout gridLayout;
    private LinearLayout linear;
    private EditText nombre;
    private TextInputLayout nombreInputLayout;
    private Drawable redBorderDrawable, defaultBorderDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit);
        gridLayout = findViewById(R.id.gridLayout);
        linear = findViewById(R.id.linear);
        nombre = findViewById(R.id.nombre);
        nombreInputLayout = findViewById(R.id.input_nombre);
        ImageView imagen = findViewById(R.id.imagen);
        redBorderDrawable = ContextCompat.getDrawable(this, R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(this, R.drawable.default_border);

        int marginInDp = 16;
        float scale = getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * scale + 0.5f);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linear.getLayoutParams();

        if (layoutParams != null) {
            layoutParams.setMargins(marginInPx, 16, marginInPx, 10);
            linear.setLayoutParams(layoutParams);
            gridLayout.setLayoutParams(layoutParams);
        }

        imagen.setOnClickListener(v -> realizarBusqueda());

        // Agregar el listener al EditText para capturar la acción del Enter
        nombre.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                realizarBusqueda();
                return true;
            }
            return false;
        });
    }

    private void realizarBusqueda() {
        // Eliminar las filas adicionales (a partir del índice 1) del GridLayout
        int childCount = gridLayout.getChildCount();
        if (childCount > 2) { // Verificar que haya más de una fila (excluyendo la primera fila)
            gridLayout.removeViews(2, childCount - 2);
        }

        String parteDelNombreABuscar = normalizarTexto(nombre.getText().toString());

        for (String dist : distritos) {
            getCount(dist, count -> {
                for (int i = 1; i <= count; i++) {
                    int finalI = i;
                    new FirestoreDatabase(dist, Integer.toString(i), firestoreDatabase -> {
                        String nombreF = firestoreDatabase.getNombre();
                        String nombreFNormalizado = normalizarTexto(nombreF);

                        if (nombreFNormalizado.contains(parteDelNombreABuscar)) {
                            agregarFila(firestoreDatabase, Integer.toString(finalI));
                        } else {
                            marcarError(nombreF, nombreInputLayout, R.string.nom, nombre);
                        }
                    });
                }
            });
        }
    }


    private void agregarFila(FirestoreDatabase firestoreDatabase, String document) {
        TextView nombreTextView = new TextView(this);
        nombreTextView.setText(firestoreDatabase.getNombre());
        nombreTextView.setTextSize(16);
        TextView distritoTextView = new TextView(this);
        distritoTextView.setText(firestoreDatabase.getDistrito());
        distritoTextView.setTextSize(16);

        gridLayout.addView(nombreTextView);
        gridLayout.addView(distritoTextView);

        nombreTextView.setOnClickListener(v -> {
            linear.setVisibility(View.GONE);
            gridLayout.setVisibility(View.GONE);

            startActivity(new Intent(Busqueda.this, Patrimonio.class).putExtra("collection", obtenerDistrito(firestoreDatabase.getDistrito())).putExtra("document", document).putExtra("source", getIntent().getStringExtra("source")));
        });
    }

    public void getCount(String dist, final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection(dist).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size();
                countCallback.onCallback(count);
            } else {
                countCallback.onCallback(-1);
            }
        });
    }

    private String obtenerDistrito(String distritoValor) {
        String[] palabras = distritoValor.split("\\s+");

        String distritoNombre = null;
        if (palabras.length >= 2) {
            distritoNombre = palabras[1].toLowerCase();
            distritoNombre = quitarAcentos(distritoNombre);

            if (distritoNombre.equals("san")) {
                return  "sanblas";
            }
        }

        return distritoNombre;
    }

    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public static String normalizarTexto(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "")  // Elimina todos los espacios en blanco
                .toLowerCase();
    }


    private void marcarError(String text, TextInputLayout input, int valor, EditText edit) {
        if (text.isEmpty()) {
            input.setHint(valor);
            edit.setBackground(redBorderDrawable);
            edit.setOnFocusChangeListener((v19, hasFocus) -> {
                if (!hasFocus) {
                    edit.setBackground(defaultBorderDrawable);
                    input.setHint(valor);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        linear.setVisibility(View.VISIBLE);
        gridLayout.setVisibility(View.VISIBLE);
    }
}
