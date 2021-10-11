package com.example.instagram.Model;

public class Users {
    private String Name;
    private String Email;
    private String Username;
    private String UserId;
    private String Bio;
    private String ImageUrl;

    public Users() {
    }

    public Users(String name, String email, String username, String userid, String bio, String imageUrl) {
        Name = name;
        Email = email;
        Username = username;
        UserId = userid;
        Bio = bio;
        ImageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userid) {
        UserId = userid;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

}
