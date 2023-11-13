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
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private Button button;
    private TextView verMail,verPass;
    private FirebaseAuth auth;

    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();

        user = auth.getCurrentUser();

        verMail = (TextView) findViewById(R.id.showEmail);
        verPass = (TextView) findViewById(R.id.showPass);
        button = (Button) findViewById(R.id.btnLogOut);
        verMail.setText(auth.getCurrentUser().getEmail().toString());
        verPass.setText(auth.getCurrentUser().getUid().toString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getEmail().toString();
        if ("pipivr72@gmail.com".equals(user)){
            startActivity(new Intent(MainActivity.this, Main_admin_Activity.class));
            finish();
        }
    }
}