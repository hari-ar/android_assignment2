package assignment2.griffith.hari.assignment2;

import android.graphics.Paint;

/**
 * Created by aahuyarakshakaharil on 10/11/17.
 */

public class CellObject {

    private boolean isFlagged,isMine = false;
    private boolean isCovered = true;
    private int mineCount;
    private Paint cellBackground,cellTextColor;
    private String mineString;

    public boolean isMine() {
        return isMine;
    }

    public Paint getCellBackground() {
        return cellBackground;
    }

    public void setCellBackground(Paint cellBackground) {
        this.cellBackground = cellBackground;
    }

    public Paint getCellTextColor() {
        return cellTextColor;
    }

    public void setCellTextColor(Paint cellTextColor) {
        this.cellTextColor = cellTextColor;
    }

    public String getMineString() {
        return mineString;
    }

    public void setMineString(String mineString) {
        this.mineString = mineString;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }




}
