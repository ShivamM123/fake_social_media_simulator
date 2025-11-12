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

    // UI Styling
    private final Color primaryColor = new Color(59, 89, 152);
    private final Color bgColor = new Color(240, 242, 245);
    private final Color panelColor = Color.WHITE;
    private final Color borderColor = new Color(200, 200, 200);
    private final Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private final Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    
    public RegisterWindow(JFrame parent) {
        this.parentFrame = parent;
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Register - Fake Social Media");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());
        
        // Main panel with padding
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
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
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
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailField = new JTextField(15);
        emailField.setBorder(BorderFactory.createLineBorder(borderColor));
        mainPanel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        passwordField.setBorder(BorderFactory.createLineBorder(borderColor));
        mainPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Confirm:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(borderColor));
        mainPanel.add(confirmPasswordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        registerButton = new JButton("Register");
        styleButton(registerButton, true); // Primary
        registerButton.addActionListener(new RegisterAction());
        
        backButton = new JButton("Back");
        styleButton(backButton, false); // Secondary
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
        
        add(paddingPanel, BorderLayout.CENTER);
        
        getRootPane().setDefaultButton(registerButton);
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