package com.example.chatz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;

import com.example.chatz.databinding.ActivityCommentsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.UUID;

public class comments extends AppCompatActivity {
ActivityCommentsBinding binding;
private String postId;

private Commentsadapter commentsadapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postId=getIntent().getStringExtra("id");
        commentsadapter = new Commentsadapter(this);
        binding.recycler.setAdapter(commentsadapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        loadcomments();

        binding.sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=binding.commentED.getText().toString();
                if(comment.trim().length()>0){
                    comment(comment);
                }
            }


        });

    }
    private void loadcomments(){
      FirebaseFirestore.getInstance().collection("comment")
              .whereEqualTo("postId",postId)
              .get()
              .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                  @Override
                  public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                      commentsadapter.clearposts();
                      List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments();
                      for(DocumentSnapshot ds:dsList){
                          CommentModel commentModel=ds.toObject(CommentModel.class);
                          commentsadapter.addPost(commentModel);
                      }
                  }
              });
    }
    private void comment(String comment) {
        String id= UUID.randomUUID().toString();
        CommentModel commentModel=new CommentModel(id,postId, FirebaseAuth.getInstance().getUid(),comment );
        FirebaseFirestore.getInstance().collection("comment").document(id)
                .set(commentModel);
        commentsadapter.addPost(commentModel);
    }
}