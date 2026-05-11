package utils;

public class SessionManager {
    private static String jwtToken;

    public static void setToken(String token) {
        jwtToken = token;
    }

    public static String getToken() {
        return jwtToken;
    }

    public static void clearSession() {
        jwtToken = null;
    }

    public static boolean isAuthenticated() {
        if (jwtToken == null) {
            return false;
        }

        try {
            JwtUtil.isValid(jwtToken);
            return true;
        } catch (Exception e) {
            clearSession();
            return false;
        }
    }
}