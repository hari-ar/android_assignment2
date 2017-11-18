package assignment2.griffith.hari.assignment2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by aahuyarakshakaharil on 09/11/17.
 */

public class MinesweeperView extends View {

    private Paint black, white, yellow; //Colors for background and line colors.

    private IOnMineCountChangeEventListener iOnMineCountChangeEventListener;
    private int numberOfMarkedCells, cellsUncoveredByUser = 0;
    private float multiplier;
    private final float borderOffset = 2f; // This is required to show background white color
    CellObject[][] cellObjectTable;
    private boolean uncoverModeSet = true; // Default to uncover Mode
    private boolean gameOverFlag = false;
    private Canvas canvas;
    private RectF square;
    int rowDown =-1, columnDown =-1, rowUp =-1,columnUp =-1;


    //Constructors Block Start..!!!
    public MinesweeperView(Context context) {
        super(context);
        init();
    }

    public MinesweeperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MinesweeperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //Constructors Block Ends..!!!


    //Common Init Method..!!
    private void init() {
        //Common init method.
        //Initialize Paints
        black = new Paint();
        white = new Paint();
        yellow = new Paint();
        //Setting respective Colors
        black.setColor(getResources().getColor(R.color.black));
        white.setColor(getResources().getColor(R.color.white));
        yellow.setColor(getResources().getColor(R.color.yellow));
        //Setting Anti Aliasing Flags..!!
        white.setAntiAlias(true);
        black.setAntiAlias(true);
        yellow.setAntiAlias(true);
        //Setting Anti Aliasing Flag Ends..!!
    }

    @Override
    protected void onDraw(Canvas canvas) { //Called on invalidate()
        super.onDraw(canvas); //Calling super..!!
        this.canvas = canvas;

        float width = getMeasuredWidth(); //Get Width
        float height = getMeasuredHeight(); //Get Height
        if (height < width) //Checking least values of height and weight
        {
            multiplier = height / 10;//Setting with least of the values
        } else {
            multiplier = width / 10; // Set Multiplier to adjust to make it perfect square
        }
        System.out.println("Multiplier is "+multiplier);

        //Initialize cell square.
        if(square==null) //Avoids unnecessary initialization
        {
            square = new RectF(borderOffset -multiplier/2, borderOffset -multiplier/2,multiplier/2- borderOffset,multiplier/2- borderOffset);
        }

        drawGameBoard(); // Draw Background

        if(!gameOverFlag) {
            if (getCellsUncoveredByUser() == 40) //Half way there.. If 40 squares are opened., player is half way there..!! Check for game over flag.
            {
                Toast.makeText(getContext(), "Great..!!! You're Half way there..!! Keep rocking..", Toast.LENGTH_LONG).show();//Toast showing game ended
            }
            if (getCellsUncoveredByUser() == 80) //If 80 squares are opened., player wins..!!
            {
                gameOverFlag = true;
                Toast.makeText(getContext(), "Awesome..!!! You Win.. Click on Reset to try again", Toast.LENGTH_LONG).show();//Toast showing game ended
                invalidate();
            }
        }
    }
    private void drawGameBoard() {
        for (int index = 0; index <= 10; index++) {
            canvas.drawLine(borderOffset, index * multiplier, 10 * multiplier, index * multiplier, white); // Horizontal Lines
            canvas.drawLine(index * multiplier, borderOffset, index * multiplier, 10 * multiplier, white); // Vertical Lines
        }
        for (int row = 0; row < cellObjectTable.length; row++) { //Iterate through rows
            for (int col = 0; col < cellObjectTable[row].length; col++) { // Iterate Columns
                canvas.save();

                //Can move this to cell object as well.. to be cleaner.
                canvas.translate((2*row+1)*multiplier/2,(2*col+1)*multiplier/2); //Finding centers of squares to move the origin.

                if (!cellObjectTable[row][col].isFlagged()) //Not draw if flagged
                {
                    if(cellObjectTable[row][col].isCovered()) //Draw black rectangle for covered cells.
                    {
                        canvas.drawRect(square, black); //Render Rectangles
                    }
                    else{ //To draw text
                        canvas.drawRect(square,cellObjectTable[row][col].getCellBackground()); //Get Cell Background color
                        Paint textPaint = cellObjectTable[row][col].getCellTextColor(); //Getting text color and setting it to paint
                        textPaint.setTextSize(multiplier/2); //Set text size, which modifies based on device
                        String input = cellObjectTable[row][col].getMineString(); //Getting input text to be printed
                        canvas.drawText(input, 0, multiplier/4, textPaint); //Adjust text to be in center., only vertically is needed because we use align center in main activity
                        //drawTextInsideTheCell(row, col); //Print the data
                    }
                }
                else
                {
                    canvas.drawRect(square, yellow); //Render Yellow Rectangles for marked squares
                }
                canvas.restore();
            }
        }
    }

    
    private void unCoverAllMines() {
        for (CellObject[] rowsOfTable : cellObjectTable) { // Iterate through rows
            for (CellObject cell : rowsOfTable) { // Iterate through columns
                if (cell.isMine()) {
                    cell.setFlagged(false); //Unflag all mines to enable it to be displayed
                    cell.setCovered(false); // Uncover all mines to enable it to be displayed
                }
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Setting square dimension.. Requirement and followed logic as per pdf
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        if (height < width) {
            setMeasuredDimension((int) height, (int) height); // Responsible for rendering square mines.
        } else {
            setMeasuredDimension((int) width, (int) width); // Responsible for rendering square mines.
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Get user touch input co-ordinates

        if(event.getActionMasked() == MotionEvent.ACTION_DOWN) { //Get touch down co-ordinates
            float x = event.getX();
            float y = event.getY();
            System.out.println("x is "+x+" and y is "+y);
            //Convert user touch co-ordinates to out cell map
             rowDown = (int) (x / multiplier);
            columnDown = (int) (y / multiplier);
            return true;
        }

        if(event.getActionMasked() == MotionEvent.ACTION_UP) {//Get touch up co-ordinates
            float x = event.getX();
            float y = event.getY();
            //Convert user touch co-ordinates to out cell map
             rowUp = (int) (x / multiplier);
            columnUp = (int) (y / multiplier);
        }

        if(rowDown == rowUp && columnDown == columnUp){ //Ignore Swipes by checking if touch down and touch up are same cells..!!
            int row = rowUp; int column = columnUp;
            rowDown = -1;columnDown = -1; columnUp = -1; columnDown = -1; //Reset to not get false values
            if (!isGameOverFlag()) //Invalidate all clicks after game over
            {
                if (!isUncoverModeSet()) // Check Mode..!!
                {
                    if (cellObjectTable[row][column].isCovered()) { //Checking if cell is covered
                        if (cellObjectTable[row][column].isFlagged()) { //Checking if cell is flagged, unflag if flagged and vice versa
                            cellObjectTable[row][column].setFlagged(false); //Unset Flag and reduce Marked count
                            setNumberOfMarkedCells(getNumberOfMarkedCells() - 1); //Reduce marked count
                        } else { //This will be called in marking mode if flag was not set already
                            cellObjectTable[row][column].setFlagged(true); //Set Flag
                            setNumberOfMarkedCells(getNumberOfMarkedCells() + 1); //Increase marking count
                        }
                    }
                } else { // Uncover Mode
                    if (!cellObjectTable[row][column].isFlagged() && cellObjectTable[row][column].isCovered() )  // Check if flagged. and if uncovered.. without that will increment counter if clicked on uncovered cell..!!
                    {
                        if (!cellObjectTable[row][column].isMine()) { //Check if user stepped on mine..!! Oops
                            if (cellObjectTable[row][column].getMineCount() == 0) //If not mine check for count., if zero, we've to appreciate user
                            {
                                uncoverZerosAndCascadeCells(row, column); // Appreciating by opening adjacent 0 tiles, till it reach a non zero tile
                            }
                            else {
                                cellObjectTable[row][column].setCovered(false); //If not mine, uncover the cell
                                setCellsUncoveredByUser(getCellsUncoveredByUser() + 1); //Increase the uncovered count to track the game progress
                            }
                        } else { //If User Clicked on Mine
                            setGameOverFlag(true);//Sets game is over..!!
                            unCoverAllMines();
                            Toast.makeText(getContext(), "OOPS..!! Better Luck Next Time..!!! Click Reset for new game..", Toast.LENGTH_SHORT).show();
                            //Uncover Mines
                        }
                    }
                }
                invalidate(); //Calls onDraw Again forcefully... Why Android not do this automatically...?????
            }
        }
        return super.onTouchEvent(event);
    }

    //Using DFS to uncover 0's
    private void uncoverZerosAndCascadeCells(int row, int col) {
        //Same loop logic as the get mine count in Main Activity.. Getting all 9 neighbor value
        for (int rowIndex = -1; rowIndex < 2; rowIndex++) //Horizontal Neighbours
        {
            for (int columnIndex = -1; columnIndex < 2; columnIndex++) //Vertical Neighbors
            {
                if (row + rowIndex < 0 || row + rowIndex >= 10 || col + columnIndex < 0 || col + columnIndex >= 10) // Checking corner case for IndexOutOfBounds
                {
                    continue;
                }
                if (cellObjectTable[row + rowIndex][col + columnIndex].isCovered()) { //Check if cell is already covered..

                    if (cellObjectTable[row + rowIndex][col + columnIndex].isFlagged()) { //Remove flagged cells and reduce flag count
                        cellObjectTable[row + rowIndex][col + columnIndex].setFlagged(false);
                        numberOfMarkedCells--;
                    }
                    cellObjectTable[row + rowIndex][col + columnIndex].setCovered(false); //Uncover mine
                    setCellsUncoveredByUser(getCellsUncoveredByUser() + 1); // Increase uncovered count.. Must not miss track of this
                    if (cellObjectTable[row + rowIndex][col + columnIndex].getMineCount() == 0) { //Check if neighbor cell also have zero
                        uncoverZerosAndCascadeCells(row + rowIndex, col + columnIndex); // Opening cells by DFS..
                    }
                }
            }
        }
    }

    // Getters and Setters Start.....!!
    public int getCellsUncoveredByUser() {
        return cellsUncoveredByUser;
    }

    public void setCellsUncoveredByUser(int cellsUncoveredByUser) {
        this.cellsUncoveredByUser = cellsUncoveredByUser;
    }

    public boolean isGameOverFlag() {
        return gameOverFlag;
    }

    public void setGameOverFlag(boolean gameOverFlag) {
        this.gameOverFlag = gameOverFlag;
    }

    public boolean isUncoverModeSet() {
        return uncoverModeSet;
    }

    public void setUncoverModeSet(boolean uncoverModeSet) {
        this.uncoverModeSet = uncoverModeSet;
    }

    public int getNumberOfMarkedCells() {
        return numberOfMarkedCells;
    }

    public void setNumberOfMarkedCells(int numberOfMarkedCells) {
        IOnMineCountChangeEventListener iOnMineCountChangeEventListener = this.iOnMineCountChangeEventListener;
        this.numberOfMarkedCells = numberOfMarkedCells;
        iOnMineCountChangeEventListener.onMinesMarkedCountChange(numberOfMarkedCells); //Call this to notify user of a change..!!!
    }

    public void setCellObjectTable(CellObject[][] cellObjectTable) {
        this.cellObjectTable = cellObjectTable;
    }
    // Getters and Setters End.....!!


    //Custom listener will be implemented in MainActivity so it gets called every time there is change in count
    public interface IOnMineCountChangeEventListener {
        void onMinesMarkedCountChange(int count);
    }

    //Method to be called to get listener event
    public void setIOnMineCountChangeEventListener(IOnMineCountChangeEventListener iOnMineCountChangeEventListener) {
        this.iOnMineCountChangeEventListener = iOnMineCountChangeEventListener;
    }
}
