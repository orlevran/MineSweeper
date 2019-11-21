package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setResizable(false);
        this.controller = new Controller(primaryStage);
        this.controller.showMenuBorderPane();
        primaryStage.setOnCloseRequest(e-> Platform.exit());
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
