package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import models.Item;
import models.ItemQueue;

public class ItemController {
	
	private Database db;

    public ItemController() {
        db = Database.getInstance();
    }

	public String uploadItemToQueue(ItemQueue itemQueue) {
        try {
        	String itemPrice = itemQueue.getItemPrice().toString();
            String query = "INSERT INTO itemsQueue (seller_id, item_name, item_category, item_size, item_price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = db.prepareStatement(query);
            ps.setInt(1, itemQueue.getSellerId());
            ps.setString(2, itemQueue.getItemName());
            ps.setString(3, itemQueue.getItemCategory());
            ps.setString(4, itemQueue.getItemSize());
            ps.setString(5, itemPrice);
            ps.executeUpdate();
            return "Success Upload Item!.. Please wait for admin approvement";
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                return "Item already exist";
            }
            return "Error while Uploading Item: " + e.getMessage();
        }
    }
	
	public List<ItemQueue> getAllItemsQueue() {
        List<ItemQueue> items = new ArrayList<>();
        String query = "SELECT * FROM itemsQueue";
        
        try (ResultSet rs = Database.getInstance().prepareStatement(query).executeQuery()) {
            while (rs.next()) {
                ItemQueue item = new ItemQueue(
                    rs.getInt("item_id"),
                    rs.getInt("seller_id"),
                    rs.getString("item_name"),
                    rs.getString("item_size"),
                    rs.getBigDecimal("item_price"),
                    rs.getString("item_category")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
	
	public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items";
        
        try (ResultSet rs = Database.getInstance().prepareStatement(query).executeQuery()) {
            while (rs.next()) {
                Item item = new Item(
                    rs.getInt("item_id"),
                    rs.getInt("seller_id"),
                    rs.getString("item_name"),
                    rs.getString("item_size"),
                    rs.getBigDecimal("item_price"),
                    rs.getString("item_category"),
                    rs.getString("item_status")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
	public List<Item> getAllExistItems() {
		List<Item> items = new ArrayList<>();
		String query = "SELECT * FROM items where item_status = 'exist'";
		
		try (ResultSet rs = Database.getInstance().prepareStatement(query).executeQuery()) {
			while (rs.next()) {
				Item item = new Item(
						rs.getInt("item_id"),
						rs.getInt("seller_id"),
						rs.getString("item_name"),
						rs.getString("item_size"),
						rs.getBigDecimal("item_price"),
						rs.getString("item_category"),
						rs.getString("item_status")
						);
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	public List<Item> getSellerItems(int seller_id) {
	    List<Item> items = new ArrayList<>();
	    String query = "SELECT * FROM items WHERE seller_id = ?";

	    try (PreparedStatement pstmt = Database.getInstance().prepareStatement(query)) {
	        pstmt.setInt(1, seller_id); 
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Item item = new Item(
	                    rs.getInt("item_id"),
	                    rs.getInt("seller_id"),
	                    rs.getString("item_name"),
	                    rs.getString("item_size"),
	                    rs.getBigDecimal("item_price"),
	                    rs.getString("item_category"),
	                    rs.getString("item_status")
	                );
	                items.add(item);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return items;
	}

	
	public ItemQueue getItemQueueById(int itemId) {
        String query = "SELECT * FROM itemsQueue WHERE item_id = ?";
        ItemQueue item = null;

        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item = new ItemQueue(
                        rs.getInt("item_id"),
                        rs.getInt("seller_id"),
                        rs.getString("item_name"),
                        rs.getString("item_size"),
                        rs.getBigDecimal("item_price"),
                        rs.getString("item_category")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;
    }
	public Item getItemById(int itemId) {
		String query = "SELECT * FROM items WHERE item_id = ?";
		Item item = null;
		
		try (PreparedStatement ps = db.prepareStatement(query)) {
			ps.setInt(1, itemId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					item = new Item(
							rs.getInt("item_id"),
							rs.getInt("seller_id"),
							rs.getString("item_name"),
							rs.getString("item_size"),
							rs.getBigDecimal("item_price"),
							rs.getString("item_category"),
							rs.getString("item_status")
							);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return item;
	}
	
	public String updateItem(Item newItem) {
	    String query = "UPDATE items SET item_name = ?, item_size = ?, item_price = ?, item_category = ? WHERE item_id = ?";
	    
	    try (PreparedStatement ps = db.prepareStatement(query)) {
	        ps.setString(1, newItem.getItemName());
	        ps.setString(2, newItem.getItemSize());
	        ps.setBigDecimal(3, newItem.getItemPrice());
	        ps.setString(4, newItem.getItemCategory());
	        ps.setInt(5, newItem.getItemId());
	        
	        int rowsUpdated = ps.executeUpdate();
	        if (rowsUpdated > 0) {
	            return "Success: Item updated!";
	        } else {
	            return "Error: Item not found or no changes made.";
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return "Error: Failed to update item due to a database error.";
	    }
	}
	
	public String deleteItem(int itemId) {
	    String query = "DELETE FROM items WHERE item_id = ?";
	    
	    try (PreparedStatement ps = db.prepareStatement(query)) {
	        ps.setInt(1, itemId);
	        
	        int rowsDeleted = ps.executeUpdate();
	        if (rowsDeleted > 0) {
	            return "Success: Item with ID " + itemId + " has been deleted.";
	        } else {
	            return "Error: Item with ID " + itemId + " not found.";
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return "Error: Failed to delete item due to a database error.";
	    }
	}

	
	public void approveItem(ItemQueue newItem) {
	    String insertQuery = "INSERT INTO items (seller_id, item_name, item_size, item_price, item_category, item_status) "
	                       + "SELECT seller_id, item_name, item_size, item_price, item_category, 'exist' FROM itemsQueue WHERE item_id = ?";

	    String deleteQuery = "DELETE FROM itemsQueue WHERE item_id = ?";

	    try (PreparedStatement psInsert = db.prepareStatement(insertQuery);
	         PreparedStatement psDelete = db.prepareStatement(deleteQuery)) {

	        psInsert.setInt(1, newItem.getItemId());

	        psInsert.executeUpdate();

	        psDelete.setInt(1, newItem.getItemId());
	        psDelete.executeUpdate();

	        System.out.println("Item " + newItem.getItemId() + " has been approved and moved to items.");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	public void declineItem(ItemQueue targetItem, String reason) {
		 String insertQuery = "INSERT INTO itemsRejected (item_name, item_size, item_price, item_category, seller_id, reason) "
		            + "SELECT item_name, item_size, item_price, item_category, seller_id, ? "
		            + "FROM itemsQueue WHERE item_id = ?";
	    String deleteQuery = "DELETE FROM itemsQueue WHERE item_id = ?";

	    try (PreparedStatement psInsert = db.prepareStatement(insertQuery);
	         PreparedStatement psDelete = db.prepareStatement(deleteQuery)) {

	        psInsert.setString(1, reason);  
	        psInsert.setInt(2, targetItem.getItemId());  

	        psInsert.executeUpdate();
	        psDelete.setInt(1, targetItem.getItemId());  

	        psDelete.executeUpdate();

	        System.out.println("Item " + targetItem.getItemId() + " has been declined and moved to itemsRejected.");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public String purchasedItem(int itemId) {
	    String updateItemStatusQuery = "UPDATE items SET item_status = 'purchased' WHERE item_id = ?";

	    try (PreparedStatement psUpdate = db.prepareStatement(updateItemStatusQuery)) {
	        psUpdate.setInt(1, itemId);

	        int affectedRows = psUpdate.executeUpdate();
	        
	        if (affectedRows > 0) {
	            System.out.println("Item " + itemId + " status has been updated to 'purchased'.");
	            return "Item status successfully updated to 'purchased'.";
	        } else {
	            return "Item with the provided ID not found.";
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return "An error occurred while updating the item status.";
	    }
	}

}
