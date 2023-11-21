package com.example.mascotas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mascotas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edMail;
    private EditText edPass;
    private EditText edName;
    private EditText edSurname;
    private String mail;
    private String pass;
    private String name;
    private String surname;
    private FirebaseAuth auth;

    FirebaseFirestore db;


    protected void registerUser(){

        db = FirebaseFirestore.getInstance();

        auth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                Map<String,Object> data = new HashMap<>();
                data.put("nombre", name);
                data.put("apellido",surname);
                data.put("rol", "User");
                data.put("contraseña",pass);

                db.collection("usuario").document(mail)
                        .set(data)
                        .addOnSuccessListener(avoid->{
                            Toast.makeText(RegisterActivity.this, "Registro exitoso maestro",Toast.LENGTH_LONG).show();
                            finish();
                        })
                        .addOnFailureListener(avoid->
                        {
                            Toast.makeText(RegisterActivity.this,"Algo salio mal",Toast.LENGTH_LONG).show();

                        });
            }
            else{
                Toast.makeText(RegisterActivity.this, "Intentalo de nuevo capaz sale",Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color));

        Button btnRegistrarse = findViewById(R.id.botonRegistrarse);
        edMail = findViewById(R.id.editTextEmail);
        edPass = findViewById(R.id.editTextPassword);
        edName = findViewById(R.id.editTextNombre);
        edSurname = findViewById(R.id.editTextApellido);

        auth = FirebaseAuth.getInstance();

        btnRegistrarse.setOnClickListener(view -> {
            mail = edMail.getText().toString();
            pass = edPass.getText().toString();
            name = edName.getText().toString();
            surname = edSurname.getText().toString();

            if(!mail.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !surname.isEmpty())
            {
               registerUser();
            }else {
                Toast.makeText(RegisterActivity.this, "Completá los campos master",Toast.LENGTH_LONG).show();
            }
        });


    }
}
