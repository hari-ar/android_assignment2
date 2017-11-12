package assignment2.griffith.hari.assignment2;

/**
 * Created by aahuyarakshakaharil on 10/11/17.
 */

public class CellObject {

    private boolean isMine = false;
    private boolean isCovered = true;
    private boolean isFlagged;
    private int mineCount;

    public boolean isMine() {
        return isMine;
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
