package com.fakesocial.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthUtil {
    
    // --- UPDATED: Hashing algorithm changed from MD5 to SHA-256 ---
    public static String hashPassword(String password) {
        try {
            // Changed "MD5" to "SHA-256" for much stronger hashing
            MessageDigest md = MessageDigest.getInstance("SHA-256");
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
        // This method still works, as it will hash the provided password
        // using SHA-256 and compare it to the stored SHA-256 hash.
        return hashPassword(password).equals(hashedPassword);
    }
}