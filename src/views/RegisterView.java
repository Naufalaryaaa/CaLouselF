package views;

import controllers.UserController;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.User;
import validators.UserValidator;

public class RegisterView {

    public void show(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); 
        grid.setVgap(10); 
        grid.setHgap(10);  
        grid.setAlignment(Pos.CENTER); 

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("+62XXXXXXXXX");
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        addressField.setPromptText("Enter address");
        grid.add(addressLabel, 0, 3);
        grid.add(addressField, 1, 3);

        Label roleLabel = new Label("Role:");
        grid.add(roleLabel, 0, 4);
        ToggleGroup roleGroup = new ToggleGroup();

        RadioButton sellerRadio = new RadioButton("Seller");
        sellerRadio.setToggleGroup(roleGroup);
        grid.add(sellerRadio, 1, 4);

        RadioButton buyerRadio = new RadioButton("Buyer");
        buyerRadio.setToggleGroup(roleGroup);
        grid.add(buyerRadio, 1, 5);

        Button submitButton = new Button("Register");
        grid.add(submitButton, 1, 6);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        grid.add(errorLabel, 1, 7);

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        grid.add(loginLink, 1, 8);

        loginLink.setOnAction(event -> {
            new LoginView().show(primaryStage);
        });

        submitButton.setOnAction(e -> {

            String username = usernameField.getText();
            String password = passwordField.getText();
            String phoneNumber = phoneField.getText();
            String address = addressField.getText();
            String role = sellerRadio.isSelected() ? "seller" : buyerRadio.isSelected() ? "buyer" : "";

            errorLabel.setText(""); 

            String usernameError = UserValidator.validateUsername(username);
            String passwordError = UserValidator.validatePassword(password);
            String phoneError = UserValidator.validatePhoneNumber(phoneNumber);
            String addressError = UserValidator.validateAddress(address);
            String roleError = UserValidator.validateRole(role);

            if (usernameError != null) {
                errorLabel.setText(usernameError);
                return;
            }
            if (passwordError != null) {
                errorLabel.setText(passwordError);
                return;
            }
            if (phoneError != null) {
                errorLabel.setText(phoneError);
                return;
            }
            if (addressError != null) {
                errorLabel.setText(addressError);
                return;
            }
            if (roleError != null) {
                errorLabel.setText(roleError);
                return;
            }

            UserController userController = new UserController();
            String result = userController.registerUser(new User(username, password, phoneNumber, address, role));

            if (result.equalsIgnoreCase("Username Already Exist")) {
                errorLabel.setText(result);

                loginLink.setVisible(true);

                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(event -> new LoginView().show(primaryStage));
                pause.play();

                return;
            }

            if (result.equalsIgnoreCase("User successfully registered!")) {
                new LoginView().show(primaryStage);
            } else {
                errorLabel.setText(result);
            }
        });

        Scene scene = new Scene(grid, 400, 450);
        primaryStage.setTitle("Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
