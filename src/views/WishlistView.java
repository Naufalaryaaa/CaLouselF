package views;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.WishlistItem;
import utils.SessionManager;

import java.math.BigDecimal;
import java.util.List;

public class WishlistView {

    public void show(Stage primaryStage) {
        WishlistController wishlistController = new WishlistController();
        UserController userController = new UserController();
        String username = SessionManager.getInstance().getUsername();
        int userId = userController.getIdByUsername(username);
        List<WishlistItem> wishlistItems = wishlistController.getWishlistsByUserId(userId);
        ObservableList<WishlistItem> data = FXCollections.observableArrayList(wishlistItems);

        TableView<WishlistItem> tableView = new TableView<>(data);

        TableColumn<WishlistItem, Integer> wishlistIdCol = new TableColumn<>("Wishlist ID");
        wishlistIdCol.setCellValueFactory(new PropertyValueFactory<>("wishlistId"));

        TableColumn<WishlistItem, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        
        TableColumn<WishlistItem, String> itemSizeCol = new TableColumn<>("Item Size");
        itemSizeCol.setCellValueFactory(new PropertyValueFactory<>("itemSize"));

        TableColumn<WishlistItem, BigDecimal> itemPriceCol = new TableColumn<>("Item Price");
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));

        TableColumn<WishlistItem, String> itemCategoryCol = new TableColumn<>("Item Category");
        itemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));


        TableColumn<WishlistItem, Void> actionCol = new TableColumn<>("Actions");

        Callback<TableColumn<WishlistItem, Void>, TableCell<WishlistItem, Void>> cellFactory = param -> new TableCell<>() {

            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(e -> {
                    WishlistItem selectedItem = getTableView().getItems().get(getIndex());
                    String result = wishlistController.removeWishlist(selectedItem.getWishlistId());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    if (result.contains("successfully")) {
                        data.remove(selectedItem);
                        tableView.refresh();
                        alert.setHeaderText("Item Removed");
                        alert.setContentText(result);
                    } else {
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setHeaderText("Failed to Remove");
                        alert.setContentText(result);
                    }
                    alert.showAndWait();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        };

        actionCol.setCellFactory(cellFactory);
        Button backBtn = new Button("Back");
        backBtn.setOnAction(event -> {
        	new HomeView().show(primaryStage);;
        });
        
        tableView.getColumns().addAll(itemNameCol, itemCategoryCol, itemSizeCol, itemPriceCol, actionCol);

        VBox vbox = new VBox(backBtn, tableView);
        Scene scene = new Scene(vbox, 600, 400);

        primaryStage.setTitle("Wishlist");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
