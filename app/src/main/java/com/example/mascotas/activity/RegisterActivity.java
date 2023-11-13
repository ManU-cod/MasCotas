package com.example.mascotas.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mascotas.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegistrarse;
    private EditText edMail;
    private EditText edPass;

    private String mail;

    private String pass;

    private FirebaseAuth auth;


    protected void registerUser(){
        auth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(RegisterActivity.this, "Registro exitoso maestro",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(RegisterActivity.this, "Intentalo de nuevo capaz sale",Toast.LENGTH_LONG).show();

            }
        });
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegistrarse = (Button) findViewById(R.id.botonRegistrarse);
        edMail = (EditText) findViewById(R.id.editTextEmail);
        edPass = (EditText) findViewById(R.id.editTextPassword);




        auth = FirebaseAuth.getInstance();

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = edMail.getText().toString();
                pass = edPass.getText().toString();
                if(!mail.isEmpty() && !pass.isEmpty())
                {
                   registerUser();
                }else {
                    Toast.makeText(RegisterActivity.this, "Completá los campos master",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
