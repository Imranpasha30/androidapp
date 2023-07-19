package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatz.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    private postadapter Postadapter;
    private String userId;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot userDocumentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Postadapter = new postadapter(this);
        binding.postRecycler.setAdapter(Postadapter);
        binding.postRecycler.setLayoutManager(new LinearLayoutManager(this));

        userId = getIntent().getStringExtra("id");

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        loadUserData();
        loadPosts();

        binding.userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, profileupdate.class));
            }
        });
    }

    private void loadUserData() {
        db.collection("users").document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null) {
                                binding.username.setText(userModel.getUserName());
                                if (userModel.getUserBio() != null) {
                                    binding.userBio.setText(userModel.getUserBio());
                                } else {
                                    binding.userBio.setText(R.string.bio);
                                }
                                if (userModel.getUserProfile() != null) {
                                    Glide.with(ProfileActivity.this).load(userModel.getUserProfile())
                                            .into(binding.userprofile);
                                }
                                if (userModel.getUserCover() == null) {
                                    Glide.with(ProfileActivity.this).load(userModel.getUserProfile())
                                            .into(binding.coverphoto);
                                }else{
                                    Glide.with(ProfileActivity.this).load(userModel.getUserProfile())
                                            .into(binding.coverphoto);
                                }
                            }
                        }
                    }
                });
    }

    private void loadPosts() {
        db.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("postingTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            List<DocumentSnapshot> dList = queryDocumentSnapshots.getDocuments();
                            Postadapter.clearposts(); // Clear the existing posts
                            for (DocumentSnapshot ds : dList) {
                                postmodel Postmodel = ds.toObject(postmodel.class);
                                Postadapter.addPost(Postmodel);
                            }
                        }
                    }
                });
    }
}
