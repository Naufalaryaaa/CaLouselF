package views;

import java.math.BigDecimal;

import controllers.ItemController;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Item;
import validators.ItemValidator;

public class EditItemView {

    public void show(Stage primaryStage, int itemId) {
        ItemController itemController = new ItemController();
        Item itemTarget = itemController.getItemById(itemId);
        
        BorderPane border = new BorderPane();
    	Button backBTN = new Button("Back");
    	backBTN.setOnAction(e -> {
    		new SellerItemView().start(primaryStage);
    	});
    	
    	border.setTop(backBTN);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        
        border.setCenter(grid);

        Label nameLabel = new Label("Item Name:");
        TextField nameField = new TextField(itemTarget.getItemName()); // Pre-fill data
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Label sizeLabel = new Label("Item Size:");
        TextField sizeField = new TextField(itemTarget.getItemSize()); // Pre-fill data
        grid.add(sizeLabel, 0, 1);
        grid.add(sizeField, 1, 1);

        Label priceLabel = new Label("Item Price:");
        TextField priceField = new TextField(itemTarget.getItemPrice().toString()); 
        grid.add(priceLabel, 0, 2);
        grid.add(priceField, 1, 2);

        Label categoryLabel = new Label("Item Category:");
        TextField categoryField = new TextField(itemTarget.getItemCategory()); 
        grid.add(categoryLabel, 0, 3);
        grid.add(categoryField, 1, 3);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 5);

        Button submitButton = new Button("Update Item");
        grid.add(submitButton, 1, 4);

        submitButton.setOnAction(e -> {
            String itemName = nameField.getText();
            String itemSize = sizeField.getText();
            String itemPrice = priceField.getText();
            String itemCategory = categoryField.getText();

            errorLabel.setText("");

            String errorMessage = ItemValidator.validateItemName(itemName);
            if (errorMessage != null) {
                errorLabel.setText(errorMessage);
                errorLabel.setTextFill(Color.RED);
                return;
            }

            errorMessage = ItemValidator.validateItemCategory(itemCategory);
            if (errorMessage != null) {
                errorLabel.setText(errorMessage);
                errorLabel.setTextFill(Color.RED);
                return;
            }

            errorMessage = ItemValidator.validateItemSize(itemSize);
            if (errorMessage != null) {
                errorLabel.setText(errorMessage);
                errorLabel.setTextFill(Color.RED);
                return;
            }

            errorMessage = ItemValidator.validateItemPrice(itemPrice);
            if (errorMessage != null) {
                errorLabel.setText(errorMessage);
                errorLabel.setTextFill(Color.RED);
                return;
            }

            BigDecimal price = new BigDecimal(itemPrice);
            itemTarget.setItemName(itemName);
            itemTarget.setItemCategory(itemCategory);
            itemTarget.setItemSize(itemSize);
            itemTarget.setItemPrice(price);

            String result = itemController.updateItem(itemTarget);

            errorLabel.setText(result);
            if (result.contains("Success")) {
                errorLabel.setTextFill(Color.GREEN);
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    new SellerItemView().start(primaryStage); 
                });
                pause.play();
            } else {
                errorLabel.setTextFill(Color.RED);
            }
        });

        Scene scene = new Scene(border, 400, 300);
        primaryStage.setTitle("Edit Item");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
