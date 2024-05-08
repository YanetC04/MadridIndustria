package com.proyectointegrador.madridindustria;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;


import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private final String[] distritos = {"arganzuela", "centro", "moncloa", "chamberi", "chamartin", "sanblas", "villaverde", "barajas", "fuencarral", "hortaleza", "latina", "retiro", "salamanca", "sanblas", "tetuan", "vallecas", "villaverde"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSharedPreferences("ModoApp", Context.MODE_PRIVATE).contains("esEspanol")){
            setLocale(getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esEspanol", true) ? "es" : "en");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        checkLocationPermission();
        initMap();
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
                    showDialog();
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
                    intent = new Intent(Map.this, Profile.class).putExtra("source", source);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Evitar que MainActivity vuelva atrás a Splash.java
        // No llames al super.onBackPressed();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        if (isNightMode) {
            setMapStyle();
        }

        if (ContextCompat.checkSelfPermission(Map.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation();
        }
    }

    private void setMapStyle() {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_dark));
            if (!success) {
                Log.e("DARKMODE_MAP", "Error al cargar el estilo de mapa oscuro.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("DARKMODE_MAP", "No se pudo encontrar el recurso de estilo de mapa oscuro.", e);
        }
    }

    private void showCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Map.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(Map.this, location -> {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                drawMarkers(latLng);
            }
        });
    }

    private void drawMarkers(LatLng origen) {
        for (String dist : distritos) {
            getCount(dist, count -> {
                for (int i = 1; i <= count; i++) {
                    new FirestoreDatabase(dist, String.valueOf(i), firestoreDatabase -> {
                        if (firestoreDatabase.getGeo() != null) {
                            LatLng markerLatLng = new LatLng(firestoreDatabase.getGeo().getLatitude(), firestoreDatabase.getGeo().getLongitude());
                            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker);
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(markerLatLng)
                                    .title(dist)
                                    .icon(icon));
                        }
                    });
                }
            });
        }
    }

    private void addMarker(LatLng position, String title, String snippet, LatLng origen) {
        Drawable drawable = ContextCompat.getDrawable(Map.this, R.drawable.marker);
        if (drawable != null) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable));

            // Crear y agregar el marcador al mapa
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .snippet(snippet)
                    .title(title)
                    .icon(icon));

            // Configurar el listener para el clic en el marcador
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    calcularYMostrarRuta(origen, marker.getPosition());
                    return true;
                }
            });
        } else {
            Log.e("Map", "Error al obtener el VectorDrawable del marcador.");
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void getCount(String dist, final CountCallback countCallback) {
        FirebaseFirestore.getInstance().collection(dist).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size();
                countCallback.onCallback(count);
            } else {
                countCallback.onCallback(-1);
            }
        });
    }

    private void calcularYMostrarRuta(LatLng origen, LatLng destino) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCG3YHX-TT69TpQq3R1cw_u3p8h66nFpS4")
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(origen.latitude, origen.longitude))
                .destination(new com.google.maps.model.LatLng(destino.latitude, destino.longitude))
                .mode(TravelMode.DRIVING);

        request.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                if (result.routes != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0];
                    PolylineOptions polylineOptions = new PolylineOptions();

                    for (DirectionsLeg leg : route.legs) {
                        for (DirectionsStep step : leg.steps) {
                            EncodedPolyline points = step.polyline;
                            List<com.google.maps.model.LatLng> decodedPath = points.decodePath();
                            for (com.google.maps.model.LatLng latLng : decodedPath) {
                                polylineOptions.add(new LatLng(latLng.lat, latLng.lng));
                            }
                        }
                    }

                    runOnUiThread(() -> {
                        if (googleMap != null) {
                            googleMap.addPolyline(polylineOptions);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
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
                .setInterval(5000);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    // Diálogo de error
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.gest))
                .setMessage(getResources().getString(R.string.mGestor))
                .setPositiveButton(getResources().getString(R.string.si), (dialog, which) -> {
                    startActivity(new Intent(Map.this, Hall.class));
                    overridePendingTransition(0, 0);
                })
                .setNegativeButton("NO", null);

        builder.create().show();
    }

    interface CountCallback {
        void onCallback(int count);
    }

    private void setLocale(String idioma) {
        Locale nuevoLocale = new Locale(idioma);
        Locale.setDefault(nuevoLocale);

        Configuration configuracion = this.getResources().getConfiguration();
        configuracion.setLocale(nuevoLocale);

        getBaseContext().getResources().updateConfiguration(configuracion, getBaseContext().getResources().getDisplayMetrics());
    }
}