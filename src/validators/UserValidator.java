package validators;

public class UserValidator {
    public static String validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "Username cannot be empty.";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters long.";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            return "Password must include at least one special character (!, @, #, $, %, ^, &, *).";
        }
        return null; 
    }

    public static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "Phone number cannot be empty.";
        }
        if (!phoneNumber.matches("\\+62\\d{9,}")) {
            return "Phone number must start with +62 and be at least 10 digits long.";
        }
        return null;
    }

    public static String validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            return "Address cannot be empty.";
        }
        return null; 
    }

    public static String validateRole(String role) {
        if (role == null || role.isEmpty()) {
            return "Role must be selected.";
        }
        if (!role.equals("seller") && !role.equals("buyer")) {
            return "Role must be either 'seller' or 'buyer'.";
        }
        return null;
    }

}
