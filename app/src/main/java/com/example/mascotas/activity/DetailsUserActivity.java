package com.example.mascotas.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.mascotas.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailsUserActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,
        GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {


    TextView title,creator,description,time,cost,cup,date;
    Button btn_turno;


    int eventinscriptos = 0;
    String txtLatitud,txtLongitud, eventEstado;
    GoogleMap mMap;

    private FirebaseFirestore mfirestore;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private FirebaseAuth mAuth;


    ImageView photo_pet;
    LinearLayout linearLayout_image_btn;
    StorageReference storageReference;

    Query query;


    private void CreateTurn(String id){
        String TurnoId = mAuth.getCurrentUser().getUid() + id;
        query = mfirestore.collection("Turnos").whereEqualTo("TurnoId", TurnoId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "El Turno Ya esta registrado", Toast.LENGTH_SHORT).show();
                } else {
                    insertar(id);
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    private  void insertar(String id){
        mfirestore.collection("Eventos").document(id).get().addOnSuccessListener(documentSnapshot -> {
            String TurnoId = mAuth.getCurrentUser().getUid() + id;
            String idUser = mAuth.getCurrentUser().getUid();
            int eventinscriptos = Integer.parseInt(String.valueOf(documentSnapshot.getLong("inscriptos")));
            int eventCups = Integer.parseInt(String.valueOf(documentSnapshot.getLong("cupo")));
            if(eventCups == eventinscriptos)
            {
                Toast.makeText(getApplicationContext(), "Ya se lleno el cupo", Toast.LENGTH_SHORT).show();
            }else {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", idUser);
                map.put("id", documentSnapshot.getString("id"));
                map.put("titulo", documentSnapshot.getString("titulo"));
                map.put("fecha", documentSnapshot.getString("fecha"));
                map.put("horario", documentSnapshot.getString("horario"));
                map.put("descripcion", documentSnapshot.getString("descripcion"));
                map.put("estado", documentSnapshot.getString("estado"));
                map.put("photo", documentSnapshot.getString("photo"));
                map.put("TurnoId", TurnoId);

                HashMap<String, Object> maps = new HashMap<>();
                maps.put("inscriptos", eventinscriptos + 1);

                mfirestore.collection("Eventos").document(id).update(maps);

                mfirestore.collection("Turnos").document().set(map).addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Creado turno exitosamente", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e ->
                    Toast.makeText(getApplicationContext(), "Error al ingresar turno", Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show());

    }

    private void getEvent(String id){

        mfirestore.collection("Eventos").document(id).get().addOnSuccessListener(documentSnapshot -> {

            String photoPet = documentSnapshot.getString("photo");
            String eventTitle = documentSnapshot.getString("titulo");
            String eventCreator =  documentSnapshot.getString("creador");
            String eventDescription =  documentSnapshot.getString("descripcion");
            String eventFecha =  documentSnapshot.getString("fecha");
            String eventHora = documentSnapshot.getString("horario");

            String eventCost  = String.valueOf(documentSnapshot.getLong("costo"));
            String eventCapacity = String.valueOf(documentSnapshot.getLong("cupo"));
            eventinscriptos = Integer.parseInt(String.valueOf(documentSnapshot.getLong("inscriptos")));

            txtLatitud = documentSnapshot.getString("latitud");
            txtLongitud = documentSnapshot.getString("longitud");
            eventEstado = documentSnapshot.getString("estado");



            LatLng argentina = new LatLng(Double.parseDouble(txtLatitud), Double.parseDouble(txtLongitud) );
            mMap.addMarker(new MarkerOptions().position(argentina).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(argentina));

            //fecha = eventFecha;
            //horario =  eventHora;

            date.setText(eventFecha);
            time.setText(eventHora);
            title.setText(eventTitle);
            creator.setText(eventCreator);
            description.setText(eventDescription);
            cost.setText("Costo: " + eventCost);
            cup.setText("Cupo: " + eventCapacity);
            this.setTitle(eventTitle);

            try {
                if(!photoPet.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Cargando foto", Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.TOP,0,200);
                    toast.show();
                    Picasso.with(DetailsUserActivity.this)
                            .load(photoPet)
                            .resize(150, 150)
                            .into(photo_pet);
                }
            }catch (Exception e){
                Log.v("Error", "e: " + e);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show());
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainUserActivity.class);
        startActivity(intent);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra("id_Event");
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if(mapFragment != null){ mapFragment.getMapAsync(this);
        }
        else { Toast.makeText(this,"El map es null",Toast.LENGTH_LONG).show();
        }

        linearLayout_image_btn = findViewById(R.id.images_btn);
        title =  findViewById(R.id.textViewtitulo);
        creator =  findViewById(R.id.textViewCreador);
        description = findViewById(R.id.textViewDesc);
        cost = findViewById(R.id.textViewCosto);
        time = findViewById(R.id.textViewHora);
        date = findViewById(R.id.textViewFecha);
        cup = findViewById(R.id.textViewCupo);
        photo_pet = findViewById(R.id.pet_photoo);
        btn_turno = findViewById(R.id.btn_Turno);

        getEvent(id);

        btn_turno.setOnClickListener(view -> CreateTurn(id));

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMyLocationButtonClickListener(this);
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);
        enableMyLocation();
        LatLng argentina = new LatLng(-34.6016217,-58.5775943);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(argentina));
    }
    //Enables the My Location layer if the fine location permission has been granted.
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return;
        }
        // 2. Otherwise, request location permissions from the user.
        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud = String.valueOf(latLng.latitude);
        txtLongitud = String.valueOf(latLng.longitude);
        mMap.clear();
        LatLng argentina = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(argentina).title(""));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud = String.valueOf(latLng.latitude);
        txtLongitud = String.valueOf(latLng.longitude);
        mMap.clear();
        LatLng argentina = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(argentina).title(""));
    }
    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }
    //Displays a dialog with error message explaining that the location permission is missing.*/
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}


