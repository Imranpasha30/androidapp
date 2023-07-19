package com.example.chatz;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatz.databinding.ActivityProfileupdateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class profileupdate extends AppCompatActivity {

    private ActivityProfileupdateBinding binding;
    private Uri pickedprofile;
    private String currentUserId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileupdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        // Load user data and profile picture
        loadUserData();

        binding.pickphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        binding.post1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void loadUserData() {
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null && userModel.getUserProfile() != null) {
                                // Load and display the user's profile picture
                                Glide.with(profileupdate.this)
                                        .load(userModel.getUserProfile())
                                        .into(binding.pickedprofileImage);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profileupdate.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    private void updateProfile() {
        if (pickedprofile != null) {
            // Show a progress dialog to indicate the image upload process
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Generate a unique filename for the uploaded image
            String fileName = UUID.randomUUID().toString() + ".jpg";

            // Get a reference to the Firebase Storage location where the image will be uploaded
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + fileName);

            // Upload the image to Firebase Storage
            storageRef.putFile(pickedprofile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully, get the download URL
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Create a map to hold the field you want to update
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("userProfile", uri.toString());

                                    // Update the field in Firestore
                                    db.collection("users").document(currentUserId)
                                            .update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Update successful
                                                    progressDialog.dismiss();
                                                    Toast.makeText(profileupdate.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Update failed
                                                    progressDialog.dismiss();
                                                    Toast.makeText(profileupdate.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Image upload failed
                            progressDialog.dismiss();
                            Toast.makeText(profileupdate.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(profileupdate.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            pickedprofile = data.getData();
            Glide.with(profileupdate.this).load(pickedprofile).into(binding.pickedprofileImage);
        } else {
            Toast.makeText(this, "Image not picked", Toast.LENGTH_SHORT).show();
        }
    }
}
