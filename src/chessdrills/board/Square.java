package chessdrills.board;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class Square extends Rectangle{
 
    public final int squareNumber;
    private final Color color;
    
    Square(int squareNumber, double width, double height, Color color){
        super(width, height, color);
        this.color = color;
        this.squareNumber = squareNumber;
    }
    
    public void highlight(){
        setFill(Color.YELLOW);
    }
    
    public void unHighlight(){
        setFill(this.color);
    }
    
}
