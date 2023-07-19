package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatz.databinding.ActivityCreatepostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.UUID;

import javax.annotation.Nullable;

public class createpost extends AppCompatActivity {
    private Uri pickedImageUri;
    private Button postbtn;
    ActivityCreatepostBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatepostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        postbtn = findViewById(R.id.post);

        binding.pickphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opengallery();
            }
        });

        // Load user profile picture and username
        loadUserData();
    }

    private void loadUserData() {
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null) {
                                // Load and display the user's profile picture
                                if (userModel.getUserProfile() != null) {
                                    Glide.with(createpost.this).load(userModel.getUserProfile())
                                            .into(binding.userprofile);
                                }

                                // Set the username
                                binding.username.setText(userModel.getUserName());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(createpost.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void opengallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.post) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Posting");
            progressDialog.show();

            String id = UUID.randomUUID().toString();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Posts/" + id + "image.png");
            if (pickedImageUri != null) {
                storageReference.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.cancel();
                                finish();
                                Toast.makeText(createpost.this, "Posted", Toast.LENGTH_SHORT).show();
                                postmodel Postmodel = new postmodel(id, firebaseAuth.getUid(),
                                        binding.postText.getText().toString(), uri.toString(), "0", "0",
                                        Calendar.getInstance().getTimeInMillis());
                                FirebaseFirestore.getInstance().collection("posts")
                                        .document(id)
                                        .set(Postmodel);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Toast.makeText(createpost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                postmodel Postmodel = new postmodel(id, firebaseAuth.getUid(),
                        binding.postText.getText().toString(), null, "0", "0",
                        Calendar.getInstance().getTimeInMillis());
                FirebaseFirestore.getInstance().collection("posts")
                        .document(id)
                        .set(Postmodel);
                progressDialog.cancel();
                finish();
                Toast.makeText(createpost.this, "Posted", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (data != null) {
                pickedImageUri = data.getData();
                binding.pickedImage.setImageURI(pickedImageUri);
                Glide.with(createpost.this).load(pickedImageUri).into(binding.pickedImage);
            } else {
                Toast.makeText(this, "Image not picked", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
