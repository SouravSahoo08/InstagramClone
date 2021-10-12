package com.example.instagram.Model;

public class Comments {

    private String comment;
    private String userName;

    public Comments() {
    }

    public Comments(String comment, String userName) {
        this.comment = comment;
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
