package com.fakesocial.dao;

import com.fakesocial.model.User;
import com.fakesocial.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public int createUser(String username, String email, String password, boolean isFake, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, is_fake, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setBoolean(4, isFake);
            pstmt.setBoolean(5, isAdmin);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }
    
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }
    
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }
    
    public User authenticateUser(String username, String password) throws SQLException {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    public List<User> getFakeUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_fake = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateUserAdminStatus(int userId, boolean isAdmin) throws SQLException {
        String sql = "UPDATE users SET is_admin = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isAdmin);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        try {
            user.setPassword(rs.getString("password"));
        } catch (SQLException e) {
            user.setPassword("");
        }
        Timestamp timestamp = rs.getTimestamp("created_at");
        user.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
        user.setFake(rs.getBoolean("is_fake"));
        try {
            user.setAdmin(rs.getBoolean("is_admin"));
        } catch (SQLException e) {
            user.setAdmin(false);
        }
        return user;
    }
}
