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

import com.example.mascotas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


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

    private ProgressDialog dialog;


    private void loginUser() {
        auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, R.string.toast2, Toast.LENGTH_LONG).show();
                    //aca guardar en shared preference los datos si el checkbox esta chequeado

                    //abrir el activity con la lista de eventos (MainActivity)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
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
                dialog.dismiss();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_registrarse = (Button) findViewById(R.id.botonIrRegistro);
        btn_ingresar = (Button) findViewById(R.id.botonIngresar);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextMail = (EditText) findViewById(R.id.editTextEmail);

        tv_recuperar_pass = findViewById(R.id.textViewRecuperarContraseña);

        dialog = new ProgressDialog(this);

        //Icono en el action bar
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.logo_icono_foreground);
        auth = FirebaseAuth.getInstance();

        //listener del boton registrarse
        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //listener del boton ingresar
        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail= editTextMail.getText().toString();
                password= editTextPassword.getText().toString();

                if (!mail.isEmpty() && !password.isEmpty()){
                    loginUser();
                }else{
                    Toast.makeText(LoginActivity.this, R.string.toast1, Toast.LENGTH_LONG).show();
                }
            }

        });

        //listener textview boton recuperar contaseña
        tv_recuperar_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail= editTextMail.getText().toString();
                if (!mail.isEmpty()){
                    dialog.setMessage(getString( R.string.espere_por_favor));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    resetPassword();
                }else {
                    Toast.makeText(LoginActivity.this, R.string.toast15, Toast.LENGTH_SHORT).show();
                    editTextMail.requestFocus();
                }
            }
        });

    }

}
