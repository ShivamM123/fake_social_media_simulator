package com.fakesocial.ui;

import com.fakesocial.dao.*;
import com.fakesocial.model.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
// Removed Timer and TimerTask imports

public class MainWindow extends JFrame {
    // UI Components
    private JTextArea postTextArea;
    private JPanel postsPanel;
    private JScrollPane scrollPane; // Class member
    // private Timer refreshTimer; // REMOVED

    // DAO and User
    private final PostDAO postDAO;
    private final UserDAO userDAO;
    private final LikeDAO likeDAO;
    private final CommentDAO commentDAO;
    private final User currentUser;

    // UI Styling
    private final Color primaryColor = new Color(59, 89, 152);
    private final Color bgColor = new Color(240, 242, 245);
    private final Color panelColor = Color.WHITE;
    private final Color textColor = new Color(30, 30, 30);
    private final Color borderColor = new Color(220, 220, 220);
    private final Font primaryFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private final Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);

    public MainWindow(User user) {
        this.currentUser = user;
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        likeDAO = new LikeDAO();
        commentDAO = new CommentDAO();
        
        setTitle("Fake Social Media - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        
        createUI();
        loadPosts();
        // startAutoRefresh(); // REMOVED
    }
    
    // REMOVED startAutoRefresh()
    // REMOVED dispose() override

    private void createUI() {
        setLayout(new BorderLayout());
        
        // --- Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(boldFont);
        topBar.add(userLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        if (currentUser.isAdmin()) {
            JButton adminButton = new JButton("Admin Panel");
            styleButton(adminButton, false); // Secondary button (white)
            adminButton.addActionListener(e -> {
                new AdminPanel(currentUser, this).setVisible(true);
            });
            buttonPanel.add(adminButton);
        }
        
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, false); // Secondary button (white)
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });
        buttonPanel.add(logoutButton);
        
        topBar.add(buttonPanel, BorderLayout.EAST);
        
        // --- Post Creation Panel ---
        JPanel createPanel = createPostCreationPanel();
        
        // --- Header Panel (Fixes layout bug) ---
        // This panel holds both the topBar and the createPanel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS)); // Stack them vertically
        headerPanel.add(topBar);
        headerPanel.add(createPanel);
        add(headerPanel, BorderLayout.NORTH);
        
        // --- Posts Display Panel ---
        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsPanel.setBackground(bgColor);
        postsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        scrollPane = new JScrollPane(postsPanel); // Use class member
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border
        
        add(scrollPane, BorderLayout.CENTER);
        
        // --- Bottom Refresh Button ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(bgColor);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JButton refreshButton = new JButton("Refresh Feed");
        styleButton(refreshButton, true); // Primary button (blue)
        refreshButton.setPreferredSize(new Dimension(150, 30));
        refreshButton.addActionListener(e -> loadPosts());
        
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPostCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(panelColor);
        
        postTextArea = new JTextArea(3, 30);
        postTextArea.setLineWrap(true);
        postTextArea.setWrapStyleWord(true);
        postTextArea.setFont(primaryFont);
        postTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane textScrollPane = new JScrollPane(postTextArea);
        textScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JButton postButton = new JButton("Post");
        styleButton(postButton, true); // Primary button (blue)
        postButton.addActionListener(e -> createPost());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(postButton);
        
        panel.add(new JLabel("Create New Post"), BorderLayout.NORTH);
        panel.add(textScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void createPost() {
        String content = postTextArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some content for your post.", 
                    "Empty Post", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            postDAO.createPost(currentUser.getId(), content, false);
            postTextArea.setText("");
            loadPosts(); // Refresh immediately after posting
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating post: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadPosts() {
        postsPanel.removeAll();
        
        try {
            List<Post> posts = postDAO.getAllPosts();
            
            if (posts.isEmpty()) {
                JLabel noPostsLabel = new JLabel("No posts yet. Be the first to post!");
                noPostsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noPostsLabel.setFont(new Font(primaryFont.getName(), Font.ITALIC, 14));
                postsPanel.add(noPostsLabel);
            } else {
                for (Post post : posts) {
                    JPanel postPanel = createPostPanel(post);
                    postsPanel.add(postPanel);
                    postsPanel.add(Box.createVerticalStrut(15)); // Space between posts
                }
            }
        } catch (SQLException e) {
            postsPanel.add(new JLabel("Error loading posts: " + e.getMessage()));
        }
        
        // Redraw the panel
        postsPanel.revalidate();
        postsPanel.repaint();
        
        // Scroll to top after manual refresh
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });
    }
    
    private JPanel createPostPanel(Post post) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Use BorderLayout
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        mainPanel.setBackground(panelColor);
        // REMOVED setMaximumSize to prevent layout conflicts
        
        try {
            User user = userDAO.getUserById(post.getUserId());
            String username = user != null ? user.getUsername() : "Unknown User";
            
            // --- Header ---
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            
            JLabel userLabel = new JLabel(username);
            userLabel.setFont(boldFont);
            userLabel.setForeground(textColor);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
            String timeString = post.getCreatedAt() != null ? 
                    post.getCreatedAt().format(formatter) : "Unknown";
            JLabel timeLabel = new JLabel(timeString);
            timeLabel.setFont(primaryFont.deriveFont(11f));
            timeLabel.setForeground(Color.GRAY);
            
            JPanel userTimePanel = new JPanel(new BorderLayout());
            userTimePanel.setOpaque(false);
            userTimePanel.add(userLabel, BorderLayout.CENTER);
            userTimePanel.add(timeLabel, BorderLayout.SOUTH);
            
            headerPanel.add(userTimePanel, BorderLayout.WEST);
            
            if (post.isAiGenerated()) {
                JLabel aiLabel = new JLabel("🤖 AI Generated");
                aiLabel.setFont(new Font(primaryFont.getName(), Font.ITALIC, 10));
                aiLabel.setForeground(new Color(100, 100, 100));
                headerPanel.add(aiLabel, BorderLayout.EAST);
            }
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            
            // --- Content (FIX for garbled text) ---
            // Use a JLabel with HTML for safe text wrapping
            String htmlContent = "<html><p style='width: 500px;'>" + 
                                post.getContent().replace("\n", "<br>") + 
                                "</p></html>";
            JLabel contentLabel = new JLabel(htmlContent);
            contentLabel.setFont(primaryFont.deriveFont(15f));
            contentLabel.setForeground(textColor);
            contentLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            mainPanel.add(contentLabel, BorderLayout.CENTER);

            // --- Footer Panel ---
            JPanel footerContainer = new JPanel(); // New container
            footerContainer.setLayout(new BoxLayout(footerContainer, BoxLayout.Y_AXIS));
            footerContainer.setOpaque(false);

            // --- Footer (Like/Comment buttons) ---
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
            footerPanel.setOpaque(false);
            footerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left
            
            JButton likeButton = new JButton("❤ Like");
            JButton commentButton = new JButton("💬 Comment");
            
            boolean hasLiked = likeDAO.hasUserLikedPost(post.getId(), currentUser.getId());
            
            // Style the 'like' button based on state
            if (hasLiked) {
                likeButton.setText("❤ Liked");
                styleButton(likeButton, true); // "Liked" is a primary action (blue)
            } else {
                styleButton(likeButton, false); // "Like" is a secondary action (white)
            }
            
            styleButton(commentButton, false); // "Comment" is always secondary (white)
            
            likeButton.addActionListener(e -> toggleLike(post));
            commentButton.addActionListener(e -> showCommentDialog(post));
            
            JLabel likeCountLabel = new JLabel(post.getLikeCount() + " likes");
            likeCountLabel.setForeground(Color.GRAY);
            likeCountLabel.setFont(primaryFont.deriveFont(12f));
            likeCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
            
            footerPanel.add(likeButton);
            footerPanel.add(likeCountLabel);
            footerPanel.add(commentButton);
            footerContainer.add(footerPanel); // Add footer to container
            
            // --- Comments Section ---
            List<Comment> comments = commentDAO.getCommentsByPostId(post.getId());
            if (!comments.isEmpty()) {
                footerContainer.add(Box.createVerticalStrut(15));
                JPanel commentsPanel = new JPanel();
                commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
                commentsPanel.setOpaque(false);
                commentsPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
                    BorderFactory.createEmptyBorder(10, 0, 0, 0)
                ));
                commentsPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left
                
                for (Comment comment : comments) {
                    JPanel commentPanel = createCommentPanel(comment);
                    commentsPanel.add(commentPanel);
                    commentsPanel.add(Box.createVerticalStrut(5));
                }
                
                footerContainer.add(commentsPanel); // Add comments to container
            }

            mainPanel.add(footerContainer, BorderLayout.SOUTH); // Add container to bottom
            
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading post: " + e.getMessage());
            mainPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        return mainPanel;
    }
    
    private JPanel createCommentPanel(Comment comment) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left
        
        try {
            User user = userDAO.getUserById(comment.getUserId());
            String username = user != null ? user.getUsername() : "Unknown";
            
            JLabel userLabel = new JLabel(username);
            userLabel.setFont(boldFont.deriveFont(12f));
            userLabel.setForeground(primaryColor);
            
            String aiTag = comment.isAiGenerated() ? " 🤖" : "";
            // Use JLabel with HTML for comment wrapping too
            String htmlComment = "<html><p style='width: 450px;'>" + 
                                 comment.getContent().replace("\n", "<br>") + aiTag + 
                                 "</p></html>";
            JLabel contentLabel = new JLabel(htmlComment);
            contentLabel.setFont(primaryFont.deriveFont(12f));
            contentLabel.setForeground(textColor);
            
            panel.add(userLabel);
            panel.add(contentLabel);
            
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading comment..."));
        }
        
        return panel;
    }
    
    private void showCommentDialog(Post post) {
        JDialog dialog = new JDialog(this, "Add Comment", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(panelColor);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextArea commentArea = new JTextArea(5, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setFont(primaryFont);
        commentArea.setBorder(BorderFactory.createLineBorder(borderColor));
        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JButton submitButton = new JButton("Post Comment");
        styleButton(submitButton, true); // Primary button (blue)
        submitButton.addActionListener(e -> {
            String content = commentArea.getText().trim();
            if (!content.isEmpty()) {
                try {
                    commentDAO.createComment(post.getId(), currentUser.getId(), content, false);
                    dialog.dispose();
                    loadPosts(); // Refresh to show new comment
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error adding comment: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please enter a comment.",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(submitButton);
        
        panel.add(new JLabel("Replying to post " + post.getId()), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void toggleLike(Post post) {
        try {
            boolean hasLiked = likeDAO.hasUserLikedPost(post.getId(), currentUser.getId());
            
            if (hasLiked) {
                likeDAO.removeLike(post.getId(), currentUser.getId());
            } else {
                likeDAO.addLike(post.getId(), currentUser.getId());
            }
            
            loadPosts(); // Refresh to show new like count and button state
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error toggling like: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * A helper method to style buttons consistently.
     * @param button The button to style.
     * @param isPrimary If true, style as a primary action (blue bg, white text).
     * If false, style as a secondary action (white bg, blue text).
     */
    private void styleButton(JButton button, boolean isPrimary) {
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isPrimary ? primaryColor : borderColor, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFont(primaryFont.deriveFont(Font.BOLD, 12f));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isPrimary) {
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(panelColor);
            button.setForeground(primaryColor);
        }
    }
}