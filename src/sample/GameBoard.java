package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.*;

public class GameBoard extends BorderPane {
    private GridPane gridPane;
    private GameButton[][] gameButtons;
    private BorderPane borderPane;
    private Label flagsLeftLabel, timerLabel;
    private Button resetButton;
    private ImageView imageView, clickedMine;
    private Stack<ImageView> flagsImageViews;
    private List<ImageView> minesImageViews, mistakesImageViews;
    private KeyFrame keyFrame;
    private Timeline timeline;
    private int numOfMines, timerCount, revealedButtons, flaggedButtons;
    private boolean started, finished, win;
    private TreeSet<Coordinate> treeSet;
    private ArrayList<Coordinate> flaggedCoordinates;
    private final int[][] NEIGHBOR_BUTTONS = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};

    public GameBoard(int rows, int columns, int numOfMines) {
        super();
        this.numOfMines = numOfMines;
        this.revealedButtons = 0;
        this.flaggedButtons = 0;
        this.minesImageViews = new ArrayList<>();
        this.mistakesImageViews = new ArrayList<>();
        this.flaggedCoordinates = new ArrayList<>();

        this.treeSet = new TreeSet<>(new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate o1, Coordinate o2) {
                Coordinate c1 = (Coordinate) o1, c2 = (Coordinate) o2;
                if (c1.getiLocation() == c2.getiLocation() && c1.getjLocation() == c2.getjLocation()) return 0;
                else if (c1.getiLocation() == c2.getjLocation() && c1.getjLocation() == c2.getiLocation()) return -1;
                if (coordinateMeasure(c1) > coordinateMeasure(c2))
                    return 1;
                else return -1;
            }
        });

        this.flagsImageViews = new Stack<>();
        fillImageViewsStack();
        createGridPane(rows, columns);
        createTop(rows);
        this.started = false;
        this.finished = false;
        this.win = false;
        createEventHandler();
    }

    public void createTop(int rows) {
        createTimer();

        this.timerLabel = new Label(String.format("%04d", this.timerCount));
        this.timerLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTSTEELBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.timerLabel.setFont(Font.font("Segoe Print", rows * 2.5));
        this.timerLabel.setTextFill(Color.RED);

        this.imageView = new ImageView(new Image("Smile_Icon.jpg"));
        this.imageView.setFitWidth(rows * 4);
        this.imageView.setFitHeight(rows * 4);
        this.resetButton = new Button();
        this.resetButton.setGraphic(this.imageView);

        this.flagsLeftLabel = new Label(String.format("%3d", this.numOfMines));
        this.flagsLeftLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTSTEELBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.flagsLeftLabel.setFont(Font.font("Segoe Print", rows * 2.5));
        this.flagsLeftLabel.setTextFill(Color.RED);

        this.borderPane = new BorderPane();
        this.borderPane.setBackground(new Background(new BackgroundFill(Color.LIGHTSTEELBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.borderPane.setLeft(this.timerLabel);
        this.borderPane.setCenter(this.resetButton);
        this.borderPane.setRight(this.flagsLeftLabel);
        super.setTop(this.borderPane);

    }

    public void revelButtonValue(int i, int j, int value) {
        this.gameButtons[i][j].setText("" + value);
        this.gameButtons[i][j].setFont(Font.font("times", FontWeight.BOLD, 12));
        this.gameButtons[i][j].setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.gameButtons[i][j].setStyle("-fx-border-color: darkgray;");
        switch (value) {
            case 1:
                this.gameButtons[i][j].setTextFill(Color.BLUE);
                break;
            case 2:
                this.gameButtons[i][j].setTextFill(Color.GREEN);
                break;
            case 3:
                this.gameButtons[i][j].setTextFill(Color.RED);
                break;
            case 4:
                this.gameButtons[i][j].setTextFill(Color.DARKBLUE);
                break;
            case 5:
                this.gameButtons[i][j].setTextFill(Color.BEIGE.darker());
                break;
            case 6:
                this.gameButtons[i][j].setTextFill(Color.CYAN.brighter());
                break;
            case 7:
                this.gameButtons[i][j].setTextFill(Color.DARKGRAY);
                break;
            case 8:
                this.gameButtons[i][j].setTextFill(Color.GRAY);
                break;
            default:
                Platform.exit();
        }
    }

    public void revealButton(int i, int j) {
        if (inBounds(i, j) && this.revealedButtons < this.gameButtons.length * this.gameButtons[0].length - this.numOfMines) {
            if (!this.gameButtons[i][j].isFlagged() && !this.gameButtons[i][j].isClicked()) {
                this.gameButtons[i][j].setClicked(true);
                if (this.gameButtons[i][j].isMined()) {
                    loseMode(i, j);
                } else if (this.gameButtons[i][j].getNumOfNeighborMines() == 0) {
                    this.gameButtons[i][j].setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                    this.gameButtons[i][j].setStyle("-fx-border-color: darkgray;");
                    for (int index = 0; index < NEIGHBOR_BUTTONS.length; index++) {
                        revealButton(i + NEIGHBOR_BUTTONS[index][0], j + NEIGHBOR_BUTTONS[index][1]);
                    }
                } else {
                    revelButtonValue(i, j, this.gameButtons[i][j].getNumOfNeighborMines());
                }
                this.revealedButtons++;
                if (this.revealedButtons == this.gameButtons.length * this.gameButtons[0].length - this.numOfMines && !this.finished) {
                    winMode();
                }
            }
        }
    }

    public boolean inBounds(int i, int j) {
        return i >= 0 && i < this.gameButtons.length && j >= 0 && j < this.gameButtons[0].length;
    }

    public void gameButtonsEventHander() {
        for (int i = 0; i < this.gameButtons.length; i++) {
            for (int j = 0; j < this.gameButtons[i].length; j++) {
                int index = i, jindex = j;
                this.gameButtons[i][j].setOnMouseClicked(event -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        buttonClick(index, jindex);
                    } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                        setFlagOnButton(index, jindex);
                    }
                });
            }
        }
    }

    public void createEventHandler() {
        gameButtonsEventHander();
        this.resetButton.setOnMouseClicked(event -> {
            restart();
        });
    }

    public void setButtonsValue() {
        for (int i = 0; i < this.gameButtons.length; i++) {
            for (int j = 0; j < this.gameButtons[i].length; j++) {
                if (!this.gameButtons[i][j].isMined()) {
                    for (int k = 0; k < NEIGHBOR_BUTTONS.length; k++) {
                        if (inBounds(i + NEIGHBOR_BUTTONS[k][0], j + NEIGHBOR_BUTTONS[k][1])) {
                            if (this.gameButtons[i + NEIGHBOR_BUTTONS[k][0]][j + NEIGHBOR_BUTTONS[k][1]].isMined())
                                this.gameButtons[i][j].addNeighborMine();
                        }
                    }
                }
            }
        }
    }

    public void minesScatter(int i, int j) {
        while (this.treeSet.size() < this.numOfMines) {
            int index = (int) (Math.random() * this.gameButtons.length);
            int jindex = (int) (Math.random() * this.gameButtons[0].length);
            if (index != i || jindex != j) {
                this.treeSet.add(new Coordinate(index, jindex));
                this.gameButtons[index][jindex].setMined(true);
            }
        }
        setButtonsValue();
        revealButton(i, j);
    }

    public void buttonClick(int i, int j) {
        if (!this.gameButtons[i][j].isClicked() && !this.gameButtons[i][j].isFlagged()) {
            if (!this.started && !this.finished) {
                this.started = true;
                this.timeline.play();
                minesScatter(i, j);
            } else if (this.started && !this.finished) {
                revealButton(i, j);
            }
        }
    }

    public void setFlagOnButton(int i, int j) {
        if (!this.gameButtons[i][j].isClicked() && this.started && !this.finished) {
            if (!this.gameButtons[i][j].isFlagged() && this.flaggedButtons < this.numOfMines) {
                this.gameButtons[i][j].setFlagged(true);
                this.gameButtons[i][j].setGraphic(this.flagsImageViews.pop());
                this.gameButtons[i][j].setFlagIndex(this.flaggedButtons);
                this.flaggedCoordinates.add(new Coordinate(i, j));
                this.flaggedButtons++;
            } else if (this.gameButtons[i][j].isFlagged() && this.flaggedButtons >= 0) {
                this.gameButtons[i][j].setFlagged(false);
                this.flagsImageViews.push((ImageView) this.gameButtons[i][j].getGraphic());
                this.gameButtons[i][j].setGraphic(null);
                this.flaggedCoordinates.remove(new Coordinate(i, j));
                for (int index = 0; index < this.flaggedCoordinates.size(); index++) {
                    if (this.flaggedCoordinates.get(index).getiLocation() == i && this.flaggedCoordinates.get(index).getjLocation() == j) {
                        this.flaggedCoordinates.remove(index);
                        break;
                    }
                }
                this.flaggedButtons--;
            }
            this.flagsLeftLabel.setText(String.format("%02d", this.numOfMines - this.flaggedButtons));
        }
    }

    public void createTimer() {
        this.timerCount = 0;
        this.keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            if (this.started && !this.finished && this.timerCount < 10000) {
                this.timerCount++;
                this.timerLabel.setText(String.format("%04d", this.timerCount));
            }
        });

        this.timeline = new Timeline();
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.getKeyFrames().add(this.keyFrame);
    }

    public void createGridPane(int rows, int columns) {
        this.gridPane = new GridPane();
        this.gameButtons = new GameButton[rows][columns];
        for (int i = 0; i < this.gameButtons.length; i++) {
            for (int j = 0; j < this.gameButtons[i].length; j++) {
                this.gameButtons[i][j] = new GameButton();
                this.gridPane.add(this.gameButtons[i][j], j, i);
            }
        }
        super.setCenter(this.gridPane);
    }

    public void fillImageViewsStack(){
        while (this.flagsImageViews.size() < this.numOfMines){
            this.flagsImageViews.push(new ImageView(new Image("Flag_Icon.jpg")));
            this.flagsImageViews.peek().setFitWidth(12);
            this.flagsImageViews.peek().setFitHeight(12);
        }
    }

    public double coordinateMeasure(Coordinate coordinate) {
        return Math.sqrt(Math.pow(coordinate.getiLocation(), 2) + Math.pow(coordinate.getjLocation(), 2));
    }

    public void winMode() {
        this.timeline.stop();
        this.finished = true;
        this.win = true;
        this.imageView.setImage(new Image("Smoke_Icon.png"));
    }

    public void loseMode(int i, int j) {
        this.timeline.stop();
        this.finished = true;
        this.win = false;
        this.imageView.setImage(new Image("Lose_Icon.png"));
        Iterator<Coordinate> iterator = this.treeSet.iterator();
        while (iterator.hasNext()) {
            Coordinate tmp = iterator.next();
            if (tmp.getiLocation() != i || tmp.getjLocation() != j) {
                this.minesImageViews.add(new ImageView(new Image("Bomb_Icon.png")));
                this.minesImageViews.get(this.minesImageViews.size() - 1).setFitHeight(12);
                this.minesImageViews.get(this.minesImageViews.size() - 1).setFitWidth(12);
                this.gameButtons[tmp.getiLocation()][tmp.getjLocation()].setGraphic(this.minesImageViews.get(this.minesImageViews.size() - 1));
            } else {
                this.clickedMine = new ImageView(new Image("Devil_Icon.jpg"));
                this.clickedMine.setFitHeight(12);
                this.clickedMine.setFitWidth(12);
                this.gameButtons[tmp.getiLocation()][tmp.getjLocation()].setGraphic(this.clickedMine);
            }
        }
        for (int index = 0; index < this.flaggedCoordinates.size(); index++) {
            if(!this.gameButtons[this.flaggedCoordinates.get(index).getiLocation()][this.flaggedCoordinates.get(index).getjLocation()].isMined()){
                this.mistakesImageViews.add(new ImageView(new Image("Wrong_Flag_Icon.png")));
                this.mistakesImageViews.get( this.mistakesImageViews.size()-1).setFitWidth(12);
                this.mistakesImageViews.get(this.mistakesImageViews.size()-1).setFitHeight(12);
                this.gameButtons[this.flaggedCoordinates.get(index).getiLocation()][this.flaggedCoordinates.get(index).getjLocation()].setGraphic(   this.mistakesImageViews.get(this.mistakesImageViews.size()-1));
            }
        }
    }

    public void restart() {
        this.timerCount = 0;
        this.timerLabel.setText(String.format("%04d", this.timerCount));
        for (int i = 0; i < this.gameButtons.length; i++) {
            for (int j = 0; j < this.gameButtons[i].length; j++) {
                this.gridPane.getChildren().remove(this.gameButtons[i][j]);
                this.gameButtons[i][j] = null;
                this.gameButtons[i][j] = new GameButton();
                this.gridPane.add(this.gameButtons[i][j], j, i);
            }
        }
        gameButtonsEventHander();
        this.flaggedCoordinates.clear();
        this.minesImageViews.clear();
        this.mistakesImageViews.clear();
        this.treeSet.clear();
        this.flaggedButtons = 0;
        this.revealedButtons = 0;
        this.imageView.setImage(new Image("Smile_Icon.jpg"));
        this.started = false;
        this.finished = false;
        this.win = false;
        fillImageViewsStack();
    }
}
