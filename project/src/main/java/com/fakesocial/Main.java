package com.fakesocial;

import com.fakesocial.dao.UserDAO;
import com.fakesocial.simulator.FakeUserSimulator;
import com.fakesocial.ui.LoginWindow;
import com.fakesocial.util.AuthUtil;
import com.fakesocial.util.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static FakeUserSimulator simulator;
    
    public static void main(String[] args) {
        // Test database connection
        try {
            System.out.println("Testing database connection...");
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful!");
                conn.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                    "Cannot connect to database. Please ensure MySQL is running and the database is set up.\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Please run the schema.sql script to create the database and tables.",
                    "Database Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Create default admin user if it doesn't exist
        createDefaultAdmin();
        
        // Start AI post and comment simulator
        simulator = new FakeUserSimulator();
        simulator.start();
        
        // Create and show the login window
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new LoginWindow().setVisible(true);
            
            // Add shutdown hook to stop simulator
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (simulator != null) {
                    simulator.stop();
                }
                DatabaseConnection.closeConnection();
            }));
        });
    }
    
    private static void createDefaultAdmin() {
        try {
            UserDAO userDAO = new UserDAO();
            if (userDAO.getUserByUsername("admin") == null) {
                String hashedPassword = AuthUtil.hashPassword("admin123");
                userDAO.createUser("admin", "admin@fakesocial.com", hashedPassword, false, true);
                System.out.println("Default admin user created: username=admin, password=admin123");
            }
        } catch (SQLException e) {
            System.err.println("Error creating default admin: " + e.getMessage());
        }
    }
}
