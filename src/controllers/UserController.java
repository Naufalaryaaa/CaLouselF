package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Database;
import models.User;
import validators.UserValidator;

public class UserController {

    private Database db;

    public UserController() {
        db = Database.getInstance();
    }

    public String registerUser(User newUser) {
    	if (isUserExists(newUser.getUsername())) {
    		return "Username Already Exist";
    	}
    	
        try {
            String query = "INSERT INTO users (username, password, phone_number, address, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = db.prepareStatement(query);
            ps.setString(1, newUser.getUsername());
            ps.setString(2, newUser.getPassword());
            ps.setString(3, newUser.getPhoneNumber());
            ps.setString(4, newUser.getAddress());
            ps.setString(5, newUser.getRole());
            ps.executeUpdate();
            return "User successfully registered!";
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                return "Username already exists. Please choose another one.";
            }
            return "Error while saving user: " + e.getMessage();
        }
    }
    
    public String userLogin(User userData) {
    	if (userData.getUsername().equalsIgnoreCase("admin") && userData.getPassword().equalsIgnoreCase("admin")) {
    		return "Login Successful";
    	}
        String query = "SELECT password FROM users WHERE username = ?";
        
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setString(1, userData.getUsername());  
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password");
                    
                    if (userData.getPassword().equals(storedPasswordHash)) {
                        return "Login successful";
                    } else {
                        return "Invalid username or password";
                    }
                } else {
                    return "Invalid username or password";  
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
    }

    
    public boolean isUserExists(String username) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = db.prepareStatement(query);
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getRoleByUsername(String username) {
    	String query = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement ps = db.prepareStatement(query)) {
        	if (username.equalsIgnoreCase("admin")) {
        		return "admin";
        	}
            ps.setString(1, username);  
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                } else {
                    return null; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int getIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";

        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; 
        }
    }
}
