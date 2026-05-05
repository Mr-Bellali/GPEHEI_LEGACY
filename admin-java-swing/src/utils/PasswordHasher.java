package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hashedBytes) {
                String hexPart = Integer.toHexString(0xff & b);
                if (hexPart.length() == 1) hex.append('0');
                hex.append(hexPart);
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verify(String rawPassword, String hashedPassword) {
        String hashedInput = hash(rawPassword);
        return hashedInput.equals(hashedPassword);
    }
}

/*
When creating user
String hashed = PasswordHasher.hash(password);
When logging in (Service layer ONLY)
Admin admin = adminDAO.findByEmail(email);

if (admin != null && PasswordHasher.verify(password, admin.getPassword())) {
    // success
}
 */