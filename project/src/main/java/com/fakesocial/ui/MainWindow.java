package com.fakesocial.ui;

import com.fakesocial.dao.*;
import com.fakesocial.model.*;
import com.fakesocial.util.AIGenerator;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindow extends JFrame {
    private JTextArea postTextArea;
    private JPanel postsPanel;
    private PostDAO postDAO;
    private UserDAO userDAO;
    private LikeDAO likeDAO;
    private CommentDAO commentDAO;
    private User currentUser;
    private JButton refreshButton;
    private JButton adminButton;
    private JButton logoutButton;
    private Timer refreshTimer;
    private Color primaryColor = new Color(59, 89, 152);
    private Color bgColor = new Color(240, 242, 245);
    
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
        startAutoRefresh();
    }
    
    private void startAutoRefresh() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> loadPosts());
            }
        }, 5000, 5000);
    }
    
    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        super.dispose();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        
        // Top bar with user info and buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font(userLabel.getFont().getName(), Font.BOLD, 16));
        topBar.add(userLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        
        if (currentUser.isAdmin()) {
            adminButton = new JButton("Admin Panel");
            adminButton.setBackground(Color.WHITE);
            adminButton.setFocusPainted(false);
            adminButton.addActionListener(e -> {
                new AdminPanel(currentUser, this).setVisible(true);
            });
            buttonPanel.add(adminButton);
        }
        
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });
        buttonPanel.add(logoutButton);
        
        topBar.add(buttonPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
        
        // Post creation panel
        JPanel createPanel = createPostCreationPanel();
        add(createPanel, BorderLayout.NORTH);
        
        // Posts display
        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsPanel.setBackground(bgColor);
        JScrollPane scrollPane = new JScrollPane(postsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(bgColor);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(bgColor);
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPosts());
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPostCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Create New Post"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 140));
        
        postTextArea = new JTextArea(3, 30);
        postTextArea.setLineWrap(true);
        postTextArea.setWrapStyleWord(true);
        postTextArea.setFont(new Font(postTextArea.getFont().getName(), Font.PLAIN, 14));
        JScrollPane textScrollPane = new JScrollPane(postTextArea);
        
        JButton postButton = new JButton("Post");
        postButton.setBackground(primaryColor);
        postButton.setForeground(Color.BLACK);
        postButton.setFocusPainted(false);
        postButton.setPreferredSize(new Dimension(80, 30));
        postButton.addActionListener(e -> createPost());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(postButton);
        
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
            loadPosts();
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
                noPostsLabel.setFont(new Font(noPostsLabel.getFont().getName(), Font.ITALIC, 14));
                postsPanel.add(noPostsLabel);
            } else {
                for (Post post : posts) {
                    JPanel postPanel = createPostPanel(post);
                    postsPanel.add(postPanel);
                    postsPanel.add(Box.createVerticalStrut(15));
                }
            }
            
            postsPanel.revalidate();
            postsPanel.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading posts: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createPostPanel(Post post) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        try {
            User user = userDAO.getUserById(post.getUserId());
            String username = user != null ? user.getUsername() : "Unknown User";
            
            // Header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            JLabel userLabel = new JLabel(username);
            userLabel.setFont(new Font(userLabel.getFont().getName(), Font.BOLD, 16));
            userLabel.setForeground(primaryColor);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
            String timeString = post.getCreatedAt() != null ? 
                    post.getCreatedAt().format(formatter) : "Unknown";
            JLabel timeLabel = new JLabel(timeString);
            timeLabel.setFont(new Font(timeLabel.getFont().getName(), Font.PLAIN, 11));
            timeLabel.setForeground(Color.GRAY);
            
            if (post.isAiGenerated()) {
                JLabel aiLabel = new JLabel("🤖 AI");
                aiLabel.setFont(new Font(aiLabel.getFont().getName(), Font.ITALIC, 10));
                aiLabel.setForeground(new Color(100, 100, 100));
                headerPanel.add(aiLabel, BorderLayout.CENTER);
            }
            
            headerPanel.add(userLabel, BorderLayout.WEST);
            headerPanel.add(timeLabel, BorderLayout.EAST);
            mainPanel.add(headerPanel);
            mainPanel.add(Box.createVerticalStrut(10));
            
            // Content
            JTextArea contentArea = new JTextArea(post.getContent());
            contentArea.setEditable(false);
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setBackground(Color.WHITE);
            contentArea.setFont(new Font(contentArea.getFont().getName(), Font.PLAIN, 14));
            contentArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            mainPanel.add(contentArea);
            mainPanel.add(Box.createVerticalStrut(10));
            
            // Footer - Like and Comment buttons
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            footerPanel.setOpaque(false);
            
            JButton likeButton = new JButton("❤ Like");
            likeButton.setFocusPainted(false);
            likeButton.setBackground(Color.WHITE);
            
            boolean hasLiked = likeDAO.hasUserLikedPost(post.getId(), currentUser.getId());
            if (hasLiked) {
                likeButton.setText("❤ Liked");
                likeButton.setForeground(Color.RED);
            }
            
            likeButton.addActionListener(e -> toggleLike(post));
            
            JLabel likeCountLabel = new JLabel("(" + post.getLikeCount() + ")");
            likeCountLabel.setForeground(Color.GRAY);
            
            JButton commentButton = new JButton("💬 Comment");
            commentButton.setFocusPainted(false);
            commentButton.setBackground(Color.WHITE);
            commentButton.addActionListener(e -> showCommentDialog(post));
            
            footerPanel.add(likeButton);
            footerPanel.add(likeCountLabel);
            footerPanel.add(commentButton);
            mainPanel.add(footerPanel);
            
            // Comments section
            List<Comment> comments = commentDAO.getCommentsByPostId(post.getId());
            if (!comments.isEmpty()) {
                mainPanel.add(Box.createVerticalStrut(10));
                JPanel commentsPanel = new JPanel();
                commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
                commentsPanel.setOpaque(false);
                commentsPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(10, 10, 0, 10)
                ));
                
                for (Comment comment : comments) {
                    JPanel commentPanel = createCommentPanel(comment);
                    commentsPanel.add(commentPanel);
                    commentsPanel.add(Box.createVerticalStrut(5));
                }
                
                mainPanel.add(commentsPanel);
            }
            
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading post: " + e.getMessage());
            mainPanel.add(errorLabel);
        }
        
        return mainPanel;
    }
    
    private JPanel createCommentPanel(Comment comment) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        try {
            User user = userDAO.getUserById(comment.getUserId());
            String username = user != null ? user.getUsername() : "Unknown";
            
            JLabel userLabel = new JLabel(username + ":");
            userLabel.setFont(new Font(userLabel.getFont().getName(), Font.BOLD, 12));
            userLabel.setForeground(primaryColor);
            
            JLabel contentLabel = new JLabel(comment.getContent());
            contentLabel.setFont(new Font(contentLabel.getFont().getName(), Font.PLAIN, 12));
            
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);
            contentPanel.add(userLabel, BorderLayout.WEST);
            contentPanel.add(contentLabel, BorderLayout.CENTER);
            
            if (comment.isAiGenerated()) {
                JLabel aiLabel = new JLabel("🤖");
                aiLabel.setFont(new Font(aiLabel.getFont().getName(), Font.PLAIN, 10));
                contentPanel.add(aiLabel, BorderLayout.EAST);
            }
            
            panel.add(contentPanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            // Error loading comment
        }
        
        return panel;
    }
    
    private void showCommentDialog(Post post) {
        JDialog dialog = new JDialog(this, "Add Comment", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextArea commentArea = new JTextArea(5, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        
        JButton submitButton = new JButton("Comment");
        submitButton.setBackground(primaryColor);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> {
            String content = commentArea.getText().trim();
            if (!content.isEmpty()) {
                try {
                    commentDAO.createComment(post.getId(), currentUser.getId(), content, false);
                    dialog.dispose();
                    loadPosts();
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
        buttonPanel.add(submitButton);
        
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
            
            loadPosts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error toggling like: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
