package com.proyectointegrador.madridindustria;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer;
import java.util.Objects;

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

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar las filas adicionales (a partir del índice 1) del GridLayout
                int childCount = gridLayout.getChildCount();
                if (childCount > 2) { // Verificar que haya más de una fila (excluyendo la primera fila)
                    gridLayout.removeViews(2, childCount - 2);
                }

                for (String dist : distritos) {
                    getCount(dist, count -> {
                        for (int i = 1; i <= count; i++) {
                            new FirestoreDatabase(dist, Integer.toString(i), firestoreDatabase -> {
                                String nombreF = firestoreDatabase.getNombre();
                                String parteDelNombreABuscar = nombre.getText().toString();

                                if (nombreF.toLowerCase().contains(parteDelNombreABuscar.toLowerCase())) {
                                    agregarFila(firestoreDatabase);
                                } else {
                                    marcarError(nombreF, nombreInputLayout, R.string.nom, nombre);
                                }
                            });
                        }
                    });
                }
            }
        });

        return root;
    }

    private void agregarFila(FirestoreDatabase firestoreDatabase) {
        TextView nombreTextView = new TextView(getContext());
        nombreTextView.setText(firestoreDatabase.getNombre());
        TextView distritoTextView = new TextView(getContext());
        distritoTextView.setText(firestoreDatabase.getDistrito());

        gridLayout.addView(nombreTextView);
        gridLayout.addView(distritoTextView);

        nombreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] palabras = firestoreDatabase.getDistrito().split("\\s+");

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
                        .setTitle("Eliminar")
                        .setMessage("¿Desea eliminar el patrimonio?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            FirebaseFirestore.getInstance().collection(dis)
                                    .whereEqualTo("nombre", firestoreDatabase.getNombre())
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String documentId = document.getId();
                                                FirebaseFirestore.getInstance().collection(dis)
                                                        .document(documentId)
                                                        .delete();
                                                break;
                                            }
                                            FragmentManager fragmentManager = requireFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.fragment_container, new Delete_Fragment());
                                            fragmentTransaction.commit();

                                        } else {
                                            FragmentManager fragmentManager = requireFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.fragment_container, new Delete_Fragment());
                                            fragmentTransaction.commit();
                                        }
                                    });

                        }).setNegativeButton("NO", (dialog, which) -> {

                        })
                        .show();
            }
        });
    }

    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public void getCount(String dist, final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection(dist).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size();
                countCallback.onCallback(count);
            } else {
                Log.e("FirestoreData", "Error getting document count: " + Objects.requireNonNull(task.getException()).getMessage());
                countCallback.onCallback(-1); // Indicates an error
            }
        });
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