package sample;

import javafx.scene.control.Button;

public class GameButton extends Button {
    private boolean mined, flagged, clicked;
    private int numOfNeighborMines, flagIndex;

    public GameButton() {
        super();
        super.setPrefHeight(30);
        super.setPrefWidth(30);
        this.mined = false;
        this.flagged = false;
        this.clicked = false;
        this.numOfNeighborMines = 0;
    }

    public boolean isClicked() {
        return this.clicked;
    }

    public boolean isFlagged() {
        return this.flagged;
    }

    public boolean isMined() {
        return this.mined;
    }

    public int getNumOfNeighborMines() {
        return this.numOfNeighborMines;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public void setMined(boolean mined) {
        this.mined = mined;
    }

    public void setFlagIndex(int flagIndex) {
        this.flagIndex = flagIndex;
    }

    public void addNeighborMine() {
        this.numOfNeighborMines++;
    }
}
