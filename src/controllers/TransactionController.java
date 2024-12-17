package controllers;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import models.Item;
import models.ItemQueue;
import models.Offer;
import models.Transaction;
import models.TransactionHistory;

public class TransactionController {
	private Database db;

    public TransactionController() {
        db = Database.getInstance();
    }
    
    public String recordTransactions(Transaction newTransaction) {
        String query = "INSERT INTO transactions (user_id, seller_id, item_id, item_name, item_size, item_price, item_category, total_paid) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, newTransaction.getUserId());
            ps.setInt(2, newTransaction.getSellerId());
            ps.setInt(3, newTransaction.getItemId());
            ps.setString(4, newTransaction.getItemName());      
            ps.setString(5, newTransaction.getItemSize());      
            ps.setBigDecimal(6, newTransaction.getItemPrice()); 
            ps.setString(7, newTransaction.getItemCategory());
            ps.setBigDecimal(8, newTransaction.getTotalPaid()); 
            
            ps.executeUpdate();
            return "Transaction recorded successfully!";
        } catch (SQLException e) {
            return "Error while recording transaction: " + e.getMessage();
        }
    }

    public BigDecimal getHighestOffer(int userId, int itemId) {
        String query = "SELECT MAX(offer_price) FROM offers WHERE user_id = ? AND item_id = ?";
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, userId); 
            ps.setInt(2, itemId); 
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1); 
            }
        } catch (SQLException e) {
            System.out.println("Error fetching highest offer: " + e.getMessage());
        }
        return BigDecimal.ZERO; 
    }

    public String makeOffer(Offer newOffer) {
        String query = "INSERT INTO offers (user_id, seller_id, item_id, offer_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, newOffer.getUserId());
            ps.setInt(2, newOffer.getSellerId());
            ps.setInt(3, newOffer.getItemId());
            ps.setBigDecimal(4, newOffer.getOfferPrice());
            ps.executeUpdate();
            return "Offer successfully recorded!";
        } catch (SQLException e) {
            return "Error recording offer: " + e.getMessage();
        }
    }
    
    public boolean hasExistingOffer(int userId, int itemId) {
        String query = "SELECT COUNT(*) FROM offers WHERE user_id = ? AND item_id = ?";
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            System.out.println("Error checking existing offer: " + e.getMessage());
        }
        return false; 
    }
    
    public String updateOffer(Offer offer) {
        String query = "UPDATE offers SET offer_price = ? WHERE user_id = ? AND item_id = ?";
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setBigDecimal(1, offer.getItemPrice());
            ps.setInt(2, offer.getUserId());
            ps.setInt(3, offer.getItemId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return "Offer successfully updated!";
            }
        } catch (SQLException e) {
            System.out.println("Error updating offer: " + e.getMessage());
            return "Failed to update offer: " + e.getMessage();
        }
        return "Failed to update offer."; 
    }
    
    public List<Offer> getAllSellerOffers(int sellerId) {
        List<Offer> offers = new ArrayList<>();
        String query = "SELECT o.offer_id, o.user_id, o.seller_id, o.item_id, o.offer_price, "
                        + "u.username AS buyer_name, i.item_name, i.item_price AS item_price "
                        + "FROM offers o "
                        + "JOIN users u ON o.user_id = u.id "
                        + "JOIN items i ON o.item_id = i.item_id "
                        + "WHERE o.seller_id = ?";
        
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, sellerId);  

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Offer offer = new Offer(
                    rs.getInt("offer_id"),
                    rs.getInt("user_id"),
                    rs.getInt("seller_id"),
                    rs.getInt("item_id"),
                    rs.getBigDecimal("offer_price")
                );

                offer.setBuyerName(rs.getString("buyer_name"));
                offer.setItemName(rs.getString("item_name"));
                offer.setItemPrice(rs.getBigDecimal("item_price"));

                offers.add(offer);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching offers: " + e.getMessage());
        }
        return offers;
    }


    
    public String declineOfferItem(Item targetItem, String reason, int userId) {
        String insertQuery = "INSERT INTO itemsRejected (item_name, item_size, item_price, item_category, seller_id, reason) "
                + "SELECT item_name, item_size, ?, item_category, seller_id, ? "
                + "FROM items WHERE item_id = ?";
        String deleteQuery = "DELETE FROM offers WHERE item_id = ? AND user_id = ?";

        try (PreparedStatement psInsert = db.prepareStatement(insertQuery);
             PreparedStatement psDelete = db.prepareStatement(deleteQuery)) {

            psInsert.setBigDecimal(1, targetItem.getItemPrice());  
            psInsert.setString(2, reason);     
            psInsert.setInt(3, targetItem.getItemId());  

            psInsert.executeUpdate();

            psDelete.setInt(1, targetItem.getItemId());  
            psDelete.setInt(2, userId);  

            psDelete.executeUpdate();

            return "Item " + targetItem.getItemId() + " successfully declined and moved to itemsRejected.";
        } catch (SQLException e) {
            return "Error declining item: " + e.getMessage();
        }
    }
    
    public String deleteOffer(int item_id, int user_id) {
        String deleteQuery = "DELETE FROM offers WHERE item_id = ? AND user_id = ?";
        try (PreparedStatement psDelete = db.prepareStatement(deleteQuery)) {
            psDelete.setInt(1, item_id);  
            psDelete.setInt(2, user_id);  

            int rowsAffected = psDelete.executeUpdate();

            if (rowsAffected > 0) {
                return "Item " + item_id + " successfully deleting offer ";
            } else {
                return "No item found with the given item_id and user_id.";
            }
        } catch (SQLException e) {
            return "Error deleting offer: " + e.getMessage();
        }
    }
    
    public List<TransactionHistory> getTransactionHistory(int userId) {
        List<TransactionHistory> historyList = new ArrayList<>();
        String query = "SELECT t.transaction_id, u.username AS seller_name, i.item_name, "
                       + "i.item_size, i.item_price, i.item_category, "
                       + "t.total_paid AS total_paid "
                       + "FROM transactions t "
                       + "JOIN users u ON t.seller_id = u.id "
                       + "JOIN items i ON t.item_id = i.item_id "
                       + "WHERE t.user_id = ?"; 
        
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, userId);  
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TransactionHistory history = new TransactionHistory(
                    rs.getInt("transaction_id"),
                    rs.getString("seller_name"),
                    rs.getString("item_name"),
                    rs.getString("item_size"),
                    rs.getBigDecimal("item_price"),
                    rs.getString("item_category"),
                    rs.getBigDecimal("total_paid")
                );
                historyList.add(history);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
        }
        return historyList;
    }

}
