package models;

import java.math.BigDecimal;

public class Transaction {
	private Integer userId;
	private Integer sellerId;
	private Integer itemId;
    private String sellerName;
    private String itemName;
    private String itemSize;
    private BigDecimal itemPrice;
    private String itemCategory;
    private BigDecimal totalPaid;
    
    public Transaction(int userId, int sellerId, int itemId, String itemName, String itemSize, BigDecimal itemPrice, String itemCategory, BigDecimal totalPaid) {
    	this.userId = userId;
    	this.sellerId = sellerId;
    	this.itemId = itemId;
    	this.itemName = itemName;
    	this.itemSize = itemSize;
    	this.itemPrice = itemPrice;
    	this.itemCategory = itemCategory;
    	this.totalPaid = totalPaid;
    }
    
    public Integer getUserId() { return userId; }
    public Integer getSellerId() { return sellerId; }
    public Integer getItemId() { return itemId; }
    public String getSellerName() { return sellerName; }
    public String getItemName() { return itemName; }
    public String getItemSize() { return itemSize; }
    public BigDecimal getItemPrice() { return itemPrice; }
    public String getItemCategory() { return itemCategory; }
    public BigDecimal getTotalPaid() { return totalPaid; }

}
