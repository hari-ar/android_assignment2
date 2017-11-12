package assignment2.griffith.hari.assignment2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aahuyarakshakaharil on 09/11/17.
 */

public class MinesweeperView extends View {

    private Paint black, white, silver, red; //Colors for background and line colors.
    private Paint[] textPaints; // For coloring texts, index indicate color position.


    public boolean isUncoverModeSet() {
        return uncoverModeSet;
    }

    public void setUncoverModeSet(boolean uncoverModeSet) {
        this.uncoverModeSet = uncoverModeSet;
    }

    private boolean uncoverModeSet = true; // Default to uncover Mode

    private TextView minesLeftTextView ;
    private int minesMarkedCount;
    private float multiplier;

    public void setCellObjectTable(CellObject[][] cellObjectTable) {
        this.cellObjectTable = cellObjectTable;
    }

    CellObject[][] cellObjectTable;


    public MinesweeperView(Context context) {
        super(context);
        init(context,null);
    }




    public MinesweeperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public MinesweeperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    private void init(Context context, @Nullable AttributeSet attrs) { //Common init method.
        black = new Paint();
        black.setColor(getResources().getColor(R.color.black));
        white = new Paint();
        white.setColor(getResources().getColor(R.color.white));
        silver = new Paint();
        silver.setColor(getResources().getColor(R.color.silver));
        red = new Paint();
        red.setColor(getResources().getColor(R.color.red));
        textPaints = new Paint[10];

        for(int index = 0; index<textPaints.length-1;index++)
        {
            textPaints[index] = new Paint();
            if(index>4)
            {
                textPaints[index].setColor(getResources().getColor(R.color.red));// Setting red as color for 4 or more bombs.
            }
            textPaints[index].setTextSize(50);
        }
        textPaints[0].setColor(getResources().getColor(R.color.black)); //Makes 0 invisible
        textPaints[1].setColor(getResources().getColor(R.color.silver)); //Makes 0 invisible
        textPaints[2].setColor(getResources().getColor(R.color.blue)); // Text 1 should be Blue. Text - 1 gives the index.
        textPaints[3].setColor(getResources().getColor(R.color.green));// Text 2 should be Green.
        textPaints[4].setColor(getResources().getColor(R.color.yellow));// Text 3 should be Yellow.
        white.setAntiAlias(true);

        minesLeftTextView = (TextView) findViewById(R.id.number_of_mines_marked);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        if(height<width)
            width = height;
        multiplier = width/11;
        drawInitialSquares(multiplier,canvas);
        drawTextOnSquares(multiplier,canvas);
    }

    private void drawTextOnSquares(float multiplier, Canvas canvas) {

        for(int row = 0; row<cellObjectTable.length;row++){
            for(int col = 0 ; col < cellObjectTable[row].length;col++){
                if(!cellObjectTable[row][col].isCovered())//Check if cell is uncovered...
                {
                    if(!(cellObjectTable[row][col].isFlagged()) || (cellObjectTable[row][col].isCovered()) ){
                        drawCellText(row,col ,cellObjectTable[row][col].getMineCount(),canvas,multiplier); //Print the data
                    }
                }
            }
        }
    }

    private void drawCellText(int row, int col, int mineCount, Canvas canvas, float multiplier) { // Used to draw Text
        //multiplier = multiplier*2;
        int left = (int) (row * multiplier) + 2;
        int top = (int) ((col * multiplier) + 2);
        int right = (int) (((row+1) * multiplier) - 2);
        int bottom =  (int) (((col+1) * multiplier) - 2);
        Paint paint = textPaints[mineCount+1];
        if(mineCount>-1) {

            drawText(left,top,right,bottom,paint,canvas,multiplier,row,col,String.valueOf(mineCount),silver);


        }
        else{
            // the display area.
            drawText(left,top,right,bottom,paint,canvas,multiplier,row,col,"M",red);

        }
    }

    private void drawText(int left, int top, int right, int bottom, Paint paint, Canvas canvas, float multiplier, int row, int col, String input, Paint textColor){
        Rect tempRect = new Rect(left, top, right, bottom);
        RectF textRectF = new RectF(tempRect);
        // measure text width
        textRectF.right = paint.measureText(input, 0, 1);
        // measure text height
        textRectF.bottom = paint.descent() - paint.ascent();
        textRectF.left += (tempRect.width() - textRectF.right) / 2;
        textRectF.top += (tempRect.height() - textRectF.bottom) / 2;
        canvas.drawRect(row*multiplier+2f,col*multiplier+2f,(row+1)*multiplier-2f,(col+1)*multiplier-2f,textColor);
        canvas.drawText(input, textRectF.left, textRectF.top - paint.ascent(), paint);
    }

    private void drawInitialSquares(float multiplier, Canvas canvas) {

        for(int i =0;i<=10;i++) {
            canvas.drawLine(2,i*multiplier,10*multiplier,i*multiplier,white); // Horizontal Lines
            canvas.drawLine(i*multiplier,2,i*multiplier,10*multiplier,white); // Vertical Lines
        }
        for(int i=0; i<10;i++)
        {
            for(int j=0 ; j<10; j++){
                canvas.drawRect(i*multiplier+2f,j*multiplier+2f,(i+1)*multiplier-2f,(j+1)*multiplier-2f,black); //Render Rectangles
            }
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Setting square dimension.. Requirement and followed logic as per pdf
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        if(height<width)
            width = height;
        setMeasuredDimension((int)width,(int) width); // Responsible for rendering square mines.

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int row = (int) (x/multiplier);
        int col = (int) (y/multiplier);
        System.out.println("Data is x "+x+" and y is "+y);
        System.out.println("Data is x "+row+" and y is "+col);
        cellObjectTable[row][col].setCovered(false);

        if(isUncoverModeSet()){// Check Mode

        }
        // Check if flagged.
        // Check if its Mine
        invalidate();
        return super.onTouchEvent(event);

    }
}
