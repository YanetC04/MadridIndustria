package com.proyectointegrador.madridindustria;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.*;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.*;

import java.text.Normalizer;

public class Delete_Fragment extends Fragment {
    private GridLayout gridLayout;
    private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde", "retiro", "tetuan", "fuencarral", "vallecas", "barajas", "hortaleza", "latina", "salamanca"};
    private Drawable redBorderDrawable, defaultBorderDrawable;
    private TextInputLayout nombreInputLayout;
    private EditText nombre;
    private String dis = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_edit, container, false);
        gridLayout = root.findViewById(R.id.gridLayout);
        nombre = root.findViewById(R.id.nombre);
        nombreInputLayout = root.findViewById(R.id.input_nombre);
        ImageView imagen = root.findViewById(R.id.imagen);
        redBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.default_border);

        imagen.setOnClickListener(v -> {
            // Eliminar las filas adicionales (a partir del índice 1) del GridLayout
            int childCount = gridLayout.getChildCount();
            if (childCount > 2) { // Verificar que haya más de una fila (excluyendo la primera fila)
                gridLayout.removeViews(2, childCount - 2);
            }

            for (String dist : distritos) {
                FirebaseFirestore.getInstance().collection(dist)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null) {
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        String nombreF = document.getString("nombre");
                                        String parteDelNombreABuscar = nombre.getText().toString();

                                        if (nombreF.toLowerCase().contains(parteDelNombreABuscar.toLowerCase())) {
                                            agregarFila(document);
                                        } else {
                                            marcarError(nombreF, nombreInputLayout, R.string.nom, nombre);
                                        }
                                    }
                                }
                            }
                        });
            }
        });

        return root;
    }

    private void agregarFila(QueryDocumentSnapshot document) {
        TextView nombreTextView = new TextView(getContext());
        nombreTextView.setText(document.getString("nombre"));
        nombreTextView.setTextSize(16);
        TextView distritoTextView = new TextView(getContext());
        distritoTextView.setText(document.getString("distrito"));
        distritoTextView.setTextSize(16);

        gridLayout.addView(nombreTextView);
        gridLayout.addView(distritoTextView);

        nombreTextView.setOnClickListener(v -> {
            String[] palabras = document.getString("distrito").split("\\s+");

            if (palabras.length >= 2) {
                String distritoNombre = palabras[1].toLowerCase();
                distritoNombre = quitarAcentos(distritoNombre);

                if (distritoNombre.equals("san")) {
                    dis = "sanblas";
                } else {
                    dis = distritoNombre;
                }
            }

            new AlertDialog.Builder(requireActivity())
                    .setTitle(getResources().getString(R.string.elim))
                    .setMessage(getResources().getString(R.string.eliminar))
                    .setPositiveButton(getResources().getString(R.string.si), (dialog, which) -> FirebaseFirestore.getInstance().collection(dis)
                            .whereEqualTo("nombre", document.getString("nombre"))
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document2 : task.getResult()) {
                                        String documentId = document2.getId();
                                        FirebaseFirestore.getInstance().collection(dis)
                                                .document(documentId)
                                                .delete();
                                        break;
                                    }
                                    recargarFragment();

                                } else {
                                    recargarFragment();
                                }
                            })).setNegativeButton("NO", (dialog, which) -> {

                    })
                    .show();
        });
    }

    private void recargarFragment(){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new Delete_Fragment());
        fragmentTransaction.commit();
    }

    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private void marcarError(String text, TextInputLayout input, int valor, EditText edit){
        if (text.isEmpty()) {
            input.setHint(valor);
            edit.setBackground(redBorderDrawable);
            edit.setOnFocusChangeListener((v19, hasFocus) -> {
                if(!hasFocus){
                    edit.setBackground(defaultBorderDrawable);
                    input.setHint(valor);
                }
            });
        }
    }
}