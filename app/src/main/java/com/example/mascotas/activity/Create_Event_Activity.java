package com.example.mascotas.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mascotas.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Create_Event_Activity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,
        GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

        EditText titulo,creador,descripcion,costo,cupo;
        Button timeButton,dateButton,btn_send;
        int hour, minute;
        String txtLatitud,txtLongitud,fecha, horario;
        private DatePickerDialog datePickerDialog;
        GoogleMap mMap;

        private FirebaseFirestore mfirestore;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private boolean permissionDenied = false;
        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        this.setTitle("Nuevo Evento");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initDatePicker();

        mfirestore = FirebaseFirestore.getInstance();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        timeButton = findViewById(R.id.timeButton);
        titulo =  findViewById(R.id.titulo);
        creador =  findViewById(R.id.creador);
        descripcion = findViewById(R.id.descripcion);
        costo = findViewById(R.id.costo);
        cupo = findViewById(R.id.cupo);
        btn_send = findViewById(R.id.button_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                String eventTitle = titulo.getText().toString().trim();
                String eventCreator = creador.getText().toString().trim();
                String eventDescription = descripcion.getText().toString().trim();
                String eventCost = costo.getText().toString().trim();
                String eventCapacity = cupo.getText().toString().trim();

                        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventCreator) ||
                                TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventCapacity)
                                || TextUtils.isEmpty(eventCost) || TextUtils.isEmpty(txtLatitud) || TextUtils.isEmpty(fecha) ) {
                                // If any field is empty, show a Toast message
                                Toast.makeText(Create_Event_Activity.this,
                                "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
                        } else {
                                postEvent(eventTitle, eventCreator, eventDescription);
                        }
                }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        }

        private void  postEvent(String eventTitle,String eventCreator,String eventDescription) {
                int eventCost = Integer.parseInt(costo.getText().toString());
                int eventCupo = Integer.parseInt(cupo.getText().toString());
                int eventinscriptos = 0;

                Map<String, Object> map = new HashMap<>();
                map.put("titulo", eventTitle);
                map.put("creador", eventCreator);
                map.put("fecha", fecha);
                map.put("horario", horario);
                map.put("latitud", txtLatitud);
                map.put("longitud", txtLongitud);
                map.put("descripcion", eventDescription);
                map.put("cupo",  eventCupo);
                map.put("costo", eventCost);
                map.put("inscriptos", eventinscriptos );
                map.put("estado", "activo");

                mfirestore.collection("Eventos").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Creado exitosamente",
                        Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Create_Event_Activity.this,
                        Main_admin_Activity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                        "Error al ingresar", Toast.LENGTH_SHORT).show();
                        }
                });
        }

        @Override
        public boolean onSupportNavigateUp() {
                onBackPressed();
                return false;
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
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
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

        private String getTodaysDate() {
                Calendar cal = Calendar.getInstance();
                return makeDateString(cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH)+ 1, cal.get(Calendar.YEAR));
        }
        private void initDatePicker() {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = makeDateString(day, month, year);
                        fecha =  makeDateString(day, month, year);
                        dateButton.setText(date);
                        }
                };
                Calendar cal = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }

        public void popTimePicker(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour = selectedHour;
                        minute = selectedMinute;
                        horario = String.format(Locale.getDefault(), "%02d:%02d",hour, minute);
                        timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
                        }
                };

                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                onTimeSetListener, hour, minute, true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
        }

        private String makeDateString(int day, int month, int year) {
                return getMonthFormat(month) + " " + day + " " + year;
        }

        private String getMonthFormat(int month) {
                if(month == 1)
                return "JAN";
                if(month == 2)
                return "FEB";
                if(month == 3)
                return "MAR";
                if(month == 4)
                return "APR";
                if(month == 5)
                return "MAY";
                if(month == 6)
                return "JUN";
                if(month == 7)
                return "JUL";
                if(month == 8)
                return "AUG";
                if(month == 9)
                return "SEP";
                if(month == 10)
                return "OCT";
                if(month == 11)
                return "NOV";
                if(month == 12)
                return "DEC";

                //default should never happen
                return "JAN";
        }

        public void openDatePicker(View view) {
                datePickerDialog.show();
        }
}