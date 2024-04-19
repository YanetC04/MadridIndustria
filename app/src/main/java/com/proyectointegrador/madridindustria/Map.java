package com.proyectointegrador.madridindustria;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import android.graphics.Canvas;


import androidx.annotation.NonNull;
import androidx.appcompat.app.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;

import java.util.Objects;

public class Map extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
        private LocationCallback locationCallback;
        private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde", "barajas", "fuencarral", "hortaleza", "latina", "retiro", "salamanca", "sanblas", "tetuan", "vallecas", "villaverde"};

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
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Intent intent = null;
                String source = getIntent().getStringExtra("source");

                if (item.getItemId() == R.id.home) {
                    intent = new Intent(Map.this, MainActivity.class).putExtra("source", source);
                }
                if (item.getItemId() == R.id.add) {
                    if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){

                        showDialog(Add.class);
                } else {
                    intent = new Intent(Map.this, Add.class);
                }
            }
            if (item.getItemId() == R.id.like) {
                intent = new Intent(Map.this, Favorite.class).putExtra("source", source);
            }
                if (item.getItemId() == R.id.profile) {
                    if(Objects.requireNonNull(source).equalsIgnoreCase("cerrado")){
                        intent = new Intent(Map.this, Profile.class).putExtra("source", "cerrado");
                    } else {
                        intent = new Intent(Map.this, Profile.class).putExtra("source", source);;
                    }
                }

            if (intent != null) {
                startActivity(intent);
                // Sin transición
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    // Diálogo de error
    private void showDialog(Class intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Modo Gestor")
                .setMessage("¿Quieres activar el modo Gestor?")
                .setPositiveButton("SÍ", (dialog, which) -> {
                    startActivity(new Intent(Map.this, Hall.class).putExtra("intent", intent.getName()));
                    overridePendingTransition(0, 0);
                })
                .setNegativeButton("NO", null);

        // Creación y visualización del diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // NO VOLVER ATRAS
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(map -> {
            googleMap = map;

            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

            // Aplicar el estilo de mapa oscuro solo si el dispositivo está en modo oscuro
            if (isNightMode) {
                try {
                    boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_dark));
                    if (!success) {
                        Log.e("DARKMODE_MAP", "Error al cargar el estilo de mapa oscuro.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("DARKMODE_MAP", "No se pudo encontrar el recurso de estilo de mapa oscuro. Error: ", e);
                }
            }

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
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(Map.this, location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                });
            }

            // PONEMOS LOS MARCADORES
            for (String dist : distritos) {
                getCount(dist, count -> {
                    for (int i = 1; i <= count; i++) {
                        String numero = String.valueOf(i);
                        new FirestoreDatabase(dist, numero, firestoreDatabase -> {
                            if (firestoreDatabase.getGeo() != null) {
                                GeoPoint geo = firestoreDatabase.getGeo();
                                LatLng latLng = new LatLng(geo.getLatitude(), geo.getLongitude());

                                // Crear un VectorDrawable para la imagen del marcador
                                Drawable drawable = ContextCompat.getDrawable(Map.this, R.drawable.marker);
                                if (drawable != null) {
                                    // Convertir el VectorDrawable a un BitmapDescriptor
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable));

                                    // Agregar el marcador al mapa
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .snippet(numero)
                                            .title(dist)
                                            .icon(icon));

                                    googleMap.setOnMarkerClickListener(marker -> {
                                        Log.e("coleccion", Objects.requireNonNull(marker.getTitle()));
                                        Log.e("documento", Objects.requireNonNull(marker.getSnippet()));
                                        Intent intent = new Intent(Map.this, Patrimonio.class);
                                        intent.putExtra("collection", marker.getTitle());
                                        intent.putExtra("document", marker.getSnippet());
                                        startActivity(intent);
                                        return true;
                                    });
                                } else {
                                    Log.e("Map", "Error al obtener el VectorDrawable del marcador.");
                                }
                            }
                        });
                    }
                });
            }

        });
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
            public void onLocationResult(@NonNull LocationResult locationResult) {
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
}