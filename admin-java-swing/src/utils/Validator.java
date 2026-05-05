package utils;

import java.util.regex.Pattern;

public class Validator {

    private Validator() {}

    // Email pattern (simple but effective for university systems)
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Password rule: min 6 chars, at least 1 letter and 1 number
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$");

    // ---------- BASIC VALIDATIONS ----------

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidName(String name) {
        return isNotEmpty(name) && name.trim().length() >= 2;
    }

    // ---------- NUMBER VALIDATIONS ----------

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    // ---------- TEXT RULES ----------

    public static boolean isValidLength(String value, int min, int max) {
        if (!isNotEmpty(value)) return false;
        int len = value.trim().length();
        return len >= min && len <= max;
    }
}

/*if (!Validator.isValidEmail(email)) {
    throw new IllegalArgumentException("Invalid email format");
}

if (!Validator.isNotEmpty(password)) {
    throw new IllegalArgumentException("Password required");
}
if (!Validator.isValidName(firstName)) {
    throw new IllegalArgumentException("Invalid first name");
}

if (!Validator.isValidPassword(password)) {
    throw new IllegalArgumentException("Weak password");
}


*/