package com.fakesocial.dao;

import com.fakesocial.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LikeDAO {
    
    public boolean addLike(int postId, int userId) throws SQLException {
        // Check if like already exists
        if (hasUserLikedPost(postId, userId)) {
            return false;
        }
        
        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    public boolean removeLike(int postId, int userId) throws SQLException {
        String sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    public boolean hasUserLikedPost(int postId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}

