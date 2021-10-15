package com.example.instagram.Model;

public class Saved {

    private String PostId;
    private String ImageUrl;

    public Saved() {
    }

    public Saved(String postId, String imageUrl) {
        PostId = postId;
        ImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }
}
