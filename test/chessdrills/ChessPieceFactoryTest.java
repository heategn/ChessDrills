package chessdrills;

import chessdrills.pieces.ChessPieceFactory;
import chessdrills.pieces.ChessPieceData;
import chessdrills.pieces.Piece;
import java.util.BitSet;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;

public class ChessPieceFactoryTest {
    
    public ChessPieceFactoryTest() {
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

    private void printOut(BitSet data){
//            System.out.println(data.captureBoard.size());
        for(int i = 0; i < ChessPieceFactory.boardSize; i++){
//          int col = Math.floorDiv( i, 16 );
            int offset = i % ChessPieceFactory.columnSize;
            if(offset == 0 && i > 0){
//                System.out.print("  " + i);
                System.out.println();
            }
            
//                System.out.print(" " + i + " ");
            if(data.get(i)){
                System.out.print("0");
            }
            else{
                System.out.print("-");
            }
        }

    }
    
    /**
     * Visually check the bitboard of a piece.
     **/
    @Test
    public void testGet() {
        ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.BISHOP_DARK);
//        ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.BISHOP_LIGHT);
//        ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.ROOK_LIGHT);
//        ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.KNIGHT_LIGHT);
//        ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.QUEEN_LIGHT);
//        BishopData result = (BishopData) ChessPieceFactory.get(Piece.PieceType.BISHOP);
//        HashMap<Integer, BitSet> board = result.getBoard2();
        HashMap <Integer, BitSet> hm = pieceData.getCaptureBoard();
        for(Map.Entry<Integer, BitSet> e : hm.entrySet()){
            printOut(e.getValue());
            System.out.println();
            System.out.println();
            System.out.println();
        }

    }
    
}