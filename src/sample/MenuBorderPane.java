package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MenuBorderPane extends BorderPane {
    private Label headline, errorLabel;
    private Label[] levelsLabels;
    private GridPane gridPane;
    private RadioButton[] radioButtons;
    private ToggleGroup toggleGroup;
    private HBox hBox;
    private VBox vBox, errorVBox;
    private ComboBox<String> rowsComboBox, columnsComboBox;
    private TextField textField;
    private Button startGameButton, closeErrorButton;
    private EventHandler<ActionEvent> eventHandler;
    private Stage errorStage;

    private final int NUMBER_OF_LEVELS = 4;
    private final String[] RADIO_BUTTONS_LABELS = {"8 X 8, 10 Mines", "16 X 16, 40 Mines", "16 X 32, 99 Mines"};
    private final String[] RADIO_BUTTONS_TEXT = {"Easy", "Medium ", "Hard", "Custom"};

    public MenuBorderPane(Controller controller) {
        super();
        super.setStyle("-fx-background-color:indigo");
        this.headline = new Label("MINESWEEPER");
        this.headline.setFont(Font.font("Comic Sans MS", 70));
        this.headline.setTextFill(Color.AQUAMARINE);
        super.setTop(this.headline);
        insertGridPane();
        super.setCenter(this.gridPane);

        this.startGameButton = new Button("Let's go!");
        this.startGameButton.setFont(Font.font("Ink Free", FontWeight.BOLD, 40));
        this.vBox = new VBox();
        this.vBox.setAlignment(Pos.CENTER);
        this.vBox.getChildren().add(this.startGameButton);
        super.setBottom(this.vBox);

        insertErrorStage();
        insertEventHandler(controller);
    }

    public void insertGridPane() {
        this.gridPane = new GridPane();
        this.toggleGroup = new ToggleGroup();

        this.levelsLabels = new Label[NUMBER_OF_LEVELS];
        this.radioButtons = new RadioButton[NUMBER_OF_LEVELS];
        for (int i = 0; i < this.NUMBER_OF_LEVELS - 1; i++) {
            this.levelsLabels[i] = new Label(RADIO_BUTTONS_LABELS[i]);
            this.levelsLabels[i].setFont(Font.font("Gadugi", 30));
            this.levelsLabels[i].setTextFill(Color.BURLYWOOD);
            insertRadioButton(this.RADIO_BUTTONS_TEXT[i], this.levelsLabels[i], i);
        }

        this.radioButtons[this.NUMBER_OF_LEVELS - 1] = new RadioButton(this.RADIO_BUTTONS_TEXT[this.NUMBER_OF_LEVELS - 1]);
        this.radioButtons[this.NUMBER_OF_LEVELS - 1].setFont(Font.font("Gadugi", 30));
        this.radioButtons[this.NUMBER_OF_LEVELS - 1].setTextFill(Color.BURLYWOOD);
        this.radioButtons[this.NUMBER_OF_LEVELS - 1].setToggleGroup(this.toggleGroup);
        this.gridPane.add(this.radioButtons[this.NUMBER_OF_LEVELS - 1], 0, this.NUMBER_OF_LEVELS - 1);

        this.hBox = new HBox(10);
        this.hBox.setAlignment(Pos.CENTER_LEFT);

        this.rowsComboBox = new ComboBox<String>();
        this.columnsComboBox = new ComboBox<>();
        insertComboBox(this.rowsComboBox, "Rows", 8, 20);
        insertComboBox(this.columnsComboBox, "Columns", 8, 40);

        this.textField = new TextField("Mines");
        this.hBox.getChildren().add(this.textField);
        this.gridPane.add(this.hBox, 1, this.NUMBER_OF_LEVELS - 1);
    }

    public void insertRadioButton(String text, Label label, int row) {
        this.radioButtons[row] = new RadioButton(text);
        this.radioButtons[row].setFont(Font.font("Gadugi", 30));
        this.radioButtons[row].setTextFill(Color.BURLYWOOD);
        this.radioButtons[row].setToggleGroup(this.toggleGroup);
        this.gridPane.add(this.radioButtons[row], 0, row);
        this.gridPane.add(label, 1, row);
        if (row == 0)
            this.radioButtons[row].setSelected(true);
    }

    public void insertComboBox(ComboBox<String> comboBox, String text, int min, int max) {
        for (int i = min; i <= max; i++) {
            comboBox.getItems().add("" + i);
        }
        comboBox.setPromptText(text);

        this.hBox.getChildren().add(comboBox);
    }

    public void insertErrorStage() {
        this.errorStage = new Stage();
        this.errorLabel = new Label();
        this.errorLabel.setFont(Font.font("Lucida Console", 20));
        this.closeErrorButton = new Button("OK");
        this.closeErrorButton.setFont(Font.font("Lucida Console", 20));

        this.errorVBox = new VBox(5);
        this.errorVBox.setAlignment(Pos.CENTER);
        this.errorVBox.getChildren().add(this.errorLabel);
        this.errorVBox.getChildren().add(this.closeErrorButton);

        this.errorStage.setAlwaysOnTop(true);
        this.errorStage.setResizable(false);
        this.errorStage.setScene(new Scene(new Pane(this.errorVBox)));
    }

    public void showError(String text) {
        this.errorLabel.setText(text);
        this.errorStage.show();
    }

    public void insertEventHandler(Controller controller) {
        this.eventHandler = event -> {
            if (event.getSource().equals(this.startGameButton) && !this.errorStage.isShowing()) {
                if (this.radioButtons[0].isSelected()) {
                    controller.showGameBoard(8, 8, 10);
                } else if (this.radioButtons[1].isSelected()) {
                    controller.showGameBoard(16, 16, 40);
                } else if (this.radioButtons[2].isSelected()) {
                    controller.showGameBoard(16, 32, 99);
                } else if (this.radioButtons[3].isSelected()) {
                    if (textField.getText().matches("[0-9]+")) {
                        if (!this.rowsComboBox.getSelectionModel().isEmpty() && !this.columnsComboBox.getSelectionModel().isEmpty()) {
                            int numOfMines = Integer.parseInt(this.textField.getText());
                            int highBar = Integer.parseInt(this.rowsComboBox.getValue()) * Integer.parseInt(this.columnsComboBox.getValue()) / 5;
                            if (numOfMines >= 10 && numOfMines <= highBar) {
                                controller.showGameBoard(Integer.parseInt(this.rowsComboBox.getValue()), Integer.parseInt(this.columnsComboBox.getValue()), Integer.parseInt(this.textField.getText()));
                            } else {
                                showError(String.format("Mines range have to be between 10 to %d", (int) highBar));
                            }
                        } else {
                            showError("Missing information");
                        }
                    } else {
                        showError("Invalid input");
                    }
                }
            }
            if (event.getSource().equals(closeErrorButton) && this.errorStage.isShowing()) {
                this.errorStage.close();
            }
        };
        for (int i = 0; i < this.radioButtons.length; i++)
            this.radioButtons[i].setOnAction(this.eventHandler);
        this.rowsComboBox.setOnAction(this.eventHandler);
        this.columnsComboBox.setOnAction(this.eventHandler);
        this.textField.setOnAction(this.eventHandler);
        this.startGameButton.setOnAction(this.eventHandler);
        this.closeErrorButton.setOnAction(this.eventHandler);
    }
}