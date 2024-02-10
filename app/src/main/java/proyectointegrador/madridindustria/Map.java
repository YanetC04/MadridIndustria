package proyectointegrador.madridindustria;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class Map extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private FirestoreDatabase fData;
    private double lat, lon;
    private String dis, nom;
    private int numerodoc;
    private String distritos[] = {"chamartin", "centro", "moncloa", "chamberi", "hortaleza", "arganzuela"};
    private ArrayList<LatLng> locationArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Comprueba y solicita permisos
        checkLocationPermission();

        // Inicializa el mapa
        initMap();

        // Inicializa la ubicación en tiempo real
        initLocationUpdates();

        // BARRA INFERIOR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = null;
                        String source = getIntent().getStringExtra("source");

                        if (item.getItemId() == R.id.home) {
                            intent = new Intent(Map.this, MainActivity.class).putExtra("source", source);
                        } else if (item.getItemId() == R.id.add) {
                            if (source.equalsIgnoreCase("password") || source.equalsIgnoreCase("add") || source.equalsIgnoreCase("profile"))
                                intent = new Intent(Map.this, Add.class);
                            else
                                showDialog("¿Quieres activar el modo Gestor?");
                        } else if (item.getItemId() == R.id.like) {
                            intent = new Intent(Map.this, Favorite.class).putExtra("source", source);
                        } else if (item.getItemId() == R.id.profile) {
                            if (source.equalsIgnoreCase("password") || source.equalsIgnoreCase("add") || source.equalsIgnoreCase("profile"))
                                intent = new Intent(Map.this, Profile.class);
                            else
                                showDialog("¿Quieres activar el modo Gestor?");
                        }

                        if (intent != null) {
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            return true;
                        }

                        return true;
                    }
                });
    }

    // Diálogo de error
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modo Gestor")
                .setMessage(message)
                .setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Map.this, Hall.class));
                        overridePendingTransition(0, 0);
                    }
                })
                .setNegativeButton("NO", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // NO VOLVER ATRAS
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                locationArrayList = new ArrayList<>();

                // Configura el mapa según tus necesidades
                if (ActivityCompat.checkSelfPermission(Map.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Map.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // Mueve la cámara a la ubicación actual del dispositivo
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Map.this);
                if (ContextCompat.checkSelfPermission(Map.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(Map.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }
                        }
                    });
                }

                // PONEMOS LOS MARCADORES
                for (String dist : distritos) {
                    getCount(dist, new CountCallback() {
                        @Override
                        public void onCallback(int count) {
                            // La variable i es el documento del distrito
                            for (int i = 1; i <= count; i++) {
                                final int numerodoc = i;
                                final String distrito = dist;
                                fData = new FirestoreDatabase(dist, String.valueOf(i), new FirestoreCallback() {
                                    @Override
                                    public void onCallback(FirestoreDatabase firestoreDatabase) {
                                        if (fData.getGeo() != null) {
                                            double lat = fData.getGeo().getLatitude();
                                            double lon = fData.getGeo().getLongitude();
                                            String dis = fData.getDistrito();
                                            String nom = fData.getNombre();

                                            Log.e("MARCADOR",lat + " " + lon);
                                            locationArrayList.add(new LatLng(lat, lon));
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                loadMarkers();
            }
        });
    }

    private void loadMarkers(){
        for (int i = 0; i < locationArrayList.size(); i++) {
            // below line is use to add marker to each location of our array list.
            googleMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title(""));
            /*googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // Abrir la actividad Patrimonio con los datos del distrito y el documento
                    startActivity(new Intent(Map.this, Patrimonio.class)
                            .putExtra("collection", distrito)
                            .putExtra("document", String.valueOf(numerodoc)));
                    return true;
                }
            });*/
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000); // Actualiza la ubicación cada 5 segundos (puedes ajustar esto según tus necesidades)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap();
                initLocationUpdates();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detén las actualizaciones de ubicación cuando la actividad se destruye
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    //Cuenta cuantos lugares hay dentro de un distrito
    public void getCount(String dist, final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection(dist).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = task.getResult().size();
                    countCallback.onCallback(count);
                } else {
                    Log.e("FirestoreData", "Error getting document count: " + task.getException().getMessage());
                    countCallback.onCallback(-1); // Indicates an error
                }
            }
        });
    }
}
