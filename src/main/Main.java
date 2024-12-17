package main;

import javafx.application.Application;
import javafx.scene.control.ListView.EditEvent;
import javafx.stage.Stage;
import views.EditItemView;
import views.LoginView;
import views.SellerItemView;
import views.UploadItemView;

public class Main extends Application {

	public Main() {
		
	}

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		LoginView defaultStart = new LoginView();
		defaultStart.show(primaryStage);
		
    }

}
