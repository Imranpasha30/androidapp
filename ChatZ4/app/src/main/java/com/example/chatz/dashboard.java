package com.example.chatz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatz.databinding.ActivityDashboardBinding;
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

public class dashboard extends AppCompatActivity {
    ActivityDashboardBinding binding;
    private postadapter Postadapter;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot userDocumentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        binding.imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(dashboard.this, ProfileActivity.class);
                intent.putExtra("id", firebaseAuth.getUid());
                startActivity(intent);
            }
        });

        Postadapter=new postadapter(this);
        binding.postRecyclerView.setAdapter(Postadapter);
        binding.postRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadProfilePicture();
        loadPosts();

        binding.goCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dashboard.this, createpost.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id==R.id.logout){
            firebaseAuth.signOut();
            startActivity(new Intent(dashboard.this, login.class));
            finish();
            return true;
        }
        return false;
    }

    private void loadProfilePicture() {
        db.collection("users").document(firebaseAuth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null && userModel.getUserProfile() != null) {
                                Glide.with(dashboard.this).load(userModel.getUserProfile())
                                        .into(binding.imageview1);
                            }
                        }
                    }
                });
    }

    private void loadPosts() {
        db.collection("posts")
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
