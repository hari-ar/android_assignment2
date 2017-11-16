package assignment2.griffith.hari.assignment2;

import android.content.Context;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

import static java.util.Collections.min;
import static java.util.Collections.shuffle;

public class MainActivity extends AppCompatActivity {

    CellObject[][] cellObjectTable;
    MinesweeperView minesweeperView;
    Button modeButton;
    TextView minesMarked;
    private Paint[] textPaints; // For coloring texts, index indicate color position.
    private SensorManager sensorManager;
    private Sensor sensor;
    private ShakeListener shakeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize and get values
        minesweeperView =  findViewById(R.id.minesweeper_view_object);
        modeButton =  findViewById(R.id.mode_button);
        minesMarked = findViewById(R.id.number_of_mines_marked);
        //Init Paints to set
        initPaints();
        initGameBoard();

        minesweeperView.setCellObjectTable(cellObjectTable);

        //Custom Event listener for mines marked count change called from Minesweeper View and this is only implementation
        minesweeperView.setIOnMineCountChangeEventListener(new MinesweeperView.IOnMineCountChangeEventListener() {
            @Override
            public void onMinesMarkedCountChange(int count) {
                minesMarked.setText(getString(R.string.mineTextDefault) + count); //Set to edit text to let users know..!!
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeListener = new ShakeListener();
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {

            @Override
            public void onShake() {
                Toast.makeText(MainActivity.this, "Device Shaken, Resetting the Game", Toast.LENGTH_SHORT).show();
                reset();
            }
        });

    }


    //Initialize game board with given size
    private void initGameBoard() {
        cellObjectTable = new CellObject[10][10];

        //Initializing Array Lists of Cells
        for (int row = 0; row < cellObjectTable.length; row++) {
            for (int col = 0; col < cellObjectTable[row].length; col++) {
                cellObjectTable[row][col] = new CellObject();
            }
        }
        //Set mines -- Whats the fun without mines ..!
        plantMines();
        //Initialize the cells with their colors here itself.. Coz.. Why not..!!!
        setMineCountsTextColorAndCellBackground();
    }

    private void setMineCountsTextColorAndCellBackground() {

        for (int row = 0; row < cellObjectTable.length; row++) { //Iterate through rows
            for (int col = 0; col < cellObjectTable[row].length; col++) { // Iterate through columns
                int mineCount = calculateMineCounts(row, col); //Get mine count
                cellObjectTable[row][col].setMineCount(mineCount); //Set mine count
                cellObjectTable[row][col].setCellTextColor(textPaints[mineCount + 1]); //Set text color of the cell
                cellObjectTable[row][col].setMineString(String.valueOf(mineCount)); // Set string of the cell
                if (mineCount > -1) {
                    cellObjectTable[row][col].setCellBackground(textPaints[1]); //Setting Silver Background
                }
                else {
                    cellObjectTable[row][col].setMineString(String.valueOf("M"));//Setting M for mine..
                    cellObjectTable[row][col].setCellBackground(textPaints[5]); //Setting Red background for mine
                }
            }
        }
    }

    private void initPaints() { //Just colors are initialized to keep everything simpler, mineCount+1 matches to index
        textPaints = new Paint[10];
        for (int index = 0; index < textPaints.length - 1; index++) {
            textPaints[index] = new Paint(); // Setting new Paint
            if (index > 4) {
                textPaints[index].setColor(getResources().getColor(R.color.red));// Setting red as color for 4 or more bombs.
            }
            textPaints[index].setTextSize(50); // Setting text size as 50
        }
        textPaints[0].setColor(getResources().getColor(R.color.black)); //Makes M color as Black
        textPaints[1].setColor(getResources().getColor(R.color.silver)); //Makes 0 invisible., A crazy hack avoid tons of if conditions..!!
        textPaints[2].setColor(getResources().getColor(R.color.blue)); // Text 1 should be Blue. Text - 1 gives the index.
        textPaints[3].setColor(getResources().getColor(R.color.green));// Text 2 should be Green.
        textPaints[4].setColor(getResources().getColor(R.color.yellow));// Text 3 should be Yellow.
    }

    //Method to calculate mine counts
    private int calculateMineCounts(int row, int col) {
        int mineCount = 0; //Initialize with zero
        if (cellObjectTable[row][col].isMine()) { //If mine, return -1, can be anything negative..!!
            return -1;
        }
        for (int i = -1; i < 2; i++) //Horizontal Neighbours
        {
            for (int j = -1; j < 2; j++) //Vertical Neighbors
            {
                if (row + i < 0 || row + i >= 10 || col + j < 0 || col + j >= 10) // Checking corner case for IndexOutOfBounds
                {
                    continue;
                }
                if (cellObjectTable[row + i][col + j].isMine()) {
                    mineCount++; // MineCount increment
                }
            }
        }
        return mineCount;
    }


    private void plantMines() {
        Random random = new Random(); //For random number generation
        int count = 0; // To keep track of number of mines..
        while (count < 20) { // Mines have to be 20..!! Else fail in assignment..!!
            int row = random.nextInt(10); //Bound by 10 , max number is 10
            int col = random.nextInt(10); //Bound by 10 , max number  is 10
            if (!cellObjectTable[row][col].isMine()) {
                cellObjectTable[row][col].setMine(true); //Power up Mines
                //cellObjectTable[row][col].setFlagged(true);//Enabled for testing
                //System.out.println("Row is " + row + " and Col is " + col);
                count++;
            }
        }

    }

    //Change mode button listener
    public void changeMode(View view) {
        changeMode();
    }
    
    private void changeMode(){
        HashMap<Boolean, String> textMap = new HashMap<>(); //Cute little thing to avoid if-else, not sure if this is costier or if-else is..!!!
        textMap.put(true, "Go to Marking Mode");
        textMap.put(false, "Go to Uncover Mode");
        minesweeperView.setUncoverModeSet(!minesweeperView.isUncoverModeSet()); //Set mode opposite to current mode
        modeButton.setText(textMap.get(minesweeperView.isUncoverModeSet())); // Get currently set mode and display text based on mode
    }


    public void resetGame(View view) {
        reset();
    }
    
    private void reset(){
        initGameBoard(); //Must init board on every reset
        minesweeperView.setCellObjectTable(cellObjectTable); //Set New Object
        minesweeperView.setGameOverFlag(false); // Reset game over flag
        minesweeperView.setMinesMarkedCount(0); // Reset mines counter
        minesweeperView.setUnCoveredCount(0); //Reset uncovered mines counter
        minesweeperView.setUncoverModeSet(false); // To start with unCoverMode
        changeMode();// To start with unCoverMode
        minesweeperView.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        sensorManager.registerListener(shakeListener, sensor,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        sensorManager.unregisterListener(shakeListener);
        super.onPause();
    }


}
