package com.fakesocial.dao;

import com.fakesocial.model.Comment;
import com.fakesocial.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    
    public int createComment(int postId, int userId, String content, boolean isAiGenerated) throws SQLException {
        String sql = "INSERT INTO comments (post_id, user_id, content, is_ai_generated) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, content);
            pstmt.setBoolean(4, isAiGenerated);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
        }
    }
    
    public List<Comment> getCommentsByPostId(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        return comments;
    }
    
    public boolean deleteComment(int commentId) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, commentId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setContent(rs.getString("content"));
        Timestamp timestamp = rs.getTimestamp("created_at");
        comment.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
        comment.setAiGenerated(rs.getBoolean("is_ai_generated"));
        return comment;
    }
}

