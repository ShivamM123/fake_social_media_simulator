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
    
    public LoginWindow() {
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Login - Fake Social Media");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Fake Social Media", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24));
        titleLabel.setForeground(new Color(59, 89, 152));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setBackground(new Color(59, 89, 152));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new LoginAction());
        
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 30));
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
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Allow Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);
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

