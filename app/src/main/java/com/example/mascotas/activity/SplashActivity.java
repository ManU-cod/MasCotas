package com.example.mascotas.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.mascotas.R;


public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000; // Duración en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Usar un Handler para retrasar la transición a la actividad principal
        new Handler().postDelayed(() -> {
            
            // Este método se ejecutará después del tiempo de espera definido
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Cerrar la actividad actual
            finish();
        }, SPLASH_TIME_OUT);
    }
}
