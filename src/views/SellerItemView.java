package views;

import java.util.List;

import controllers.ItemController;
import controllers.UserController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Item;
import utils.SessionManager;

public class SellerItemView extends Application {

    public void start(Stage primaryStage) {
        ItemController itemController = new ItemController();
        UserController userController = new UserController();
        String username = SessionManager.getInstance().getUsername();
        int userId = userController.getIdByUsername(username);

        List<Item> items = itemController.getSellerItems(userId);
        ObservableList<Item> data = FXCollections.observableArrayList(items);

        TableView<Item> tableView = new TableView<>(data);

        TableColumn<Item, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<Item, String> itemCategoryCol = new TableColumn<>("Item Category");
        itemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));

        TableColumn<Item, String> itemSizeCol = new TableColumn<>("Item Size");
        itemSizeCol.setCellValueFactory(new PropertyValueFactory<>("itemSize"));

        TableColumn<Item, Double> priceCol = new TableColumn<>("Item Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        
        TableColumn<Item, Double> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("itemStatus"));

        TableColumn<Item, Void> actionCol = new TableColumn<>("Actions");

        Callback<TableColumn<Item, Void>, TableCell<Item, Void>> cellFactory =
                new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
                    @Override
                    public TableCell<Item, Void> call(final TableColumn<Item, Void> param) {
                        return new TableCell<Item, Void>() {

                            private final Button editButton = new Button("Edit");
                            private final Button removeButton = new Button("Remove");

                            {
                                editButton.setOnAction(e -> {
                                    Item selectedItem = getTableView().getItems().get(getIndex());
                                    System.out.println("Edit item: " + selectedItem.getItemName());
                                    new EditItemView().show(primaryStage, selectedItem.getItemId());
                                    tableView.refresh();
                                });

                                removeButton.setOnAction(e -> {
                                    Item selectedItem = getTableView().getItems().get(getIndex());
                                    String result = itemController.deleteItem(selectedItem.getItemId());

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Delete Item");
                                    
                                    if (result.startsWith("Success")) {
                                        data.remove(selectedItem);
                                        tableView.refresh();
                                        
                                        alert.setAlertType(Alert.AlertType.INFORMATION);
                                        alert.setHeaderText("Item Deleted");
                                        alert.setContentText(result); 
                                    } else {
                                        alert.setAlertType(Alert.AlertType.ERROR);
                                        alert.setHeaderText("Failed to Delete Item");
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
                                    setGraphic(new HBox(10, editButton, removeButton));
                                }
                            }
                        };
                    }
                };

        actionCol.setCellFactory(cellFactory);
        tableView.getColumns().addAll(itemNameCol, itemCategoryCol, itemSizeCol, priceCol, statusCol, actionCol);
        

        Button backBtn = new Button("Back");
        backBtn.setOnAction(event -> {
            new HomeView().show(primaryStage);
        });

        VBox vBox = new VBox(10, backBtn, tableView);

        Scene scene = new Scene(vBox, 600, 400);

        primaryStage.setTitle("Edit/Remove Items");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
