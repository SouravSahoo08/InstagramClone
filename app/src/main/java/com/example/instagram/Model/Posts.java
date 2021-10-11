package com.example.instagram.Model;

public class Posts {
    private String Description;
    private String ImageUrl;
    private String PostId;
    private String UserId;

    public Posts() {
    }

    public Posts(String description, String ImageUrl, String Postid, String UserId) {
        this.Description = description;
        this.ImageUrl = ImageUrl;
        this.PostId = Postid;
        this.UserId = UserId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String PostId) {
        this.PostId = PostId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
}
