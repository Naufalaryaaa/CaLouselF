package models;

public class User {
    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private String role;

    public User(String username, String password, String phoneNumber, String address, String role) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }
    
    public User(String username, String password) {
    	this.username = username;
    	this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
}
