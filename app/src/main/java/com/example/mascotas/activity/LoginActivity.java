package com.example.mascotas.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mascotas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {


    private EditText editTextPassword;
    private EditText editTextMail;
    private Button btn_ingresar;
    private Button btn_registrarse;
    private TextView tv_recuperar_pass;

    //variables para ingresar sesion
    private String password;
    private String mail;
    private FirebaseAuth auth;
    FirebaseFirestore db;
    DocumentReference userRef;

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
                    Intent adminIntent = new Intent(LoginActivity.this, Main_admin_Activity.class);
                    startActivity(adminIntent);


                } else {
                    // El usuario no tiene rol de administrador
                    // Abrir el activity principal (MasterActivity)
                    Intent mainIntent = new Intent(LoginActivity.this, MainUserActivity.class);
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

    private void loginUser() {
        auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, R.string.toast2, Toast.LENGTH_LONG).show();
                    //aca guardar en shared preference los datos si el checkbox esta chequeado

                    checkUserRole(auth.getCurrentUser().getEmail());

                    //abrir el activity con la lista de eventos (MainActivity)
                    //Intent intent = new Intent(LoginActivity.this, MasterActivity.class);
                    //startActivity(intent);
                    //finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword() {
        auth.setLanguageCode("Reestablecer pass");
        auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, R.string.toast16, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, R.string.toast17, Toast.LENGTH_SHORT).show();
                }
                //dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null){

            checkUserRole(auth.getCurrentUser().getEmail());

        }else {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color));
        btn_registrarse = (Button) findViewById(R.id.botonIrRegistro);
        btn_ingresar = (Button) findViewById(R.id.botonIngresar);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextMail = (EditText) findViewById(R.id.editTextEmail);
        tv_recuperar_pass = findViewById(R.id.textViewRecuperarContraseña);

        auth = FirebaseAuth.getInstance();


        btn_registrarse.setOnClickListener(v -> {
            //listener del boton registrarse
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        btn_ingresar.setOnClickListener(v -> {
            //listener del boton ingresar
            mail = editTextMail.getText().toString();
            password = editTextPassword.getText().toString();

            if (!mail.isEmpty() && !password.isEmpty()){
                loginUser();
            }else{
                Toast.makeText(LoginActivity.this, R.string.toast1, Toast.LENGTH_LONG).show();
            }
        });


        tv_recuperar_pass.setOnClickListener(v -> {
            //listener textview boton recuperar contaseña
            mail= editTextMail.getText().toString();
            if (!mail.isEmpty()){
                //dialog.setMessage(getString( R.string.espere_por_favor));
                //dialog.setCanceledOnTouchOutside(false);
                //dialog.show();
                resetPassword();
            }else {
                Toast.makeText(LoginActivity.this, R.string.toast15, Toast.LENGTH_SHORT).show();
                editTextMail.requestFocus();
            }
        });

    }

}
