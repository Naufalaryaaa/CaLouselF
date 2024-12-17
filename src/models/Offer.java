package models;

import java.math.BigDecimal;

public class Offer {
    private Integer offerId;
    private Integer userId;
    private Integer sellerId;
    private Integer itemId;
    private BigDecimal offerPrice;
    
    private String buyerName; 
    private String itemName; 
    private BigDecimal itemPrice; 
    
    public Offer(Integer offerId, Integer userId, Integer sellerId, Integer itemId, BigDecimal offerPrice) {
        this.offerId = offerId;
        this.userId = userId;
        this.sellerId = sellerId;
        this.itemId = itemId;
        this.offerPrice = offerPrice;
    }
    
    
    
    public Offer(Integer offerId, Integer userId, Integer sellerId, Integer itemId, BigDecimal offerPrice,
		String buyerName, String itemName, BigDecimal itemPrice) {
		super();
		this.offerId = offerId;
		this.userId = userId;
		this.sellerId = sellerId;
		this.itemId = itemId;
		this.offerPrice = offerPrice;
		this.buyerName = buyerName;
		this.itemName = itemName;
		this.itemPrice = itemPrice;
	}
    
    public Offer(int userId, int itemId, BigDecimal newOffer) {
    	this.userId = userId;
    	this.itemId = itemId;
    	this.offerPrice = newOffer;
    }

    public Offer(int userId, int sellerId, int itemId, BigDecimal newOffer) {
    	this.userId = userId;
    	this.sellerId = sellerId;
    	this.itemId = itemId;
    	this.offerPrice = newOffer;
    }


	public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

	public Integer getOfferId() {
		return offerId;
	}

	public void setOfferId(Integer offerId) {
		this.offerId = offerId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(BigDecimal offerPrice) {
		this.offerPrice = offerPrice;
	}

    
}
