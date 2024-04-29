package com.proyectointegrador.madridindustria;

import com.google.firebase.firestore.*;

import java.util.Objects;
public class FirestoreDatabase {
    private String nombre, inaguracion, metro, direccion, descripcion, imagen, distrito, patrimonio, id;
    private GeoPoint geo;

    public FirestoreDatabase(String collectionPath, String documentPath, final FirestoreCallback callback){
        // INICIALIZAR FIREBASE
        FirebaseFirestore.getInstance().collection(collectionPath).document(documentPath).get().addOnCompleteListener(task -> {
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
                }
            }

            if (callback != null) {
                callback.onCallback(FirestoreDatabase.this);
            }
        });
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
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