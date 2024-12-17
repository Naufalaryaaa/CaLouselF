package models;

import java.math.BigDecimal;

public class TransactionHistory {

    private Integer transactionId;
    private String sellerName;
    private String itemName;
    private String itemSize;
    private BigDecimal itemPrice;
    private String itemCategory;
    private BigDecimal totalPaid;

    public TransactionHistory(Integer transactionId, String sellerName, String itemName, String itemSize, BigDecimal itemPrice, String itemCategory, BigDecimal totalPaid) {
        this.transactionId = transactionId;
        this.sellerName = sellerName;
        this.itemName = itemName;
        this.itemSize = itemSize;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
        this.totalPaid = totalPaid;
    }

    public Integer getTransactionId() { return transactionId; }
    public String getSellerName() { return sellerName; }
    public String getItemName() { return itemName; }
    public String getItemSize() { return itemSize; }
    public BigDecimal getItemPrice() { return itemPrice; }
    public String getItemCategory() { return itemCategory; }
    public BigDecimal getTotalPaid() { return totalPaid; }
}
