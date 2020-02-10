package org.chessdrills_test;

import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.chessdrills.pieces.ChessPieceFactory;
import org.chessdrills.pieces.ChessPieceData;
import org.chessdrills.pieces.Piece;
import org.chessdrills.util.Util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;


public class ChessPieceFactoryTest {
    
    public ChessPieceFactoryTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    private void printOut(BitSet data){
//      System.out.println(data.captureBoard.size());
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
		//@Test
    public void testGet() {
//      ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.BISHOP_DARK);
      ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.BISHOP_LIGHT);
 //       ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.ROOK_LIGHT);
//      ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.KNIGHT_LIGHT);
//      ChessPieceData pieceData = ChessPieceFactory.getData(Piece.PieceType.QUEEN_LIGHT);
//      BishopData result = (BishopData) ChessPieceFactory.get(Piece.PieceType.BISHOP);
//      HashMap<Integer, BitSet> board = result.getBoard2();
        HashMap <Integer, BitSet> hm = pieceData.getCaptureBoard();
        for(Map.Entry<Integer, BitSet> e : hm.entrySet()){
					printOut(e.getValue());
					System.out.println();
					System.out.println();
					System.out.println();
        }

    }

  	//@Test
	 public void isAttacking_testKnight(){
		 	Piece p = new Piece(Piece.PieceType.KNIGHT_LIGHT);
			for(int i = 0; i < 64; i++){
				int x = i % 8;
				int y = (int)Math.floor(i/8);	

				for(int k = 0; k < 64; k++){
					int dx = x - ( k % 8);
					int dy = y - ((int)Math.floor(k/8));	

					if(Math.abs(dx) == 1 && Math.abs(dy) == 2 || Math.abs(dx) == 2 &&  Math.abs(dy) == 1){
						assertEquals(true,p.pieceData.isAttacking(i,k));
					}
					else{
						assertEquals(false,p.pieceData.isAttacking(i,k));
					}
				}
			}

	 }

	 //@Test
	 public void isAttacking_test(){
		 	Piece bishop = new Piece(Piece.PieceType.BISHOP_LIGHT);
		 	Piece queen = new Piece(Piece.PieceType.QUEEN_LIGHT);
		 	Piece rook = new Piece(Piece.PieceType.ROOK_LIGHT);

    	Random rnd = new Random();
			int randomTest = 0;
			BitSet randomObstacle = new BitSet();

			for(int i = 0; i < 64; i++){
				int x = i % 8;
				int y = (int)Math.floor(i/8);	

				for(int k = 0; k < 64; k++){
					if(k == i){
						continue;
					}
					int dx = x - ( k % 8);
					int dy = y - ((int)Math.floor(k/8));	
					int n_dx = Util.normalize(dx);
					int n_dy = Util.normalize(dy);
					boolean diagnol = (Math.abs(dx) == Math.abs(dy));
					boolean line = (dx == 0 && dy != 0 || dx != 0 && dy == 0);

					randomObstacle.clear();
					int random = rnd.nextInt(64);
					randomObstacle.set(random,true);
					int rx = x - (random % 8);
					int ry = y - ((int)Math.floor(random/8));	
					int n_rx = Util.normalize(rx);
					int n_ry = Util.normalize(ry);

					if(diagnol){
						assertEquals(true,bishop.pieceData.isAttacking(i,k));
						assertEquals(true,queen.pieceData.isAttacking(i,k));
						if((n_rx == n_dx && n_ry == n_dy) && Math.abs(rx) < Math.abs(dx) && random != i){
							assertEquals(false,bishop.pieceData.isAttacking(i,k,randomObstacle));
							assertEquals(false,queen.pieceData.isAttacking(i,k,randomObstacle));
						}
					}

					if(line){
						assertEquals(true,queen.pieceData.isAttacking(i,k));
						assertEquals(true,rook.pieceData.isAttacking(i,k));
						if((n_rx == n_dx && n_ry == n_dy) && (Math.abs(rx) < Math.abs(dx) || Math.abs(ry) < Math.abs(dy)) && random != i){
							assertEquals(false,rook.pieceData.isAttacking(i,k,randomObstacle));
							assertEquals(false,queen.pieceData.isAttacking(i,k,randomObstacle));
						}
					}

					if(!diagnol){
						assertEquals(false,bishop.pieceData.isAttacking(i,k));
					}

					if(!line){
						assertEquals(false,rook.pieceData.isAttacking(i,k));
					}

					if(!diagnol && !line){
						assertEquals(false,rook.pieceData.isAttacking(i,k));
						assertEquals(false,bishop.pieceData.isAttacking(i,k));
						assertEquals(false,queen.pieceData.isAttacking(i,k));
					}
				}
			}
	 }
	 
}
