package org.chessdrills_test.pieces;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import org.chessdrills.pieces.Piece;
import org.chessdrills.pieces.BishopData;
import org.chessdrills.pieces.ChessPieceData;
import org.chessdrills.pieces.ChessPieceFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChessPieceDataTest {
    
    public ChessPieceDataTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }

    //@Test (Depreciated)
    public void testCheckForCollision() {
        
        int currentSquare = 24;
        int targetSquare = 0;
        int targetPieceSquare = 9;
        ArrayList<Piece.PieceType> types = new ArrayList();
        types.add(Piece.PieceType.BISHOP_LIGHT);
        types.add(Piece.PieceType.QUEEN_LIGHT);
        types.add(Piece.PieceType.ROOK_LIGHT);
        
        ChessPieceData instance;
        boolean result;
        
        for(Piece.PieceType t : types){
            instance = ChessPieceFactory.getData(t);
            result = instance.checkForCollision(24, 0, 9);
            assertEquals(true, result);
        }
        
        instance = ChessPieceFactory.getData(Piece.PieceType.KNIGHT_LIGHT);
        result = instance.checkForCollision(0, 17, 17);
        assertEquals(true, result);
        
    }

    private void print(BitSet data){
        for(int i = 0; i < ChessPieceFactory.boardSize; i++){
            int offset = i % ChessPieceFactory.columnSize;
            if(offset == 0){
                System.out.println("");
            }
            if(data.get(i)){
                System.out.print("0");
            }
            else{
                System.out.print("-");
            }
        }
    }
    
    public void testBishopBoards(){
        Piece p = new Piece(Piece.PieceType.BISHOP_LIGHT);
        BishopData bd = (BishopData)p.pieceData;
        bd.setLightSquares(false);
        HashMap<Integer, BitSet> hm = p.getCaptureBoard();
        hm.forEach((k,v)->{
            print(v);
        });
    }
    
		//@Test (Depreciated)
    public void testCheckForNoCollision() {
        
        int currentSquare = 0;
        int targetSquare = 9;
        int targetPieceSquare = 24;
        ArrayList<Piece.PieceType> types = new ArrayList();
        types.add(Piece.PieceType.QUEEN_LIGHT);
        types.add(Piece.PieceType.ROOK_LIGHT);
        types.add(Piece.PieceType.BISHOP_DARK);
        
        ChessPieceData instance = null;
        boolean result;
        
        for(Piece.PieceType t : types){
          	instance = ChessPieceFactory.getData(t);
            result = instance.checkForCollision(currentSquare, targetSquare, targetPieceSquare);
            assertEquals(false, result, "Collision found. " + t + " Current Square: " + currentSquare + " Target Square: " + targetSquare + " TargetPieceSquare" + targetPieceSquare);
        }
        
    }

}
