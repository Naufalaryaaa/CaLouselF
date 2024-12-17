package views;

import controllers.TransactionController;
import controllers.UserController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.TransactionHistory;
import utils.SessionManager;

import java.math.BigDecimal;
import java.util.List;

public class TransactionHistoryView {

    public void show(Stage primaryStage) {
        TransactionController transactionController = new TransactionController();
        UserController userController = new UserController();
        String username = SessionManager.getInstance().getUsername();
        int userId = userController.getIdByUsername(username);

        List<TransactionHistory> historyList = transactionController.getTransactionHistory(userId);
        ObservableList<TransactionHistory> data = FXCollections.observableArrayList(historyList);

        TableView<TransactionHistory> tableView = new TableView<>(data);

        TableColumn<TransactionHistory, Integer> transactionIdCol = new TableColumn<>("Transaction ID");
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        TableColumn<TransactionHistory, String> sellerNameCol = new TableColumn<>("Seller Name");
        sellerNameCol.setCellValueFactory(new PropertyValueFactory<>("sellerName"));

        TableColumn<TransactionHistory, String> itemNameCol = new TableColumn<>("Item Name");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        
        TableColumn<TransactionHistory, String> itemCategoryCol = new TableColumn<>("Item Category");
        itemCategoryCol.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));
        
        TableColumn<TransactionHistory, String> itemSizeCol = new TableColumn<>("Item Size");
        itemSizeCol.setCellValueFactory(new PropertyValueFactory<>("itemSize"));

        TableColumn<TransactionHistory, BigDecimal> itemPriceCol = new TableColumn<>("Item Price");
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));

        TableColumn<TransactionHistory, BigDecimal> totalPaidCol = new TableColumn<>("Total Paid");
        totalPaidCol.setCellValueFactory(new PropertyValueFactory<>("totalPaid"));

        tableView.getColumns().addAll(transactionIdCol, sellerNameCol, itemNameCol, itemCategoryCol, itemSizeCol, itemPriceCol, totalPaidCol);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(event -> new HomeView().show(primaryStage));

        VBox vbox = new VBox(backBtn, tableView);
        Scene scene = new Scene(vbox, 800, 600);

        primaryStage.setTitle("Transaction History");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
