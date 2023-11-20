package com.example.mascotas.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.mascotas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    DocumentReference userRef;

    private FirebaseFirestore db;

    private void checkUserRole(String userId) {

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("usuario").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {
                // El documento del usuario existe
                String userRole = documentSnapshot.getString("rol");

                if ("Admin".equals(userRole)) {

                    // El usuario tiene rol de administrador
                    // Abrir el activity de administrador (AdminActivity, por ejemplo)
                    Intent adminIntent = new Intent(SplashActivity.this, Main_admin_Activity.class);
                    startActivity(adminIntent);


                } else {
                    // El usuario no tiene rol de administrador
                    // Abrir el activity principal (MasterActivity)
                    Intent mainIntent = new Intent(SplashActivity.this, MainUserActivity.class);
                    startActivity(mainIntent);

                }
                finish();
            } else {
                // El documento del usuario no existe
                // Manejar el caso según sea necesario
            }
        }).addOnFailureListener(e -> {
            // Manejar errores al obtener datos de Firestore
        });
    }

    private static final int SPLASH_TIME_OUT = 2000; // Duración en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Usar un Handler para retrasar la transición a la actividad principal
        new Handler().postDelayed(() -> {

            if (auth.getCurrentUser() != null){

                checkUserRole(auth.getCurrentUser().getEmail());

            }else {
                // Este método se ejecutará después del tiempo de espera definido
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);

                // Cerrar la actividad actual
                finish();
            }

        }, SPLASH_TIME_OUT);
    }

}
