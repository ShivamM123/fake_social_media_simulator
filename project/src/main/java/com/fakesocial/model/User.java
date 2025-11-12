package com.fakesocial.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private boolean isFake;
    private boolean isAdmin;

    public User() {
    }

    public User(int id, String username, String email, String password, LocalDateTime createdAt, boolean isFake, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.isFake = isFake;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFake() {
        return isFake;
    }

    public void setFake(boolean fake) {
        isFake = fake;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return username;
    }
}

