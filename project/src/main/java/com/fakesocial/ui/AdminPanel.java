package com.fakesocial.ui;

import com.fakesocial.dao.PostDAO;
import com.fakesocial.dao.UserDAO;
import com.fakesocial.model.Post;
import com.fakesocial.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminPanel extends JFrame {
    private User currentUser;
    private JFrame parentFrame;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private JTable userTable;
    private JTable postTable;
    private DefaultTableModel userTableModel;
    private DefaultTableModel postTableModel;

    // UI Styling
    private final Color primaryColor = new Color(59, 89, 152);
    private final Color bgColor = new Color(240, 242, 245);
    private final Color panelColor = Color.WHITE;
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color borderColor = new Color(220, 220, 220);
    private final Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    
    public AdminPanel(User user, JFrame parent) {
        this.currentUser = user;
        this.parentFrame = parent;
        userDAO = new UserDAO();
        postDAO = new PostDAO();
        
        setTitle("Admin Panel - " + user.getUsername());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(bgColor);
        
        createUI();
        loadData();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        
        // Title bar
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(primaryColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JButton closeButton = new JButton("Close");
        styleButton(closeButton, false, false); // Secondary (white)
        closeButton.addActionListener(e -> dispose());
        titlePanel.add(closeButton, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        tabbedPane.setBackground(panelColor);
        
        // Users tab
        JPanel usersPanel = createUsersPanel();
        tabbedPane.addTab("Users", usersPanel);
        
        // Posts tab
        JPanel postsPanel = createPostsPanel();
        tabbedPane.addTab("Posts", postsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columnNames = {"ID", "Username", "Email", "Admin", "Fake"};
        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(userTableModel);
        styleTable(userTable);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, true, false); // Primary (blue)
        refreshButton.addActionListener(e -> loadUsers());
        
        JButton deleteButton = new JButton("Delete Selected User");
        styleButton(deleteButton, false, true); // Danger (red)
        deleteButton.addActionListener(e -> deleteSelectedUser());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPostsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columnNames = {"ID", "User ID", "Content", "AI Generated"};
        postTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        postTable = new JTable(postTableModel);
        styleTable(postTable);
        JScrollPane scrollPane = new JScrollPane(postTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, true, false); // Primary (blue)
        refreshButton.addActionListener(e -> loadPosts());
        
        JButton deleteButton = new JButton("Delete Selected Post");
        styleButton(deleteButton, false, true); // Danger (red)
        deleteButton.addActionListener(e -> deleteSelectedPost());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void styleTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(primaryColor);
        header.setBorder(BorderFactory.createLineBorder(borderColor));
    }

    private void styleButton(JButton button, boolean isPrimary, boolean isDanger) {
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 30));
        button.setFont(buttonFont);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isDanger) {
            button.setBackground(dangerColor);
            button.setForeground(Color.WHITE); // White text on red
            button.setBorder(BorderFactory.createLineBorder(dangerColor, 1));
        } else if (isPrimary) {
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE); // White text on blue
            button.setBorder(BorderFactory.createLineBorder(primaryColor, 1));
        } else {
            button.setBackground(panelColor);
            button.setForeground(primaryColor); // Blue text on white
            button.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        }
    }
    
    private void loadData() {
        loadUsers();
        loadPosts();
    }
    
    private void loadUsers() {
        userTableModel.setRowCount(0);
        try {
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                Object[] row = {
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.isAdmin() ? "Yes" : "No",
                    user.isFake() ? "Yes" : "No"
                };
                userTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPosts() {
        postTableModel.setRowCount(0);
        try {
            List<Post> posts = postDAO.getAllPosts();
            for (Post post : posts) {
                String content = post.getContent().length() > 50 ? 
                    post.getContent().substring(0, 50) + "..." : post.getContent();
                Object[] row = {
                    post.getId(),
                    post.getUserId(),
                    content,
                    post.isAiGenerated() ? "Yes" : "No"
                };
                postTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading posts: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int userId = (Integer) userTableModel.getValueAt(selectedRow, 0);
            if (userId == currentUser.getId()) {
                JOptionPane.showMessageDialog(this, "You cannot delete your own account!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this user?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                userDAO.deleteUser(userId);
                loadUsers();
                JOptionPane.showMessageDialog(this, "User deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedPost() {
        int selectedRow = postTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a post to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int postId = (Integer) postTableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this post?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                postDAO.deletePost(postId);
                loadPosts();
                JOptionPane.showMessageDialog(this, "Post deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                // Also refresh the main window if it's open
                if (parentFrame instanceof MainWindow) {
                    ((MainWindow) parentFrame).loadPosts();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting post: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}