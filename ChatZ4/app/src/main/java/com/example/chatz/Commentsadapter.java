package com.example.chatz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class Commentsadapter extends RecyclerView.Adapter<Commentsadapter.MyviewHolder> {
    private Context context;
    private List<CommentModel> postmodelList;

    public Commentsadapter(Context context) {
        this.context = context;
        postmodelList=new ArrayList<>();

    }
    public void addPost(CommentModel Postmodel){
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.commentview,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        CommentModel CommentModel = postmodelList.get(position);
        holder.comment.setText(CommentModel.getComment());
        String uid=CommentModel.getUserId();
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
        private TextView username ,comment;
        private ImageView userProfile;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            userProfile=itemView.findViewById(R.id.userprofile);
            comment=itemView.findViewById(R.id.comment);

        }
    }
}
