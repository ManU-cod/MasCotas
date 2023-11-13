package com.example.mascotas.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mascotas.R;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private Button botoncerrarSesion;
    private TextView verMail;
    private TextView verPass;

    private FirebaseAuth auth;

    protected void signOut(){
        auth.signOut();
    }

    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();

        verMail = (TextView) findViewById(R.id.showEmail);
        verPass = (TextView) findViewById(R.id.showPass);
        botoncerrarSesion = (Button) findViewById(R.id.btnLogOut);

        verMail.setText(auth.getCurrentUser().getEmail().toString());
        verPass.setText(auth.getCurrentUser().getPhoneNumber());

        botoncerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        verMail = (TextView) findViewById(R.id.showEmail);
        verPass = (TextView) findViewById(R.id.showPass);
        botoncerrarSesion = (Button) findViewById(R.id.btnLogOut);

        auth = FirebaseAuth.getInstance();

    }
}