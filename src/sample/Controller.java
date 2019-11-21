package sample;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {
    private Stage primaryStage;
    private MenuBorderPane menuBorderPane;
    private GameBoard gameBoard;

    public Controller(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.menuBorderPane = new MenuBorderPane(this);
    }

    public void showMenuBorderPane(){
        this.primaryStage.setScene(null);
        this.primaryStage.setScene(new Scene(this.menuBorderPane));
    }

    public void showGameBoard(int rows, int columns, int mines){
        this.gameBoard = new GameBoard(rows,columns,mines);
        this.primaryStage.setScene(null);
        this.primaryStage.setScene(new Scene(this.gameBoard));
    }
}
