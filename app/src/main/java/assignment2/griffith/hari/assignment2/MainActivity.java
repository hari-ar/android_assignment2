package assignment2.griffith.hari.assignment2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;

import static java.util.Collections.min;
import static java.util.Collections.shuffle;

public class MainActivity extends AppCompatActivity {

    CellObject[][] cellObjectTable;
    MinesweeperView minesweeperView;
    Button modeButton;
    TextView minesMarked;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        minesweeperView = (MinesweeperView) findViewById(R.id.minesweeper_view_object);
        modeButton = (Button) findViewById(R.id.mode_button);
        minesMarked = (TextView) findViewById(R.id.number_of_mines_marked);
        init(minesweeperView);
        minesweeperView.setCellObjectTable(cellObjectTable);

        minesweeperView.setIOnMineCountChangeEventListener(new MinesweeperView.IOnMineCountChangeEventListener() {
            @Override
            public void onMinesMarkedCountChange(int count) {
                minesMarked.setText("Marked Mines : "+count);
            }
        });

    }

    private void init(MinesweeperView minesweeperView) {
        cellObjectTable = new CellObject[10][10];

        //Initializing Array Lists of Cells
        for(int row =0 ; row<10; row++){
            for(int col =0 ; col<10; col++){
                cellObjectTable[row][col] = new CellObject();
            }
        }
        plantMines();
        setMineCounts();
    }

    private void setMineCounts() {
        for(int row =0 ; row<10; row++){
            for(int col =0 ; col<10; col++){
                cellObjectTable[row][col].setMineCount(calculateMineCounts(row,col));
            }
        }
    }

    private int calculateMineCounts(int row,int col) {
        int mineCount = 0;
        if(cellObjectTable[row][col].isMine())
        {
            return -1;
        }
        for(int i = -1; i < 2; i++) //Horizontal Neighbours
        {
            for(int j = -1; j < 2; j++) //Vertical Neighbors
            {
                if(row+i < 0 || row+i >= 10 || col+j < 0 || col+j >= 10) // Checking corner case for IndexOutOfBounds
                {
                    continue;
                }
                if(cellObjectTable[row+i][col+j].isMine())
                {
                    mineCount++;
                }
            }
        }
        return mineCount;
    }


    private void plantMines() {
        Random random = new Random();
        int count =0;
        while (count<20){
            int row = random.nextInt(10);
            int col = random.nextInt(10);
            if(!cellObjectTable[row][col].isMine()){
                cellObjectTable[row][col].setMine(true);
                //cellObjectTable[row][col].setCovered(false);Enabled for testing
                System.out.println("Row is "+row+" and Col is "+col);
                count++;
            }
        }

    }


    public void changeMode(View view) {
        HashMap<Boolean,String> textMap = new HashMap<>();
        textMap.put(true,"Go to Marking Mode");
        textMap.put(false,"Go to Uncover Mode");
        minesweeperView.setUncoverModeSet(!minesweeperView.isUncoverModeSet());
        modeButton.setText(textMap.get(minesweeperView.isUncoverModeSet()));
    }




    public void resetGame(View view) {
        init(minesweeperView);
        minesweeperView.setCellObjectTable(cellObjectTable);
        minesweeperView.setGameOverFlag(false);
        minesweeperView.setMinesMarkedCount(0);
        minesweeperView.setUnCoveredCount(0);
        minesweeperView.setUncoverModeSet(false); // To start with unCoverMode
        changeMode(view);// To start with unCoverMode
        minesweeperView.invalidate();

    }
}
