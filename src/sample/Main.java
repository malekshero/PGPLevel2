package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.security.GeneralSecurityException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("BankTransactionsLEVEL2PGP");
        primaryStage.setScene(new Scene(root, 644, 399));
        primaryStage.show();
    }
    public static void main(String[] args) throws GeneralSecurityException {
        launch(args);
    }
}
