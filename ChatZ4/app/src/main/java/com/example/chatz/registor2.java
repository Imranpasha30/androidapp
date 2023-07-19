package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class registor2 extends AppCompatActivity {
    private ProgressDialog progressDialog;

    private TextView textview;
    private EditText etUsername, etEmail, etPhone, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registor2);
        etUsername = findViewById(R.id.fullname);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.rRegister);
        textview = findViewById(R.id.Rlogin);

        etUsername.setBackgroundColor(Color.TRANSPARENT);

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registor2.this, login.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String number = etPhone.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                 progressDialog = new ProgressDialog(registor2.this);
                progressDialog.setTitle("Creating");
                progressDialog.setMessage("Account");
                progressDialog.show();

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(),password.trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(username).build();

                                FirebaseAuth.getInstance().getCurrentUser()
                                        .updateProfile(userProfileChangeRequest);

                                new MySharedPreferences(registor2.this).setMyData(number);
                                UserModel userModel=new UserModel();
                                userModel.setUserName(username);
                                userModel.setUserNumber(number);
                                userModel.setUserEmail(email);

                                FirebaseFirestore
                                        .getInstance()
                                        .collection("users")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .set(userModel);

                                reset();


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(registor2.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        });

            }
        });


    }
    private void reset(){
        progressDialog.cancel();
        Toast.makeText(this,"Account created login please",Toast.LENGTH_SHORT).show();
        //FirebaseAuth.getInstance().signOut();
    }
}