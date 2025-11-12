package com.fakesocial.model;

import java.time.LocalDateTime;

public class Post {
    private int id;
    private int userId;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
    private boolean isAiGenerated;

    public Post() {
    }

    public Post(int id, int userId, String content, LocalDateTime createdAt, int likeCount, boolean isAiGenerated) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.isAiGenerated = isAiGenerated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isAiGenerated() {
        return isAiGenerated;
    }

    public void setAiGenerated(boolean aiGenerated) {
        isAiGenerated = aiGenerated;
    }
}

