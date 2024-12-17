package validators;

import java.math.BigDecimal;

public class ItemValidator {

    public static String validateItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return "Item Name - Cannot be empty.";
        }
        if (itemName.length() < 3) {
            return "Item Name - Must be at least 3 characters long.";
        }
        return null;
    }

    public static String validateItemCategory(String itemCategory) {
        if (itemCategory == null || itemCategory.trim().isEmpty()) {
            return "Item Category - Cannot be empty.";
        }
        if (itemCategory.length() < 3) {
            return "Item Category - Must be at least 3 characters long.";
        }
        return null; 
    }

    public static String validateItemSize(String itemSize) {
        if (itemSize == null || itemSize.trim().isEmpty()) {
            return "Item Size - Cannot be empty.";
        }
        return null; 
    }

    public static String validateItemPrice(String itemPrice) {
        if (itemPrice == null || itemPrice.trim().isEmpty()) {
            return "Item Price - Cannot be empty.";
        }
        try {
            BigDecimal price = new BigDecimal(itemPrice);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                return "Item Price - Cannot be 0 or less.";
            }
        } catch (NumberFormatException e) {
            return "Item Price - Must be a valid number.";
        }
        return null;
    }
}
