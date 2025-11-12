package com.fakesocial.ui;

import com.fakesocial.dao.UserDAO;
import com.fakesocial.util.AuthUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterWindow extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private UserDAO userDAO;
    private JFrame parentFrame;
    
    public RegisterWindow(JFrame parent) {
        this.parentFrame = parent;
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Register - Fake Social Media");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
        titleLabel.setForeground(new Color(59, 89, 152));
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
        mainPanel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailField = new JTextField(15);
        mainPanel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Confirm:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        confirmPasswordField = new JPasswordField(15);
        mainPanel.add(confirmPasswordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setBackground(new Color(59, 89, 152));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(new RegisterAction());
        
        backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 30));
        backButton.addActionListener(e -> {
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        getRootPane().setDefaultButton(registerButton);
    }
    
    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(RegisterWindow.this, 
                    "Please fill in all fields.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(RegisterWindow.this, 
                    "Passwords do not match.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(RegisterWindow.this, 
                    "Password must be at least 4 characters long.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Check if username already exists
                if (userDAO.getUserByUsername(username) != null) {
                    JOptionPane.showMessageDialog(RegisterWindow.this, 
                        "Username already exists.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create user
                String hashedPassword = AuthUtil.hashPassword(password);
                userDAO.createUser(username, email, hashedPassword, false, false);
                
                JOptionPane.showMessageDialog(RegisterWindow.this, 
                    "Registration successful! Please login.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RegisterWindow.this, 
                    "Registration failed: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

