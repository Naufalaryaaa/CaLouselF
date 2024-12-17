package models;

import java.math.BigDecimal;

public class ItemQueue {

	private int itemId;
    private int sellerId;
    private String itemName;         
    private String itemSize;         
    private BigDecimal itemPrice;    
    private String itemCategory;     

    public ItemQueue() {
    }

    public ItemQueue(int itemId, int sellerId, String itemName, String itemSize, BigDecimal itemPrice, String itemCategory) {
    	this.itemId = itemId;
        this.sellerId = sellerId;
        this.itemName = itemName;
        this.itemSize = itemSize;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
    }
    
    public ItemQueue(int sellerId, String itemName, String itemSize, BigDecimal itemPrice, String itemCategory) {
    	this.sellerId = sellerId;
    	this.itemName = itemName;
    	this.itemSize = itemSize;
    	this.itemPrice = itemPrice;
    	this.itemCategory = itemCategory;
    }
    
    public int getItemId() {return itemId; }
    public String getItemName() { return itemName; }
    public String getItemSize() { return itemSize; }
    public BigDecimal getItemPrice() { return itemPrice; }
    public String getItemCategory() { return itemCategory; }
    public int getSellerId() { return sellerId; }
}
