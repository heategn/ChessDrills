package org.chessdrills.pieces;

import java.util.BitSet;
import java.util.HashMap;
import org.chessdrills.pieces.ChessPieceData;
import org.chessdrills.pieces.Piece;

/**
 *
 * The bishop piece has two different boards, one for light-squares and dark-squares. The board (light or dark-squared) is set by the client at runtime.
 */
public class BishopData extends ChessPieceData{
    
    private HashMap<Integer, BitSet> captureBoard2 = new HashMap();
    private boolean lightSquares = true;
    
    public BishopData(int boardSize, Piece.PieceType type){
        super(boardSize, type);

        for(int i = 0; i < boardSize; i++){
            captureBoard2.put(i, new BitSet(boardSize));
        }

    }

    @Override 
    public HashMap<Integer,BitSet> getCaptureBoard(){ 
        if(lightSquares){
            return super.getCaptureBoard();
        }
        return new HashMap(captureBoard2);
    }

    public HashMap getCaptureBoardRef2(){
        return captureBoard2;
    }
    
    @Override 
    public BitSet getCaptureSquares(int square){
        BitSet b = null;
        if(lightSquares){ 
            b = super.captureBoard.get(square);
        }
        else{
            b = captureBoard2.get(square);
        }
        if(b != null){
            return (BitSet)b.clone();
        }
        return new BitSet();
    }
    
    public void setCaptureBoard2(HashMap<Integer,BitSet> cb2){
        this.captureBoard2 = cb2;
    }
    
    public void setLightSquares(boolean light){ 
        lightSquares = light;
    }
    
    public boolean isLightSquared(){
        return lightSquares;
    }
    
}
