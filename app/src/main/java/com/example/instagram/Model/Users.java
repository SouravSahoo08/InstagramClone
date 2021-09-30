package com.example.instagram.Model;

public class Users {
    private String Name;
    private String Email;
    private String Username;
    private String Userid;
    private String Bio;
    private String Imageurl;

    public Users() {
    }

    public Users(String name, String email, String username, String userid, String bio, String imageurl) {
        Name = name;
        Email = email;
        Username = username;
        Userid = userid;
        Bio = bio;
        Imageurl = imageurl;
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

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }
}
