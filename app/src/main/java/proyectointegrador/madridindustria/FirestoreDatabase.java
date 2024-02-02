package proyectointegrador.madridindustria;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class FirestoreDatabase {
    private String collectionPath, documentPath, nombre, inaguracion, metro, direccion, descripcion, imagen, distrito, patrimonio;
    private FirebaseFirestore db;
    private DocumentReference patrimonioRef;

    public FirestoreDatabase(String collectionPath, String documentPath, final FirestoreCallback callback){
        this.collectionPath = collectionPath;
        this.documentPath = documentPath;

        // INICIALIZAR FIREBASE
        db = FirebaseFirestore.getInstance();

        patrimonioRef = db.collection(collectionPath).document(documentPath);
        patrimonioRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nombre = document.getString("nombre");
                        inaguracion = document.getString("inaguracion");
                        patrimonio = document.getString("patrimonio");
                        metro = document.getString("metro");
                        direccion = document.getString("direccion");
                        descripcion = document.getString("descripcion").replace("\\n", "\n\n");
                        imagen = document.getString("imagen");
                        distrito = document.getString("distrito");
                    } else {
                        // El documento no existe
                        Log.d("FirestoreData", "Documento no encontrado");
                    }
                } else {
                    // Error al leer el documento
                    Log.e("FirestoreData", "Error al leer datos: " + task.getException().getMessage());
                }

                if (callback != null) {
                    callback.onCallback(FirestoreDatabase.this);
                }
            }
        });
    }

    public String getCollectionPath() {
        return collectionPath;
    }

    public void setCollectionPath(String collectionPath) {
        this.collectionPath = collectionPath;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInaguracion() {
        return inaguracion;
    }

    public void setInaguracion(String inaguracion) {
        this.inaguracion = inaguracion;
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

    public void setMetro(String metro) {
        this.metro = metro;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

}
