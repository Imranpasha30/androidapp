package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {
    private EditText usermail, password1;
    private Button signinbtn;
    private TextView regist;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usermail = findViewById(R.id.inusername);
        password1 = findViewById(R.id.inpassword);
        signinbtn = findViewById(R.id.lllogin);
        regist = findViewById(R.id.here);

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, registor2.class));
            }
        });

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usermail.getText().toString().trim();
                String password = password1.getText().toString().trim();
                progressDialog = new ProgressDialog(login.this);
                progressDialog.setTitle("Logging in");
                progressDialog.setMessage("In progress");
                progressDialog.show();

                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressDialog.cancel();

                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                                new MySharedPreferences(login.this).setMyData(userModel.getUserNumber());
                                                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();

                                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                                    startActivity(new Intent(login.this, dashboard.class));
                                                    finish();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
