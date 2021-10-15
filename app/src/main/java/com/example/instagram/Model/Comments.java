package com.example.instagram.Model;

public class Comments {

    private String comment;
    private String userName;
    private String commnetId;

    public Comments() {
    }

    public Comments(String comment, String userName, String commnetId) {
        this.comment = comment;
        this.userName = userName;
        this.commnetId = commnetId;
    }

    public String getCommnetId() {
        return commnetId;
    }

    public void setCommnetId(String commnetId) {
        this.commnetId = commnetId;
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
