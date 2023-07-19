package com.example.chatz;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class postadapter extends RecyclerView.Adapter<postadapter.MyviewHolder> {
    private Context context;
    private List<postmodel> postmodelList;

    public postadapter(Context context) {
        this.context = context;
        postmodelList=new ArrayList<>();

    }
    public void addPost(postmodel Postmodel){
        postmodelList.add(Postmodel);
        notifyDataSetChanged();
    }
    public void clearposts(){
        postmodelList.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        postmodel PostModel = postmodelList.get(position);
        if(PostModel.getPostImage()!=null){
            holder.postimage.setVisibility(View.VISIBLE);
            Glide.with(context).load(PostModel.getPostImage()).into(holder.postimage);
        }
        else{
            holder.postimage.setVisibility(View.GONE);
        }
        holder.postText.setText(PostModel.getPostText());
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, comments.class);
                intent.putExtra("id",PostModel.getPostId());
                context.startActivity(intent);
            }
        });



        FirebaseFirestore.getInstance().collection("Likes")
                        .document(PostModel.getPostId()+FirebaseAuth.getInstance().getUid())
                                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot!=null){
                            String data=documentSnapshot.getString("postId");
                            if(data!=null){
                         PostModel.setLiked(true);
                            holder.like.setImageResource(R.drawable.baseline_thumb_up_24_blue);
                        }else{
                            PostModel.setLiked(false);
                                holder.like.setImageResource(R.drawable.baseline_thumb_up_24);
                            }
                        }
                        else {
                            PostModel.setLiked(false);
                            holder.like.setImageResource(R.drawable.baseline_thumb_up_24);
                        }
                    }
                });

      //  PostModel.setLiked(false);
    holder.clickProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(context, ProfileActivity.class);
            intent.putExtra("id",PostModel.getUserId());
            context.startActivity(intent);
        }
    });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PostModel.isLiked()){
                    PostModel.setLiked(false);

                    holder.like.setImageResource(R.drawable.baseline_thumb_up_24);
                    FirebaseFirestore.getInstance()
                            .collection("Likes")
                            .document(PostModel.getPostId()+FirebaseAuth.getInstance().getUid())
                            .delete();
                }
                else {
                    PostModel.setLiked(true);
                    holder.like.setImageResource(R.drawable.baseline_thumb_up_24_blue);
                    FirebaseFirestore.getInstance()
                            .collection("Likes")
                            .document(PostModel.getPostId()+FirebaseAuth.getInstance().getUid())
                            .set(new postmodel("hi"));

                }
            }
        });




        String uid=PostModel.getUserId();
        FirebaseFirestore.getInstance()
                .collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                   UserModel   userModel=documentSnapshot.toObject(UserModel.class);
                   if(userModel.getUserProfile()!=null){
                       Glide.with(context).load(userModel.getUserProfile()).into(holder.userProfile);
                   }
                   holder.username.setText(userModel.getUserName());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return postmodelList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder{
        private TextView username ,postText;
        private ImageView userProfile,postimage,like,comment;
        private RelativeLayout clickProfile;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            userProfile=itemView.findViewById(R.id.userprofile);
            postText=itemView.findViewById(R.id.postText);
            postimage=itemView.findViewById(R.id.postImage);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            clickProfile=itemView.findViewById(R.id.clickProfile);


        }
    }
}
