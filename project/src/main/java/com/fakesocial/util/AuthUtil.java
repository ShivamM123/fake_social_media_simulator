package com.fakesocial.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthUtil {
    
    // Simple password hashing (MD5 - not secure but simple for a uni project)
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback to plain text if hashing fails
        }
    }
    
    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}

