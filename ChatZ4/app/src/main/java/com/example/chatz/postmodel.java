package com.example.chatz;

public class postmodel {
    private String postId,userId,postText,postImage,postLikes,postcomments;
    private long postingTime;

    private boolean isLiked;

    public postmodel(String postId, String userId,String postText, String postImage, String postLikes, String postcomments, long postingTime) {
        this.postId = postId;
        this.userId = userId;
        this.postText= postText;
        this.postImage = postImage;
        this.postLikes = postLikes;
        this.postcomments = postcomments;
        this.postingTime = postingTime;
    }

    public postmodel(String postId) {
        this.postId = postId;
    }

    public postmodel() {
    }

    public String getPostText() {
        return postText;
    }
public  boolean isLiked(){
        return isLiked;
}
public void setLiked(boolean liked){
        isLiked=liked;
}

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(String postLikes) {
        this.postLikes = postLikes;
    }

    public String getPostcomments() {
        return postcomments;
    }

    public void setPostcomments(String postcomments) {
        this.postcomments = postcomments;
    }

    public long getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(long postingTime) {
        this.postingTime = postingTime;
    }
}
