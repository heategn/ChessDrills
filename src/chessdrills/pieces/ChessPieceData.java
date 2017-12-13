/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessdrills.pieces;

import java.util.HashMap;
import java.util.BitSet;
import chessdrills.pieces.Piece;
/**
 * Contains data information, such as what squares the piece can capture, and utility methods useful for gathering information at runtime.
 */
public class ChessPieceData {
    
    public int lightStartingPosition;
    public int darkStartingPosition;
    public int startingPosition;
    public HashMap<Integer, Piece.PieceColor> startingPositions = new HashMap();
    public String lightImage;
    public String darkImage;
    protected HashMap<Integer, BitSet> captureBoard = new HashMap();
    public Piece.PieceType type;
    
    public ChessPieceData(int boardSize, Piece.PieceType type){ 
        for(int i = 0; i < boardSize; i++){
            captureBoard.put(i, new BitSet(boardSize));
        }
        this.type = type;
    }
    
    public HashMap<Integer,BitSet> getCaptureBoard(){ 
        return new HashMap(captureBoard);
    }
    
    public HashMap<Integer,BitSet> getCaptureBoardRef(){
        return captureBoard;
    }
    
    public void setCaptureBoard(HashMap<Integer, BitSet> cb){
        this.captureBoard = cb;
    }
    
    public HashMap getBoard2(){
        throw new Error("getBoard2 not overridden");
    }
    
    public BitSet getCaptureSquares(int square){
        return (BitSet)captureBoard.get(square).clone();
    }
    
    public ChessPieceData(Piece.PieceType type){
        this.type = type;
    }
    
    private double[] getCoords(int square){
        double[] c = { square % 8, Math.floorDiv(square, 8) };
        return c;
    }
    
    private double getDistance(double[] c1, double[] c2){
        return Math.sqrt( Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2) );
    }
    
    public boolean checkForCollision(int currentSquare, int targetSquare, int targetPieceSquare){
        
        if(type == Piece.PieceType.KNIGHT_LIGHT || type == Piece.PieceType.KNIGHT_DARK){
            BitSet cb = captureBoard.get(currentSquare);
            int idx = 0;
            for(int i = cb.nextSetBit(idx); i >= 0; i = cb.nextSetBit(idx++)){
                if(i == targetPieceSquare){
                    return true;
                }
            }
            return false;
        }
        
        double[] current = getCoords(currentSquare);
        double[] target = getCoords(targetSquare);
        double[] other = getCoords(targetPieceSquare);
        double distanceT = getDistance(target,current);
        double distanceOther = getDistance(other,current);
        
        if(distanceT >= distanceOther){
            return true;
        }
        
        return false;
        
    }

    
}
