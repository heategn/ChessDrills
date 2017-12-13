package chessdrills.pieces;

import java.util.ArrayList;
import java.util.BitSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;

public class ChessPieceDataTest {
    
    public ChessPieceDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
//
//    @Test
//    public void testCheckForCollisionCollision() {
//        
//        System.out.println("checkForCollision");
//        int currentSquare = 24;
//        int targetSquare = 0;
//        int targetPieceSquare = 9;
//        ArrayList<Piece.PieceType> types = new ArrayList();
//        types.add(Piece.PieceType.BISHOP_LIGHT);
//        types.add(Piece.PieceType.QUEEN_LIGHT);
//        types.add(Piece.PieceType.ROOK_LIGHT);
//        
//        Piece instance;
//        boolean result;
//        
//        for(Piece.PieceType t : types){
//            System.out.println(t);
//            instance = ChessPieceFactory.get(t);
//            result = instance.checkForCollision(24, 0, 9);
//            assertEquals(true, result);
//        }
//        
//        System.out.println(Piece.PieceType.KNIGHT_LIGHT);
//        instance = ChessPieceFactory.get(Piece.PieceType.KNIGHT_LIGHT);
//        result = instance.checkForCollision(0, 17, 17);
//        assertEquals(true, result);
//        
//    }
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
    
    @Test
    public void testBishopBoards(){
        Piece p = new Piece(Piece.PieceType.BISHOP_LIGHT);
        BishopData bd = (BishopData)p.pieceData;
        bd.setLightSquares(false);
        HashMap<Integer, BitSet> hm = p.getCaptureBoard();
        hm.forEach((k,v)->{
            print(v);
        });
    }
    
  @Test
    public void testCheckForNoCollision() {
        
        System.out.println("checkForNoCollision");
        
        int currentSquare = 0;
        int targetSquare = 9;
        int targetPieceSquare = 24;
        ArrayList<Piece.PieceType> types = new ArrayList();
        types.add(Piece.PieceType.QUEEN_LIGHT);
        types.add(Piece.PieceType.ROOK_LIGHT);
        types.add(Piece.PieceType.BISHOP_DARK);
        
        Piece instance = null;
        boolean result;
        
        for(Piece.PieceType t : types){
            System.out.println(t);
//            instance = ChessPieceFactory.getData(t);
            result = instance.checkForCollision(currentSquare, targetSquare, targetPieceSquare);
            assertEquals(false, result);
        }
        
        HashMap<Integer,BitSet> hm = instance.getCaptureBoard();
        print(hm.get(0));
        
        System.out.println(Piece.PieceType.KNIGHT_LIGHT);
        result =  instance.checkForCollision(0, 17, 9);
        assertEquals(false, result);
        
    }
}