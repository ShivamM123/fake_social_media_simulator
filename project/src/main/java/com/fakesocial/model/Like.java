package com.fakesocial.model;

public class Like {
    private int id;
    private int postId;
    private int userId;

    public Like() {
    }

    public Like(int id, int postId, int userId) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

