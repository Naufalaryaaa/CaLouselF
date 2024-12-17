package views;

import java.math.BigDecimal;
import java.util.List;

import controllers.ItemController;
import controllers.TransactionController;
import controllers.UserController;
import controllers.WishlistController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Item;
import models.Offer;
import models.Transaction;
import utils.SessionManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

public class BuyerItemView extends Application {

    private ItemController itemController;
    private WishlistController wishlistController;
    private ObservableList<Item> data;
    private TableView<Item> itemList;
    private Button backButton;
    private Scene scene;
    private String userRole;

    @Override
    public void start(Stage primaryStage) {
        initComponents();
        configureLayout(); 

        configureActions(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Item View");
        primaryStage.show();
    }

    private void initComponents() {
        itemController = new ItemController();
        wishlistController = new WishlistController();
        List<Item> items = itemController.getAllExistItems();
        data = FXCollections.observableArrayList(items);

        itemList = new TableView<>();
        backButton = new Button("Back");

        createTableColumns();
        scene = new Scene(createMainLayout(), 900, 400);
    }

    private void createTableColumns() {
        TableColumn<Item, String> itemName = new TableColumn<>("Item Name");
        itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<Item, String> itemCategory = new TableColumn<>("Item Category");
        itemCategory.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));

        TableColumn<Item, String> itemSize = new TableColumn<>("Item Size");
        itemSize.setCellValueFactory(new PropertyValueFactory<>("itemSize"));

        TableColumn<Item, BigDecimal> itemPrice = new TableColumn<>("Item Price");
        itemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));

        TableColumn<Item, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(300);  
        actionCol.setCellFactory(createActionButtons());

        itemList.getColumns().addAll(itemName, itemCategory, itemSize, itemPrice, actionCol);
        itemList.setItems(data);
    }

    private Callback<TableColumn<Item, Void>, TableCell<Item, Void>> createActionButtons() {
        return param -> new TableCell<>() {
            private final Button purchase = new Button("Purchase");
            private final Button offer = new Button("Make Offer");
            private final Button addWishlist = new Button("Add to Wishlist");
            private final HBox buttonGroup = new HBox(5, purchase, offer, addWishlist);

            {
                purchase.setOnAction(e -> handlePurchase(getCurrentItem()));
                offer.setOnAction(e -> handleOffer(getCurrentItem()));
                addWishlist.setOnAction(e -> handleWishlist(getCurrentItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonGroup);
                }
            }

            private Item getCurrentItem() {
                return getTableView().getItems().get(getIndex());
            }
        };
    }

    private void handlePurchase(Item item) {
        UserController userController = new UserController();
        TransactionController transactionController = new TransactionController();
        WishlistController wishlistController = new WishlistController();
        ItemController itemController = new ItemController();

        String username = SessionManager.getInstance().getUsername();
        int userId = userController.getIdByUsername(username);

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Purchase");
        confirmationAlert.setHeaderText("Are you sure you want to purchase this item?");
        confirmationAlert.setContentText("Item: " + item.getItemName() + "\nPrice: " + item.getItemPrice());

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response.getText().equalsIgnoreCase("OK")) {
                Transaction transaction = new Transaction(userId, item.getSellerId(), item.getItemId(), item.getItemName(), item.getItemSize(), item.getItemPrice(), item.getItemCategory(), item.getItemPrice());
                String transactionResult = transactionController.recordTransactions(transaction);

                if ("Transaction recorded successfully!".equals(transactionResult)) {
                    wishlistController.removeItemFromWishlist(item.getItemId());

                    String purchasedResult = itemController.purchasedItem(item.getItemId());
                    if ("Item status successfully updated to 'purchased'.".equals(purchasedResult)) {
                        showAlert("Transaction Status", "Purchase Successful", 
                                  "You have successfully purchased: " + item.getItemName());
                        refreshItemList();
                    } else {
                        showAlert("Purchase Failed", "Error updating item status", purchasedResult);
                    }
                } else {
                    showAlert("Transaction Failed", "Purchase Failed", "Error: " + transactionResult);
                }

                System.out.println("Purchased: " + item.getItemName());
            } else {
                System.out.println("Purchase canceled by user.");
            }
        });
    }


    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }



    private void refreshItemList() {
        List<Item> updatedItems = itemController.getAllExistItems();
        data.setAll(updatedItems); 
        itemList.setItems(data);   
    }
    

    private void handleOffer(Item item) {
        UserController userController = new UserController();
        TransactionController transactionController = new TransactionController();
        WishlistController wishlistController = new WishlistController();
        ItemController itemController = new ItemController();

        String username = SessionManager.getInstance().getUsername();
        int userId = userController.getIdByUsername(username);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Make an Offer");
        dialog.setHeaderText("Offer for " + item.getItemName());
        dialog.setContentText("Enter your offer price:");

        BigDecimal highestOfferValue = transactionController.getHighestOffer(userId, item.getItemId());
        if (highestOfferValue == null) {
            highestOfferValue = BigDecimal.ZERO;
        }

        final BigDecimal finalHighestOffer = highestOfferValue;
        String highestOfferMessage = (finalHighestOffer.compareTo(BigDecimal.ZERO) > 0)
            ? "Current highest offer: " + finalHighestOffer
            : "No offers yet.";

        dialog.setHeaderText("Offer for " + item.getItemName() + "\n" + highestOfferMessage);

        dialog.showAndWait().ifPresent(offerPrice -> {
            try {
                BigDecimal offer = new BigDecimal(offerPrice);

                if (offer.compareTo(finalHighestOffer) <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Offer",
                        "Your offer must be higher than the current highest offer (" + finalHighestOffer + ").");
                    return;
                }

                boolean hasExistingOffer = transactionController.hasExistingOffer(userId, item.getItemId());
                String result;
                
                if (hasExistingOffer) {
                    result = transactionController.updateOffer(new Offer(userId, item.getItemId(), offer));
                } else {
                    result = transactionController.makeOffer(new Offer(userId, item.getSellerId(), item.getItemId(), offer));
                }

                if (result.contains("successfully")) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Your offer of " + offer + " for " + item.getItemName() + " has been recorded.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", result);
                }

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric value for the offer.");
            }
        });
    }





    private void handleWishlist(Item item) {
        UserController userController = new UserController();
        String username = SessionManager.getInstance().getUsername();
        int userId = userController.getIdByUsername(username);

        String result = wishlistController.addToWishlist(item.getItemId(), userId);
        
        if (result.equals("Item successfully added to wishlist!")) {
            showAlert(AlertType.INFORMATION, "Success", result);
        } else if (result.equals("Item already in wishlist!")) {
            showAlert(AlertType.WARNING, "Warning", result);
        } else {
            showAlert(AlertType.ERROR, "Error", result);
        }
    }
    
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private BorderPane createMainLayout() {
        BorderPane border = new BorderPane();
        border.setTop(backButton);
        ScrollPane scrollPane = new ScrollPane(itemList);
        scrollPane.setFitToWidth(true);  
        border.setCenter(scrollPane);
        return border;
    }

    private void configureLayout() {
        
    }

    private void configureActions(Stage primaryStage) {
        backButton.setOnAction(e -> {
        	new HomeView().show(primaryStage);
            System.out.println("Navigating back to Home...");
        });
    }

    public static void itemView(String[] args) {
        launch(args);
    }
}
