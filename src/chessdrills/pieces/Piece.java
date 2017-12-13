package chessdrills.pieces;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.HashMap;
import java.util.BitSet;
import java.util.ArrayList;
import chessdrills.pieces.ChessPieceFactory;

/**
 *  Represents the piece on the board. It's also a container for underlying data-classes governing it's behavior and provides utility methods for gathering runtime information.
 */
public class Piece extends ImageView{
    
    public static enum PieceColor {LIGHT, DARK};
    public static enum PieceType {PAWN_LIGHT, PAWN_DARK, ROOK_LIGHT,ROOK_DARK, KNIGHT_LIGHT, KNIGHT_DARK, BISHOP_LIGHT, BISHOP_DARK, QUEEN_LIGHT,QUEEN_DARK, KING_LIGHT, KING_DARK}; 
    
    private ImageView imgView;
    private String imagePath = "chessdrills/assets/";
    private Image img;
    private double imageWidth;
    private double imageHeight;
    private String sizeKey; //Used to cache position offsets
    public ChessPieceData pieceData;
    public PieceType type;
    private int startPosition = -1;
    
    public Piece(PieceType type){
        this(type, 50);
    }
    
    public int getStartingPosition(){
        return startPosition;
    }
    
    public Piece(PieceType type, int offset){
        
        pieceData = ChessPieceFactory.getData(type);
        
        String imgStr = imagePath.concat(pieceData.lightImage);
        this.type = type;
        startPosition = pieceData.lightStartingPosition + offset;
        
        if(type.toString().contains("DARK")){
            imgStr = imagePath.concat(pieceData.darkImage);
            startPosition = pieceData.darkStartingPosition + offset;
        }
        
        try{
            loadImage(imgStr);
        }
        catch(Exception e){
            System.out.println("Warning: Internal Graphics not initialized");
        }
        
        sizeKey = Double.toString(imageWidth).concat(Double.toString(imageHeight));
        
    }
   
    public HashMap getCaptureBoard(){
        return pieceData.getCaptureBoard();
    }
 
    
    public BitSet getCaptureSquares(int square){
        return pieceData.getCaptureSquares(square);
    }
    
    public ArrayList<Integer> getCaptureSquaresArr(int square){
        ArrayList<Integer> l = new ArrayList();
        BitSet s = getCaptureSquares(square);
        for(int i = s.nextSetBit(0); i >= 0; i = s.nextSetBit(i+1)){
            l.add(i);
        }
        return l;
    }
    
    public boolean isSquareEmptyAndNotAttacking(int square, BitSet board){
        BitSet captureBoardForSquare = (BitSet)getCaptureSquares(square).clone();
        captureBoardForSquare.and(board);
        if(captureBoardForSquare.cardinality() == 0){
            return true;
        }
        return false;
    }
    
    public int getNumberAttacking(int square, BitSet board){
        BitSet captureSquares = (BitSet)getCaptureSquares(square).clone();
        captureSquares.and(board);
        return captureSquares.cardinality();
    }
    
    public ArrayList<Integer> getPiecesAttacking(int square, BitSet board){
        BitSet captureSquares = (BitSet)getCaptureSquares(square).clone();
        captureSquares.and(board);
        return captureSquares.stream().collect(ArrayList<Integer>::new, (al,b) -> {
            if(captureSquares.get(b) == true)
                al.add(b);
        },ArrayList<Integer>::addAll);
    }
    
    public boolean checkForCollision(int currentSquare, int targetSquare, int targetPieceSquare){
        return pieceData.checkForCollision(currentSquare, targetSquare, targetPieceSquare);
    }

    public String getSizeKey(){
        return sizeKey;
    }

    protected void loadImage(String imageStr){
        try{
            this.setImage(new Image(imageStr));
        }
        catch(java.lang.IllegalArgumentException exception){
            System.out.println("Error loading: " + imageStr);
            throw exception;
        }
    }
    
}
