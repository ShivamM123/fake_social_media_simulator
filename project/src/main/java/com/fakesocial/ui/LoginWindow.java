package com.fakesocial.ui;

import com.fakesocial.dao.UserDAO;
import com.fakesocial.model.User;
import com.fakesocial.util.AuthUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserDAO userDAO;

    // UI Styling
    private final Color primaryColor = new Color(59, 89, 152);
    private final Color bgColor = new Color(240, 242, 245);
    private final Color panelColor = Color.WHITE;
    private final Color borderColor = new Color(200, 200, 200);
    private final Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private final Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);

    public LoginWindow() {
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Login - Fake Social Media");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        // Use a main panel with a border for a "card" effect
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(panelColor);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Add padding around the main panel
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setBackground(bgColor);
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddingPanel.add(mainPanel, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Fake Social Media", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(15);
        usernameField.setBorder(BorderFactory.createLineBorder(borderColor));
        mainPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        passwordField.setBorder(BorderFactory.createLineBorder(borderColor));
        mainPanel.add(passwordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        loginButton = new JButton("Login");
        styleButton(loginButton, true); // Primary
        loginButton.addActionListener(new LoginAction());
        
        registerButton = new JButton("Register");
        styleButton(registerButton, false); // Secondary
        registerButton.addActionListener(e -> {
            new RegisterWindow(this).setVisible(true);
            setVisible(false);
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        add(paddingPanel, BorderLayout.CENTER);
        
        // Allow Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);
    }

    /**
     * A helper method to style buttons consistently.
     */
    private void styleButton(JButton button, boolean isPrimary) {
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 30));
        button.setFont(buttonFont);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isPrimary) {
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE); // White text on blue
            button.setBorder(BorderFactory.createLineBorder(primaryColor, 1));
        } else {
            button.setBackground(panelColor);
            button.setForeground(primaryColor); // Blue text on white
            button.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        }
    }
    
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginWindow.this, 
                    "Please enter both username and password.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Use hashed password for authentication
                User user = userDAO.authenticateUser(username, AuthUtil.hashPassword(password));
                if (user != null) {
                    // Login successful
                    new MainWindow(user).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginWindow.this, 
                        "Invalid username or password.", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginWindow.this, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}