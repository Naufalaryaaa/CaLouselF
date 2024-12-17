package views;

import controllers.ItemController;
import controllers.TransactionController;
import controllers.UserController;
import controllers.WishlistController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Item;
import models.Offer;
import models.Transaction;
import utils.SessionManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OfferView {

    public void show(Stage primaryStage) {
    	TransactionController transactionController = new TransactionController();
    	UserController userController = new UserController();
        String username = SessionManager.getInstance().getUsername();
        int sellerId = userController.getIdByUsername(username);
    	List<Offer> offerList = transactionController.getAllSellerOffers(sellerId);
    	ObservableList<Offer> data = FXCollections.observableArrayList(offerList);

    	TableView<Offer> tableView = new TableView<>(data);

    	TableColumn<Offer, String> buyerCol = new TableColumn<>("Buyer Name");
    	buyerCol.setCellValueFactory(new PropertyValueFactory<>("buyerName"));

    	TableColumn<Offer, String> itemNameCol = new TableColumn<>("Item Name");
    	itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

    	TableColumn<Offer, BigDecimal> itemPriceCol = new TableColumn<>("Item Price");
    	itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));

    	TableColumn<Offer, BigDecimal> offerPriceCol = new TableColumn<>("Offer Price");
    	offerPriceCol.setCellValueFactory(new PropertyValueFactory<>("offerPrice"));

        TableColumn<Offer, Void> actionCol = new TableColumn<>("Actions");

        Callback<TableColumn<Offer, Void>, TableCell<Offer, Void>> cellFactory = param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button declineButton = new Button("Decline");
            
            private void showAlert(Alert.AlertType alertType, String headerText, String contentText) {
        	    Alert alert = new Alert(alertType);
        	    alert.setHeaderText(headerText);
        	    alert.setContentText(contentText);
        	    alert.showAndWait();
        	}

            {
                acceptButton.setOnAction(e -> {
                    UserController userController = new UserController();
                    String username = SessionManager.getInstance().getUsername();
                    int userId = userController.getIdByUsername(username);
                    Offer selectedOffer = getTableView().getItems().get(getIndex());

                    if (selectedOffer == null) {
                        showAlert(Alert.AlertType.ERROR, "No Offer Selected", "Please select an offer to accept.");
                        return;
                    }

                    ItemController itemController = new ItemController();
                    Item item = itemController.getItemById(selectedOffer.getItemId());

                    if (item == null) {
                        showAlert(Alert.AlertType.ERROR, "Item Not Found", "The item associated with the selected offer could not be found.");
                        return;
                    }

                    TransactionController transactionController = new TransactionController();
                    String transactionResult = transactionController.recordTransactions(
                    	new Transaction(
                        selectedOffer.getUserId(),
                        selectedOffer.getSellerId(),
                        item.getItemId(),
                        item.getItemName(),
                        item.getItemSize(),
                        item.getItemPrice(),
                        item.getItemCategory(),
                        selectedOffer.getOfferPrice())
                    );

                    if (!transactionResult.contains("successfully")) {
                        showAlert(Alert.AlertType.ERROR, "Failed to Accept Offer", transactionResult);
                        return;
                    }

                    WishlistController wishlistController = new WishlistController();
                    wishlistController.removeItemFromWishlist(selectedOffer.getItemId());
                    itemController.purchasedItem(selectedOffer.getItemId());

                    String deleteResult = transactionController.deleteOffer(selectedOffer.getItemId(), selectedOffer.getUserId());

                    if (deleteResult.contains("successfully")) {
                        showAlert(Alert.AlertType.INFORMATION, "Offer Accepted", transactionResult);
                        data.remove(selectedOffer);
                        tableView.refresh();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to Decline Offer", deleteResult);
                    }
                });
           

                declineButton.setOnAction(e -> {
                    ItemController itemController = new ItemController();
                    Offer selectedOffer = getTableView().getItems().get(getIndex());
                    Item getItem = itemController.getItemById(selectedOffer.getItemId());

                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Decline Offer");
                    dialog.setHeaderText("Decline Offer with Reason");
                    dialog.setContentText("Please enter a reason:");

                    Optional<String> reason = dialog.showAndWait();

                    if (reason.isPresent() && !reason.get().trim().isEmpty()) {
                        String declineResult = transactionController.declineOfferItem(
                                getItem,
                                reason.get(),  
                                selectedOffer.getUserId()  
                        );

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Offer Declined");
                        alert.setContentText(declineResult);
                        alert.showAndWait();

                        if (declineResult.contains("successfully")) {
                            data.remove(selectedOffer);
                            tableView.refresh();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Reason Required");
                        alert.setContentText("Please provide a reason to decline the offer.");
                        alert.showAndWait();
                    }

                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    
                    HBox buttons = new HBox(10); 
                    buttons.getChildren().addAll(acceptButton, declineButton);

                    buttons.setStyle("-fx-alignment: center;");

                    setGraphic(buttons);
                }
            }
        };

        actionCol.setCellFactory(cellFactory);
        actionCol.setPrefWidth(200);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(event -> new HomeView().show(primaryStage));

        tableView.getColumns().addAll(buyerCol, itemNameCol, itemPriceCol, offerPriceCol, actionCol);

        VBox vbox = new VBox(backBtn, tableView);
        Scene scene = new Scene(vbox, 800, 600);

        primaryStage.setTitle("Offer List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
