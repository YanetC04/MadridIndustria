package com.proyectointegrador.madridindustria;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.*;

import android.provider.MediaStore;
import android.text.*;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.Normalizer;
import java.util.*;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;


public class Edit_Fragment extends Fragment {

    private GridLayout gridLayout;
    private LinearLayout linear;
    private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde", "retiro", "tetuan", "fuencarral", "vallecas", "barajas", "hortaleza", "latina", "salamanca"};
    private Drawable redBorderDrawable, defaultBorderDrawable;
    private EditText nombre, nombreEditText, inaguracionEditText, patrimonioEditText, coordenadas_latEditText, coordenadas_lonEditText, metroEditText, direccionEditText, descripcionEditText, imagenEditText;
    private TextInputLayout nombreInputLayout, imagenInputLayout, descripcionInputLayout;
    private Spinner distritoT;
    private String dis = null;
    private WebView webView;
    private Uri fileUri;
    private static final int GALLERY_REQUEST_CODE = 123;
    private FloatingActionButton fab;

    public Edit_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit, container, false);
        gridLayout = root.findViewById(R.id.gridLayout);
        linear = root.findViewById(R.id.linear);
        nombre = root.findViewById(R.id.nombre);
        nombreInputLayout = root.findViewById(R.id.input_nombre);
        ImageView imagen = root.findViewById(R.id.imagen);
        redBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.default_border);

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

        return root;
    }

    private void realizarBusqueda() {
        gridLayout.removeAllViews();
        // Eliminar las filas adicionales (a partir del índice 1) del GridLayout
        int childCount = gridLayout.getChildCount();
        if (childCount > 2) { // Verificar que haya más de una fila (excluyendo la primera fila)
            gridLayout.removeViews(2, childCount - 2);
        }

        String parteDelNombreABuscar = normalizarTexto(nombre.getText().toString());

        for (String dist : distritos) {
            getCount(dist, count -> {
                for (int i = 1; i <= count; i++) {
                    new FirestoreDatabase(dist, Integer.toString(i), firestoreDatabase -> {
                        String nombreF = firestoreDatabase.getNombre();
                        String nombreFNormalizado = normalizarTexto(nombreF);

                        if (nombreFNormalizado.contains(parteDelNombreABuscar)) {
                            agregarFila(firestoreDatabase);
                        } else {
                            marcarError(nombreF, nombreInputLayout, R.string.nom, nombre);
                        }
                    });
                }
            });
        }
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

    private void agregarFila(FirestoreDatabase firestoreDatabase) {
        LinearLayout cardView = (LinearLayout) getLayoutInflater().inflate(R.layout.favorite_card, null);

        // Obtener los elementos de la card
        TextView nombreTextView = cardView.findViewById(R.id.nombre);
        TextView inauguracionTextView = cardView.findViewById(R.id.inaguracion);
        TextView patrimonioTextView = cardView.findViewById(R.id.patrimonio);
        TextView metroTextView = cardView.findViewById(R.id.metro);
        TextView direccionTextView = cardView.findViewById(R.id.direccion);
        ImageView imagenView = cardView.findViewById(R.id.imagen); // Asegúrate de que el ImageView tenga el ID correcto

        // Configurar los valores de los elementos de la card
        nombreTextView.setText(firestoreDatabase.getNombre());
        inauguracionTextView.setText(firestoreDatabase.getInaguracion());
        patrimonioTextView.setText(firestoreDatabase.getPatrimonio());
        metroTextView.setText(firestoreDatabase.getMetro());
        direccionTextView.setText(firestoreDatabase.getDireccion());

        // Utilizar Glide para cargar la imagen desde la URL proporcionada
        Glide.with(this)
                .load(firestoreDatabase.getImagen())
                .into(imagenView);

        // Añadir la card configurada al GridLayout
        gridLayout.addView(cardView);

        cardView.setOnClickListener(v -> {
            linear.setVisibility(View.GONE);
            gridLayout.setVisibility(View.GONE);

            View root = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add, null);

            Button enviar = root.findViewById(R.id.enviar);
            imagenEditText = root.findViewById(R.id.imagen);
            nombreEditText = root.findViewById(R.id.nombre);
            inaguracionEditText = root.findViewById(R.id.inaguracion);
            patrimonioEditText = root.findViewById(R.id.patrimonio);
            coordenadas_latEditText = root.findViewById(R.id.coordenadas_lat);
            coordenadas_lonEditText = root.findViewById(R.id.coordenadas_lon);
            metroEditText = root.findViewById(R.id.metro);
            distritoT = root.findViewById(R.id.distrito);
            direccionEditText = root.findViewById(R.id.direccion);
            descripcionEditText = root.findViewById(R.id.descripcion);
            imagenInputLayout = root.findViewById(R.id.input_imagen);
            descripcionInputLayout = root.findViewById(R.id.input_descripcion);
            redBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.red_border);
            defaultBorderDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.default_border);

            fab = root.findViewById(R.id.boton);

            fab.setOnClickListener(view -> openGallery());

            // WEBVIEW
            webView = root.findViewById(R.id.webView);
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
            distritoT.setAdapter(adapter);

            // ESTABLECER TEXTOS
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

            int disPos = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                String distritoA = Objects.requireNonNull(adapter.getItem(i)).toString();
                if (distritoA.startsWith(obtenerDistrito(firestoreDatabase.getDistrito()))) {
                    disPos = i;
                }
            }

            double lat = firestoreDatabase.getGeo().getLatitude();
            double lon = firestoreDatabase.getGeo().getLongitude();
            imagenEditText.setText(firestoreDatabase.getImagen());
            nombreEditText.setText(firestoreDatabase.getNombre());
            inaguracionEditText.setText(firestoreDatabase.getInaguracion());
            patrimonioEditText.setText(firestoreDatabase.getPatrimonio());
            coordenadas_latEditText.setText(Double.toString(lat));
            coordenadas_lonEditText.setText(Double.toString(lon));
            metroEditText.setText(firestoreDatabase.getMetro());
            distritoT.setSelection(disPos);
            direccionEditText.setText(firestoreDatabase.getDireccion());
            descripcionEditText.setText(firestoreDatabase.getDescripcion());

            editEditable();

            enviar.setText(R.string.mod);

            enviar.setOnClickListener(v1 -> {
                if (!firestoreDatabase.getNombre().isEmpty()) {
                    // OBTENER VALORES ACTUALIZADOS
                    String nuevaImagen = imagenEditText.getText().toString();
                    String nuevoNombre = nombreEditText.getText().toString();
                    String nuevaInauguracion = inaguracionEditText.getText().toString();
                    String nuevoPatrimonio = patrimonioEditText.getText().toString();
                    double nuevaLatitud = Double.parseDouble(coordenadas_latEditText.getText().toString());
                    double nuevaLongitud = Double.parseDouble(coordenadas_lonEditText.getText().toString());
                    String nuevoMetro = metroEditText.getText().toString();
                    String nuevoDistrito = firestoreDatabase.getDistrito();
                    String nuevaDireccion = direccionEditText.getText().toString();
                    String nuevaDescripcion = descripcionEditText.getText().toString();

                    // AGREGAR DATOS
                    HashMap<String, Object> datos = new HashMap<>();
                    datos.put("imagen", nuevaImagen);
                    datos.put("nombre", nuevoNombre);
                    datos.put("inaguracion", nuevaInauguracion);
                    datos.put("patrimonio", nuevoPatrimonio);
                    datos.put("geo", new GeoPoint(nuevaLatitud, nuevaLongitud));
                    datos.put("metro", nuevoMetro);
                    datos.put("distrito", nuevoDistrito);
                    datos.put("direccion", nuevaDireccion);
                    datos.put("descripcion", nuevaDescripcion);

                    // ACTUALIZAR FIRESTORE
                    FirebaseFirestore.getInstance().collection(dis)
                            .whereEqualTo("nombre", nuevoNombre)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String documentId = document.getId();
                                        FirebaseFirestore.getInstance().collection(dis)
                                                .document(documentId)
                                                .update(datos);
                                        break;
                                    }
                                    new AlertDialog.Builder(requireActivity())
                                            .setTitle(getResources().getString(R.string.modif))
                                            .setMessage(getResources().getString(R.string.modi))
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                FragmentManager fragmentManager = getParentFragmentManager(); // requireFragmentManager
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.replace(R.id.fragment_container, new Edit_Fragment());
                                                fragmentTransaction.commit();

                                            })
                                            .show();
                                } else {
                                    showErrorDialog();
                                }
                            });
                } else {
                    marcarError(firestoreDatabase.getImagen(), imagenInputLayout, R.string.ima, imagenEditText);
                    marcarError(firestoreDatabase.getDescripcion(), descripcionInputLayout, R.string.des, descripcionEditText);
                }
            });


            // Agregar el layout de AddFragment al contenedor actual
            ViewGroup containerView = requireView().findViewById(R.id.linear_layout_container);
            containerView.addView(root);
        });
    }

    public static String quitarAcentos(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private void editEditable() {
        // NO
        nombreEditText.setEnabled(false);
        inaguracionEditText.setEnabled(false);
        patrimonioEditText.setEnabled(false);
        coordenadas_latEditText.setEnabled(false);
        coordenadas_lonEditText.setEnabled(false);
        metroEditText.setEnabled(false);
        distritoT.setEnabled(false);
        direccionEditText.setEnabled(false);
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Error")
                .setMessage(getResources().getString(R.string.patno))
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (fileUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + fileUri.getLastPathSegment());

            UploadTask uploadTask = imagesRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(imageUrl);
                    imagenEditText.setText(imageUrl);
                }).addOnFailureListener(exception -> {
                    // Error al obtener la URL de descarga
                });
            }).addOnFailureListener(exception -> {
                // Error al subir la imagen
            });
        }
    }

    private String obtenerDistrito(String distritoText) {
        if (!requireContext().getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esEspanol", true)) {
            String distritoNombre = distritoText.split("\\s+")[1];
            distritoNombre = quitarAcentos(distritoNombre);

            if (distritoNombre.equals("San")) {
                return "Distrito San Blas-Canillejas";
            }

            return distritoNombre + " District";
        }

        return distritoText;
    }

    public static String normalizarTexto(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "")  // Elimina todos los espacios en blanco
                .toLowerCase();
    }
}
