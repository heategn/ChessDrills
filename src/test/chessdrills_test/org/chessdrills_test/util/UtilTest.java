package org.chessdrills_test.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Collections;
import java.time.LocalTime;
import java.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.chessdrills.util.Util;
import org.chessdrills.pieces.Piece;
import org.chessdrills.board.Setup;

public class UtilTest {
    
    public UtilTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }

		//@Test
		public void testInit(){
			Util.init();
			System.out.println(Util.getSquare(1,0));
			System.out.println(Util.getSquare(1,1));
		}

		//@Test
		public void timeCheck_getDistance(){

			ArrayList<double[]> set1 = new ArrayList();
			for(int i = 0; i < 8; i++){
				for(int k = 0; k < 8; k++){
					double[] s = {i,k};
					set1.add(s);
				}
			}

			ArrayList<double[]> set2 = new ArrayList(set1);

			ArrayList<Integer> set1b = new ArrayList();
			ArrayList<Integer> set2b = new ArrayList();
			for(int i = 0; i < 64; i++){
				set1b.add(i);
				set2b.add(i);
			}	


			int total = 0;
			for(int t2 = 0; t2 < 1000; t2++){
				Collections.shuffle(set1b);
				Collections.shuffle(set2b);
				int currentTime = LocalTime.now().getNano();
				for(int i = 0; i < 64; i++){
					Util.getDistance(set1b.get(i),set2b.get(i));
				}
				total += LocalTime.now().getNano() - currentTime;
			}

			System.out.println(total/1000);
				
		}

   	//@Test
    public void isRayClear_test() {

			BitSet board = new BitSet(64);
			board.set(19,true);
			board.set(18,true);
			board.set(27,true);
			board.set(2,true);
			assertEquals(false,Util.isRayClear(26,12,board));
			assertEquals(false,Util.isRayClear(26,28,board));
			assertEquals(false,Util.isRayClear(26,10,board));
			board.set(19,false);	
			board.set(18,false);	
			board.set(27,false);	
			assertEquals(true,Util.isRayClear(26,12,board));
			assertEquals(true,Util.isRayClear(26,28,board));
			assertEquals(true,Util.isRayClear(26,42,board));
			board.set(34,true);	
			assertEquals(false,Util.isRayClear(26,42,board));

		}

		//@Test
		public void getClosest_test(){
			int[] squares = {19,25,26};
			assertEquals(26,Util.getClosest(27, 24, 26));
			assertEquals(9,Util.getClosest(27, 0, 9));
		}
		
		//@Test
		public void getClosestSquareOnLine_test(){
			int[] squares = {19,25,26};
			int[] squares2 = {20,13,6};
			BitSet bs = new BitSet(64);
			bs.set(19,true);
			bs.set(25,true);
			bs.set(26,true);
			BitSet bs2 = new BitSet(64);
			bs2.set(20,true);
			bs2.set(13,true);
			bs2.set(6,true);
			assertAll("",
				() -> assertEquals(26, Util.getClosestSquareOnLine(27, 24, bs)),
				() -> assertEquals(20, Util.getClosestSquareOnLine(27, 6, bs2)));
		}

	 //@Test
		public void vector_test(){
			int[] r = Util.vector(7,63);	
			assertEquals(r[0],0);
			assertEquals(r[1],7);
			r = Util.vector(63,7);	
			assertEquals(r[0],0);
			assertEquals(r[1],-7);
			r = Util.vector(63,0);	
			assertEquals(r[0],-7);
			assertEquals(r[1],-7);
			r = Util.vector_normal(63,0);	
			assertEquals(r[0],-1);
			assertEquals(r[1],-1);
		}

		//@Test
		public void getPiecesOnLine_test(){
			BitSet bs = new BitSet(64);
			bs.set(3,true);
			bs.set(9,true);
			ArrayList<Integer> on_line = Util.getPiecesOnLine(11,8,bs);
			assertEquals(9,on_line.get(0));
			on_line = Util.getPiecesOnLine(63,27,bs);
			assertEquals(true,on_line.contains(3));
			assertEquals(true,on_line.contains(9));

		}

		//@Test
		public void getSquaresAttackedBy(){

			Piece knight = new Piece(Piece.PieceType.KNIGHT_DARK);
			Piece bishop = new Piece(Piece.PieceType.BISHOP_DARK);
			Piece rook = new Piece(Piece.PieceType.ROOK_DARK);
			Piece queen = new Piece(Piece.PieceType.QUEEN_DARK);
			BitSet bs = new BitSet(64);
			BitSet bs2 = new BitSet(64);
			int[] set = {10,12,20,13,19};

			for(int i : set){
				bs.set(i,true);
			}
		
			ArrayList<Integer> a1 = Util.getSquaresAttackedBy(27,knight,bs);
			ArrayList<Integer> a2 = Util.getSquaresAttackedBy(27,bishop,bs);
			ArrayList<Integer> a3 = Util.getSquaresAttackedBy(27,rook,bs);
			ArrayList<Integer> a4 = Util.getSquaresAttackedBy(27,queen,bs);

			assertAll("",
			() -> assertEquals(2,a1.size()),
			() -> assertEquals(1,a2.size()),
			() -> assertEquals(1,a3.size()),
			() -> assertEquals(2,a4.size()));

		HashMap<Integer, Piece> board = new HashMap();
		Piece q =	new Piece(Piece.PieceType.KNIGHT_DARK);
		board.put(57, q);
		Setup s = new Setup(board);
			
		}

		//**
		//* Print the board in the terminal.
		//**
    private void printOut(BitSet data, int piece){

			for(int i = 0; i < 64; i++){

				int offset = i % 8;
				if(offset == 0 && i > 0){
					System.out.println();
				}

				if(i == piece){
					System.out.print(" O ");
					continue;
				}

				if(data.get(i)){
					System.out.print(" X ");
				}
				else{
					System.out.print(" - ");
				}

			}

    }

		//@Test
		public void numAttackingThisSquare_test2(){

			BitSet targetPieceBoard = new BitSet(64);
			targetPieceBoard.set(29, true);

			HashMap<Integer, Piece> board = new HashMap();
			board.put(57, new Piece(Piece.PieceType.QUEEN_DARK));
			board.put(29, new Piece(Piece.PieceType.BISHOP_LIGHT));
			board.put(45, new Piece(Piece.PieceType.ROOK_LIGHT));
			board.put(18, new Piece(Piece.PieceType.ROOK_LIGHT));
			board.put(9, new Piece(Piece.PieceType.KNIGHT_LIGHT));
			board.put(43, new Piece(Piece.PieceType.QUEEN_LIGHT));
			Setup setupBoard = new Setup(board); 	
			ArrayList<Piece> pl = Util.getPiecesAttackingSquare(targetPieceBoard,setupBoard);
			assertEquals(2,pl.size());


		}

		//@Test
		public void piecesAttackingThisSquare_test(){

			HashMap<Integer, Piece> board = new HashMap();

//		board.put(29, new Piece(Piece.PieceType.ROOK_LIGHT));
//    board.put(46, new Piece(Piece.PieceType.KNIGHT_LIGHT));
//		board.put(34, new Piece(Piece.PieceType.BISHOP_LIGHT));
//		board.put(41, new Piece(Piece.PieceType.ROOK_LIGHT));
			board.put(13, new Piece(Piece.PieceType.QUEEN_LIGHT));
			board.put(43, new Piece(Piece.PieceType.ROOK_LIGHT));
//		board.put(50, new Piece(Piece.PieceType.BISHOP_LIGHT));
			Setup setupBoard = new Setup(board); 	
			BitSet bs = new BitSet(64);
			bs.set(34,true);
			ArrayList<Piece> al = Util.getPiecesAttackingSquare(bs,setupBoard);

//		System.out.println(al.size());

		}

		//@Test
		public void numAttackingThisSquare_test(){

			HashMap<Integer, Piece> board = new HashMap();

			BitSet targetPieceBoard = new BitSet(64);
			targetPieceBoard.set(27, true);

			board.put(26, new Piece(Piece.PieceType.ROOK_LIGHT));
			board.put(24, new Piece(Piece.PieceType.ROOK_LIGHT));
			Setup setupBoard = new Setup(board); 	
			assertEquals(1, Util.numAttackingThisSquare(targetPieceBoard,setupBoard));

			board.put(28, new Piece(Piece.PieceType.ROOK_LIGHT));
			setupBoard.newSetup(board); 	
			assertEquals(2, Util.numAttackingThisSquare(targetPieceBoard,setupBoard));

			board.put(43, new Piece(Piece.PieceType.ROOK_LIGHT));
			setupBoard.newSetup(board); 	
			assertEquals(3, Util.numAttackingThisSquare(targetPieceBoard, setupBoard));

			board.put(44, new Piece(Piece.PieceType.KNIGHT_LIGHT));
			setupBoard.newSetup(board); 	
			assertEquals(4, Util.numAttackingThisSquare(targetPieceBoard, setupBoard));

			board.put(27, new Piece(Piece.PieceType.KNIGHT_LIGHT));
			setupBoard.newSetup(board); 	
			assertEquals(4, Util.numAttackingThisSquare(targetPieceBoard, setupBoard));

			board.clear();
			board.put(11, new Piece(Piece.PieceType.BISHOP_DARK));
			board.put(3, new Piece(Piece.PieceType.QUEEN_DARK));
			setupBoard.newSetup(board);
			assertEquals(0, Util.numAttackingThisSquare(targetPieceBoard, setupBoard));

		}

}    
