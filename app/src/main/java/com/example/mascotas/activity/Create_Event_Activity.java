package com.example.mascotas.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
        int eventinscriptos = 0;
        String txtLatitud,txtLongitud,fecha, horario, eventEstado;
        private DatePickerDialog datePickerDialog;
        GoogleMap mMap;

        private FirebaseFirestore mfirestore;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private boolean permissionDenied = false;
        private FirebaseAuth mAuth;


        ImageView photo_pet;
        Button btn_cu_photo, btn_r_photo,btn_turno;
        LinearLayout linearLayout_image_btn;
        StorageReference storageReference;
        String storage_path = "Eventos/*";

        private static final int COD_SEL_STORAGE = 200;
        private static final int COD_SEL_IMAGE = 300;

        private Uri image_url;
        String photo = "photo";
        String idd;

        String download_uri1;
        ProgressDialog progressDialog;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_create_event);
                this.setTitle("Evento");
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                initDatePicker();

                progressDialog = new ProgressDialog(this);
                String id = getIntent().getStringExtra("id_Event");
                mfirestore = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                storageReference = FirebaseStorage.getInstance().getReference();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                if(mapFragment != null){
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(this,"El map es null",Toast.LENGTH_LONG).show();
                }

                linearLayout_image_btn = findViewById(R.id.images_btn);
                dateButton = findViewById(R.id.datePickerButton);
                dateButton.setText(getTodaysDate());
                timeButton = findViewById(R.id.timeButton);
                titulo =  findViewById(R.id.titulo);
                creador =  findViewById(R.id.creador);
                descripcion = findViewById(R.id.descripcion);
                costo = findViewById(R.id.costo);
                cupo = findViewById(R.id.cupo);
                btn_send = findViewById(R.id.button_send);
                photo_pet = findViewById(R.id.pet_photo);
                btn_cu_photo = findViewById(R.id.btn_photo);
                btn_r_photo = findViewById(R.id.btn_remove_photo);

                btn_turno = findViewById(R.id.btn_Turno);
                btn_turno.setOnClickListener(view -> CrearTurno(id));

                btn_cu_photo.setOnClickListener(view -> uploadPhoto());

                btn_r_photo.setOnClickListener(view -> {
                  if (id == null || id == ""){
                    photo_pet.setImageResource(R.drawable.ic_image);
                    if(!TextUtils.isEmpty(download_uri1)){
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(download_uri1);
                        storageRef.delete().addOnSuccessListener(aVoid ->
                                Toast.makeText(getApplicationContext(), "exitoso", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "fallido", Toast.LENGTH_SHORT).show();
                        });
                    }
                  }else {
                    mfirestore.collection("Eventos").document(id).get().addOnSuccessListener(documentSnapshot -> {
                        String photoPet = documentSnapshot.getString("photo");
                        photo_pet.setImageResource(R.drawable.ic_image);
                        if(!TextUtils.isEmpty(photoPet)){
                           StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoPet);
                           storageRef.delete().addOnSuccessListener(aVoid -> {
                               HashMap<String, Object> map = new HashMap<>();
                               map.put("photo", "");
                               mfirestore.collection("Eventos").document(id).update(map);
                               Toast.makeText(Create_Event_Activity.this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                           }).addOnFailureListener(e -> {
                               Toast.makeText(getApplicationContext(), "fallido", Toast.LENGTH_SHORT).show();
                           });
                        }

                    }).addOnFailureListener(e ->
                       Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show());
                  }
                });

                if (id == null || id == ""){
                       //linearLayout_image_btn.setVisibility(View.GONE);
                }else{
                        idd = id;
                        btn_send.setText("Resubir");
                        getEvent(id);
                }
                btn_send.setOnClickListener(view -> {
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
                                if (id == null || id == "") {
                                        // Publicar nuevo evento
                                        postEvent(eventTitle, eventCreator, eventDescription);
                                } else {
                                        // Actualizar evento existente
                                        updatePet(eventTitle, eventCreator, eventDescription, id);
                                }
                        }
                });

        }



        private void CrearTurno(String id){
            mfirestore.collection("Eventos").document(id).get().addOnSuccessListener(documentSnapshot -> {
                int eventCost  = Math.toIntExact(documentSnapshot.getLong("costo"));
                int eventinscriptos = Integer.parseInt(String.valueOf(documentSnapshot.getLong("inscriptos")));
                String idUser = mAuth.getCurrentUser().getUid();

                Map<String, Object> map = new HashMap<>();
                map.put("userId", idUser);
                map.put("id", documentSnapshot.getString("id"));
                map.put("titulo", documentSnapshot.getString("titulo"));
                //map.put("creador", documentSnapshot.getString("creador"));
                //map.put("fecha", documentSnapshot.getString("fecha"));
                //map.put("horario", documentSnapshot.getString("horario"));
                //map.put("latitud",  documentSnapshot.getString("latitud"));
                //map.put("longitud",  documentSnapshot.getString("longitud"));
                //map.put("descripcion", documentSnapshot.getString("descripcion"));
                map.put("estado", documentSnapshot.getString("estado"));
                map.put("photo",  documentSnapshot.getString("photo"));
                //map.put("costo", eventCost);

                HashMap<String, Object> maps = new HashMap<>();
                maps.put("inscriptos", eventinscriptos + 1);
                mfirestore.collection("Eventos").document(id).update(maps);

                mfirestore.collection("Turnos").document(idUser).set(map).addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Creado turno exitosamente", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Error al ingresar turno", Toast.LENGTH_SHORT).show());

            }).addOnFailureListener(e ->
                    Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show());
        }

        private void uploadPhoto() {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, COD_SEL_IMAGE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if(resultCode == RESULT_OK){
                        if (requestCode == COD_SEL_IMAGE){
                                image_url = data.getData();
                                subirPhoto(image_url);
                        }
                }

        }

        private void subirPhoto(Uri image_url) {

                if (idd == null || idd == ""){
                        progressDialog.setMessage("Actualizando foto");
                        progressDialog.show();
                        String rute_storage_photo = storage_path + "" + photo + "" + mAuth.getUid() +""+ idd;
                        StorageReference reference = storageReference.child(rute_storage_photo);
                        reference.putFile(image_url).addOnSuccessListener(taskSnapshot -> {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());
                                if (uriTask.isSuccessful()){
                                        uriTask.addOnSuccessListener(uri -> {
                                                download_uri1 = uri.toString();
                                                Picasso.with(getApplicationContext())
                                                        .load(download_uri1)
                                                        .resize(150, 150)
                                                        .into(photo_pet);
                                                Toast.makeText(Create_Event_Activity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                        });
                                }
                        }).addOnFailureListener(e -> Toast.makeText(Create_Event_Activity.this,
                                "Error al cargar foto", Toast.LENGTH_SHORT).show());
                }else{
                        progressDialog.setMessage("Actualizando foto");
                        progressDialog.show();
                        String rute_storage_photo = storage_path + "" + photo + "" + mAuth.getUid() +""+ idd;
                        StorageReference reference = storageReference.child(rute_storage_photo);
                        reference.putFile(image_url).addOnSuccessListener(taskSnapshot -> {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());
                                if (uriTask.isSuccessful()){
                                        uriTask.addOnSuccessListener(uri -> {
                                                String download_uri = uri.toString();
                                                Picasso.with(getApplicationContext())
                                                        .load(download_uri)
                                                        .resize(150, 150)
                                                        .into(photo_pet);
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("photo", download_uri);
                                                mfirestore.collection("Eventos").document(idd).update(map);
                                                Toast.makeText(Create_Event_Activity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                        });
                                }
                        }).addOnFailureListener(e -> Toast.makeText(Create_Event_Activity.this,
                                "Error al cargar foto", Toast.LENGTH_SHORT).show());
                }

        }

        private void  updatePet(String eventTitle,String eventCreator,String eventDescription, String id) {
                int eventCost = Integer.parseInt(costo.getText().toString());
                int eventCupo = Integer.parseInt(cupo.getText().toString());

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

                mfirestore.collection("Eventos").document(id).update(map).addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Create_Event_Activity.this, Main_admin_Activity.class);
                        startActivity(intent);
                }).addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Error al actualizar", Toast.LENGTH_SHORT).show());
        }

        private void  postEvent(String eventTitle,String eventCreator,String eventDescription) {
                //String idUser = mAuth.getCurrentUser().getUid();
                DocumentReference id = mfirestore.collection("Eventos").document();
                int eventCost = Integer.parseInt(costo.getText().toString());
                int eventCupo = Integer.parseInt(cupo.getText().toString());

                Map<String, Object> map = new HashMap<>();
                map.put("id", id.getId());
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
                map.put("photo", download_uri1);

                mfirestore.collection("Eventos").document(id.getId()).set(map).addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Create_Event_Activity.this, Main_admin_Activity.class);
                        startActivity(intent);
                }).addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show());
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
              fecha = eventFecha;
              horario =  eventHora;


              LatLng argentina = new LatLng(Double.parseDouble(txtLatitud), Double.parseDouble(txtLongitud) );
              mMap.addMarker(new MarkerOptions().position(argentina).title(""));
              mMap.moveCamera(CameraUpdateFactory.newLatLng(argentina));

              dateButton.setText(eventFecha);
              timeButton.setText(eventHora);
              titulo.setText(eventTitle);
              creador.setText(eventCreator);
              descripcion.setText(eventDescription);
              costo.setText(eventCost);
              cupo.setText(eventCapacity);
                   try {
                           if(!photoPet.equals("")){
                                   Toast toast = Toast.makeText(getApplicationContext(), "Cargando foto", Toast.LENGTH_SHORT);
                                   //toast.setGravity(Gravity.TOP,0,200);
                                   toast.show();
                                   Picasso.with(Create_Event_Activity.this)
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

        @Override
        public boolean onSupportNavigateUp() {
                onBackPressed();
                Intent intent = new Intent(getApplicationContext(), Main_admin_Activity.class);
                startActivity(intent);
                return true;
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