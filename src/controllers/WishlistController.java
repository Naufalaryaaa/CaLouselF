package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import models.Item;
import models.WishlistItem;

public class WishlistController {

	private Database db;

    public WishlistController() {
        db = Database.getInstance();
    }
    
    public boolean isItemInWishlist(int itemId, int userId) {
        try {
            String query = "SELECT COUNT(*) FROM wishlists WHERE item_id = ? AND user_id = ?";
            PreparedStatement ps = db.prepareStatement(query);
            ps.setInt(1, itemId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking wishlist: " + e.getMessage());
        }
        return false;
    }

    public String addToWishlist(int itemId, int userId) {
        if (isItemInWishlist(itemId, userId)) {
            return "Item already in wishlist!";
        }
        try {
            String query = "INSERT INTO wishlists (item_id, user_id) VALUES (?, ?)";
            PreparedStatement ps = db.prepareStatement(query);
            ps.setInt(1, itemId);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return "Item successfully added to wishlist!";
        } catch (SQLException e) {
            return "Error while saving Item: " + e.getMessage();
        }
    }
    
    public List<WishlistItem> getWishlistsByUserId(int userId) {
        String query = "SELECT w.wishlist_id, i.item_name, i.item_size, i.item_price, i.item_category " +
                       "FROM wishlists w " +
                       "JOIN items i ON w.item_id = i.item_id " +
                       "WHERE w.user_id = ?";
        List<WishlistItem> wishlistItems = new ArrayList<>();

        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem(
                        rs.getInt("wishlist_id"),
                        rs.getString("item_name"),
                        rs.getString("item_size"),
                        rs.getDouble("item_price"),
                        rs.getString("item_category")
                    );
                    wishlistItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishlistItems;
    }


    
    public String removeWishlist(int wishlistId) {
        String query = "DELETE FROM wishlists WHERE wishlist_id = ?";
        
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, wishlistId);
            int rowsDeleted = ps.executeUpdate();
            
            if (rowsDeleted > 0) {
                return "successfully removed from wishlist!";
            } else {
                return "Error: Wishlist item not found.";
            }
        } catch (SQLException e) {
            return "Error while removing item: " + e.getMessage();
        }
    }
    
    public void removeItemFromWishlist(int itemId) {
        String query = "DELETE FROM wishlists WHERE item_id = ?";
        
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, itemId);
            
            int rowsDeleted = ps.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Item successfully removed from wishlist!");
            } else {
                System.out.println("Warning: Wishlist item not found for this user.");
            }
        } catch (SQLException e) {
            System.out.println("Error while removing item: " + e.getMessage());
        }
    }


}
