package com.proyectointegrador.madridindustria;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;


public class FirestoreDatabase {
    private String collectionPath, documentPath, nombre, inaguracion, metro, direccion, descripcion, imagen, distrito, patrimonio, mail, pass, like, id;
    private GeoPoint geo;

    public FirestoreDatabase(String collectionPath, String documentPath, final FirestoreCallback callback){
        this.collectionPath = collectionPath;
        this.documentPath = documentPath;

        // INICIALIZAR FIREBASE
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference patrimonioRef = db.collection(collectionPath).document(documentPath);
        patrimonioRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    nombre = document.getString("nombre");
                    inaguracion = document.getString("inaguracion");
                    patrimonio = document.getString("patrimonio");
                    metro = document.getString("metro");
                    direccion = document.getString("direccion");
                    descripcion = (document.getString("descripcion") != null) ? Objects.requireNonNull(document.getString("descripcion")).replace("\\n", "\n\n"): "";
                    imagen = document.getString("imagen");
                    distrito = document.getString("distrito");
                    geo = document.getGeoPoint("geo");
                } else {
                    // El documento no existe
                    Log.d("FirestoreData", "Documento no encontrado");
                }
            } else {
                // Error al leer el documento
                Log.e("FirestoreData", "Error al leer datos: " + Objects.requireNonNull(task.getException()).getMessage());
            }

            if (callback != null) {
                callback.onCallback(FirestoreDatabase.this);
            }
        });
    }

    public String getNombre() {
        return nombre;
    }

    public String getInaguracion() {
        return inaguracion;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(String patrimonio) {
        this.patrimonio = patrimonio;
    }

    public String getMetro() {
        return metro;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public String getDistrito() {
        return distrito;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoPoint getGeo() {
        return geo;
    }
}