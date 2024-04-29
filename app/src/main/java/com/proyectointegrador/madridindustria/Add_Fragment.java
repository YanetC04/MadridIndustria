package com.proyectointegrador.madridindustria;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.*;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.*;

import java.text.Normalizer;
import java.util.*;

public class Add_Fragment extends Fragment {

    private Spinner distrito;
    private Drawable redBorderDrawable, defaultBorderDrawable;
    private EditText nombreEditText, inaguracionEditText, patrimonioEditText,coordenadas_latEditText, coordenadas_lonEditText,  metroEditText, direccionEditText, descripcionEditText, imagenEditText;
    private TextInputLayout nombreInputLayout, inaguracionInputLayout, patrimonioInputLayout, coordenadas_latInputLayout, coordenadas_lonInputLayout, metroInputLayout, direccionInputLayout, imagenInputLayout, descripcionInputLayout;
    private String nombreText, inaguracionText, patrimonioText,coordenadas_latText, coordenadas_lonText,  metroText, direccionText, descripcionText, distritoText, imagenText, dist = null;

    public Add_Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        Button enviar = root.findViewById(R.id.enviar);
        imagenEditText = root.findViewById(R.id.imagen);
        nombreEditText = root.findViewById(R.id.nombre);
        inaguracionEditText = root.findViewById(R.id.inaguracion);
        patrimonioEditText = root.findViewById(R.id.patrimonio);
        coordenadas_latEditText = root.findViewById(R.id.coordenadas_lat);
        coordenadas_lonEditText = root.findViewById(R.id.coordenadas_lon);
        metroEditText = root.findViewById(R.id.metro);
        distrito = root.findViewById(R.id.distrito);
        direccionEditText = root.findViewById(R.id.direccion);
        descripcionEditText = root.findViewById(R.id.descripcion);
        imagenInputLayout = root.findViewById(R.id.input_imagen);
        nombreInputLayout = root.findViewById(R.id.input_nombre);
        inaguracionInputLayout = root.findViewById(R.id.input_inaguracion);
        patrimonioInputLayout = root.findViewById(R.id.input_patrimonio);
        coordenadas_latInputLayout = root.findViewById(R.id.input_coordenadas_lat);
        coordenadas_lonInputLayout = root.findViewById(R.id.input_coordenadas_lon);
        metroInputLayout = root.findViewById(R.id.input_metro);
        direccionInputLayout = root.findViewById(R.id.input_direccion);
        descripcionInputLayout = root.findViewById(R.id.input_descripcion);
        redBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.default_border);

        // WEBVIEW
        WebView webView = root.findViewById(R.id.webView);
        webView.setEnabled(false);
        webView.setBackgroundColor(Color.GRAY);

        imagenEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String html = "<html><body style='margin:0; padding:0;'><img style='object-fit: contain; width:100%; height:100%;' src='" + imagenEditText.getText().toString() + "' /></body></html>";
                webView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));
                webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            }
        });

        // SPINNER DISTRITOS
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(), R.array.distritos, android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distrito.setAdapter(adapter);

        //SPINNER DISTRITOS
        distrito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if (position!=0) {
                    distrito.setBackground(defaultBorderDrawable);
                    distritoText = distrito.getSelectedItem().toString();
                    int pos = (new Locale("es").getDisplayLanguage().equalsIgnoreCase(distritoText)) ? 1 : 0;
                    String[] palabras = distritoText.split("\\s+");
                    if (palabras.length >= 2) {
                        String distritoNombre = palabras[pos].toLowerCase();
                        distritoNombre = quitarAcentos(distritoNombre);

                        if (distritoNombre.equals("san")) {
                            dist = "sanblas";
                        } else {
                            dist = distritoNombre;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // ENVIAR
        enviar.setOnClickListener(v -> {
            imagenText = imagenEditText.getText().toString();
            nombreText = nombreEditText.getText().toString();
            inaguracionText = inaguracionEditText.getText().toString();
            patrimonioText = patrimonioEditText.getText().toString();
            coordenadas_latText = coordenadas_latEditText.getText().toString();
            coordenadas_lonText = coordenadas_lonEditText.getText().toString();
            metroText = metroEditText.getText().toString();
            direccionText = direccionEditText.getText().toString();
            descripcionText = descripcionEditText.getText().toString();

            double latitude = 0.0;
            double longitude = 0.0;

            if (!coordenadas_latText.isEmpty() && !coordenadas_lonText.isEmpty()) {
                try {
                    latitude = Double.parseDouble(coordenadas_latText);
                    longitude = Double.parseDouble(coordenadas_lonText);
                } catch (NumberFormatException e) {
                    showErrorDialog(getResources().getString(R.string.valorInv));
                    return;
                }
            } else {
                marcarError(coordenadas_latText, coordenadas_latInputLayout, R.string.lat, coordenadas_latEditText);
                marcarError(coordenadas_lonText, coordenadas_lonInputLayout, R.string.lon, coordenadas_lonEditText);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (distrito.getSelectedItemPosition() != 0) {
                if (!imagenText.isEmpty() && !nombreText.isEmpty() && !inaguracionText.isEmpty() && !patrimonioText.isEmpty() && !metroText.isEmpty() && !direccionText.isEmpty() && !descripcionText.isEmpty() && !dist.isEmpty()) {
                    HashMap<String, Object> datos = new HashMap<>();
                    datos.put("imagen", imagenText);
                    datos.put("nombre", nombreText);
                    datos.put("inaguracion", inaguracionText);
                    datos.put("patrimonio", patrimonioText);
                    if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
                        datos.put("geo", new GeoPoint(latitude, longitude));
                    } else {
                        showErrorDialog(getResources().getString(R.string.valorInv));
                        return;
                    }
                    datos.put("metro", metroText);
                    datos.put("direccion", direccionText);
                    datos.put("descripcion", descripcionText);
                    datos.put("distrito", obtenerDistrito());

                    // BUSCAR SI EXISTE EN LA BD UN NOMBRE SIMILAR
                    db.collection(dist).whereEqualTo("nombre", nombreText).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                showErrorDialog(getResources().getString(R.string.patya));
                            } else {
                                getCount(dist, count -> {
                                    int nuevoID = count + 1;

                                    // ESTABLECER NUEVOS DATOS
                                    db.collection(dist).document(String.valueOf(nuevoID)).set(datos);
                                });

                                new AlertDialog.Builder(requireActivity())
                                        .setTitle(getResources().getString(R.string.ag))
                                        .setMessage(getResources().getString(R.string.agr))
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            // LIMPIAR INPUTS
                                            imagenEditText.getText().clear();
                                            nombreEditText.getText().clear();
                                            inaguracionEditText.getText().clear();
                                            patrimonioEditText.getText().clear();
                                            coordenadas_latEditText.getText().clear();
                                            coordenadas_lonEditText.getText().clear();
                                            metroEditText.getText().clear();
                                            direccionEditText.getText().clear();
                                            descripcionEditText.getText().clear();
                                            distrito.setSelection(0);
                                            dist="";
                                        })
                                        .show();
                            }
                        }
                    });
                } else {
                    marcarError(imagenText, imagenInputLayout, R.string.ima, imagenEditText);
                    marcarError(nombreText, nombreInputLayout, R.string.nom, nombreEditText);
                    marcarError(inaguracionText, inaguracionInputLayout, R.string.ina, inaguracionEditText);
                    marcarError(patrimonioText, patrimonioInputLayout, R.string.pat, patrimonioEditText);
                    marcarError(metroText, metroInputLayout, R.string.met, metroEditText);
                    marcarError(direccionText, direccionInputLayout, R.string.dir, direccionEditText);
                    marcarError(descripcionText, descripcionInputLayout, R.string.des, descripcionEditText);
                }
            } else {
                showErrorDialog(getResources().getString(R.string.selecDist));
                distrito.setBackground(redBorderDrawable);
            }

        });

        return root;
    }

    private String obtenerDistrito() {
        String distritoNombre = distritoText.split("\\s+")[(new Locale("es").getDisplayLanguage().equalsIgnoreCase(distritoText)) ? 1 : 0];
        distritoNombre = quitarAcentos(distritoNombre);

        if (distritoNombre.equals("San")) {
            return "Distrito San Blas-Canillejas";
        }

        return "Distrito " +distritoNombre;
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
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
                countCallback.onCallback(-1);
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