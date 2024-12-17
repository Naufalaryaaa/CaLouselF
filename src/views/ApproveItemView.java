package views;

import java.util.List;

import controllers.ItemController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import models.ItemQueue;

public class ApproveItemView {

    public void show(Stage primaryStage) {
        ItemController itemController = new ItemController();
        List<ItemQueue> items = itemController.getAllItemsQueue(); 
        ObservableList<ItemQueue> data = FXCollections.observableArrayList(items);
        Button backBtn = new Button("Back");
        backBtn.setOnAction(event -> {
        	new HomeView().show(primaryStage);
        });

        TableView<ItemQueue> tableView = new TableView<>(data);

        TableColumn<ItemQueue, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        
        TableColumn<ItemQueue, String> itemCategoryCol = new TableColumn<>("Item Category");
        itemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));

        TableColumn<ItemQueue, String> itemSizeCol = new TableColumn<>("Item Size");
        itemSizeCol.setCellValueFactory(new PropertyValueFactory<>("itemSize"));

        TableColumn<ItemQueue, Double> priceCol = new TableColumn<>("Item Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));

        TableColumn<ItemQueue, Void> actionCol = new TableColumn<>("Actions");

        Callback<TableColumn<ItemQueue, Void>, TableCell<ItemQueue, Void>> cellFactory = 
            new Callback<TableColumn<ItemQueue, Void>, TableCell<ItemQueue, Void>>() {
                @Override
                public TableCell<ItemQueue, Void> call(final TableColumn<ItemQueue, Void> param) {
                    return new TableCell<ItemQueue, Void>() {

                        private final Button approveButton = new Button("Approve");
                        private final Button declineButton = new Button("Decline");

                        {
                            approveButton.setOnAction(e -> {
                                ItemQueue selectedItem = getTableView().getItems().get(getIndex());
                                itemController.approveItem(selectedItem);
                                data.remove(selectedItem);
                                tableView.refresh();
                            });

                            declineButton.setOnAction(e -> {
                                ItemQueue selectedItem = getTableView().getItems().get(getIndex());

                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setTitle("Decline Reason");
                                dialog.setHeaderText("Please provide a reason for declining the item.");
                                dialog.setContentText("Reason:");

                                dialog.showAndWait().ifPresent(reason -> {
                                    if (reason != null && !reason.isEmpty()) {
                                        System.out.println("Declined item ID: " + selectedItem.getItemId() + " for reason: " + reason);
                                        itemController.declineItem(selectedItem, reason);
                                        data.remove(selectedItem);
                                        tableView.refresh();
                                    } else {
                                        Alert alert = new Alert(AlertType.WARNING);
                                        alert.setTitle("No Reason Provided");
                                        alert.setHeaderText("You must provide a reason to decline.");
                                        alert.showAndWait();
                                    }
                                });
                            });
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(new HBox(10, approveButton, declineButton));
                            }
                        }
                    };
                }
            };
        actionCol.setCellFactory(cellFactory);
        tableView.getColumns().addAll(itemNameCol, itemCategoryCol, itemSizeCol, priceCol, actionCol);

        VBox vBox = new VBox(10, backBtn, tableView);

        Scene scene = new Scene(vBox, 600, 400);
        
        primaryStage.setTitle("Approve Items");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
