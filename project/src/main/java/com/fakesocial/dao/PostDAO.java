package com.fakesocial.dao;

import com.fakesocial.model.Post;
import com.fakesocial.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    
    public int createPost(int userId, String content, boolean isAiGenerated) throws SQLException {
        String sql = "INSERT INTO posts (user_id, content, is_ai_generated) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, content);
            pstmt.setBoolean(3, isAiGenerated);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating post failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating post failed, no ID obtained.");
                }
            }
        }
    }
    
    public Post getPostById(int id) throws SQLException {
        String sql = "SELECT p.*, COUNT(l.id) as like_count " +
                     "FROM posts p " +
                     "LEFT JOIN likes l ON p.id = l.post_id " +
                     "WHERE p.id = ? " +
                     "GROUP BY p.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPost(rs);
            }
            return null;
        }
    }
    
    public List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, COUNT(l.id) as like_count " +
                     "FROM posts p " +
                     "LEFT JOIN likes l ON p.id = l.post_id " +
                     "GROUP BY p.id " +
                     "ORDER BY p.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        }
        return posts;
    }
    
    public List<Post> getPostsByUserId(int userId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, COUNT(l.id) as like_count " +
                     "FROM posts p " +
                     "LEFT JOIN likes l ON p.id = l.post_id " +
                     "WHERE p.user_id = ? " +
                     "GROUP BY p.id " +
                     "ORDER BY p.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        }
        return posts;
    }
    
    public boolean deletePost(int postId) throws SQLException {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        Timestamp timestamp = rs.getTimestamp("created_at");
        post.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
        post.setLikeCount(rs.getInt("like_count"));
        try {
            post.setAiGenerated(rs.getBoolean("is_ai_generated"));
        } catch (SQLException e) {
            // Column might not exist in old databases
            post.setAiGenerated(false);
        }
        return post;
    }
}
