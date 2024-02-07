package proyectointegrador.madridindustria;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


public class FirestoreDatabase {
    private String collectionPath, documentPath, nombre, inaguracion, metro, direccion, descripcion, imagen, distrito, patrimonio, mail, pass, like, id;
    private GeoPoint geo;
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
                        mail = document.getString("mail");
                        pass = document.getString("password");
                        nombre = document.getString("nombre");
                        inaguracion = document.getString("inaguracion");
                        patrimonio = document.getString("patrimonio");
                        metro = document.getString("metro");
                        direccion = document.getString("direccion");
                        descripcion = (document.getString("descripcion") != null) ? document.getString("descripcion").replace("\\n", "\n\n"): "";
                        imagen = document.getString("imagen");
                        distrito = document.getString("distrito");
                        like = document.getString("like");
                        id = document.getString("id_patrimonio");
                        geo = document.getGeoPoint("geo");
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
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

    public void setGeo(GeoPoint geo) {
        this.geo = geo;
    }
}
