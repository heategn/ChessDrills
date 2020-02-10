package org.chessdrills_test.setup;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.Exception;
import java.util.HashMap;
import java.util.BitSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.chessdrills.pieces.Piece;
import org.chessdrills.board.Setup;
import org.chessdrills.util.Util;

public class SetupTest{
    
  	public SetupTest(){} 

		//**
		//* Print the board in the terminal.
		//**
    private void printOut(BitSet data){
			printOut(data, -1);
		}

    private void printOut(BitSet data, int piece){

			for(int i = 0; i < 64; i++){

				int offset = i % 8;
				if(offset == 0 && i > 0){
					System.out.println();
				}

				if(piece != -1 && i == piece){
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
		public void getPieces_test(){
			HashMap<Integer,Piece> hm = new HashMap();
			Piece p = new Piece(Piece.PieceType.ROOK_LIGHT);
			hm.put(27,p);
			Setup s = new Setup(hm);
			List<Piece> pieces = s.getPieces();
		}

		//@Test 
		public void getProtectedBitSet_test(){

			HashMap<Integer,Piece> hm = new HashMap();
			hm.put(27,new Piece(Piece.PieceType.ROOK_LIGHT));
			hm.put(32,new Piece(Piece.PieceType.ROOK_LIGHT));	
			hm.put(34,new Piece(Piece.PieceType.BISHOP_DARK));
			Setup s = new Setup(hm);
			BitSet covered = s.getProtectedBitSet();
			printOut(covered,-1);
			covered.flip(0,63);
			System.out.println("");
			System.out.println("");
			System.out.println("=====");
			System.out.println("");
			printOut(covered,-1);
		}

		//@Test
		public void recalculate_test(){

			HashMap<Integer,Piece> hm = new HashMap();
			//hm.put(40,new Piece(Piece.PieceType.BISHOP_LIGHT));
			//hm.put(27,new Piece(Piece.PieceType.ROOK_LIGHT));	
			//hm.put(39,new Piece(Piece.PieceType.KNIGHT_LIGHT));
			hm.put(28,new Piece(Piece.PieceType.BISHOP_LIGHT));
			//hm.put(31,new Piece(Piece.PieceType.ROOK_LIGHT));
			//hm.put(24,new Piece(Piece.PieceType.QUEEN_LIGHT));
			Setup s = new Setup(hm);
			int[] covered = s.getProtected();
			BitSet coveredbs = s.getProtectedBitSet();
			int[] covered2 = coveredbs.stream().toArray();
			System.out.println(Arrays.toString(covered));
			printOut(coveredbs,28);
		}

		//@Test
		public void setupClassTest(){

			HashMap<Integer,Piece> hm = new HashMap();
			Piece p = new Piece(Piece.PieceType.ROOK_LIGHT);
			hm.put(27,p);
			hm.put(32,new Piece(Piece.PieceType.ROOK_LIGHT));	
			hm.put(34,new Piece(Piece.PieceType.BISHOP_DARK));
			Setup s = new Setup(hm);
			BitSet covered = s.getProtectedBitSet();
			int[] i = s.getProtected();
			//System.out.println(Arrays.toString(i));
			ArrayList<Piece> pieces = s.getPieces();
			System.out.println(pieces.size());
			s.removePiece(p);
			System.out.println(pieces.size());
			s.addPiece(10,p);
			System.out.println(pieces.size());
			Piece[][] brd = s.getBoard();
			int[] coords = Util.getCoords(10);
			System.out.println(brd[coords[0]][coords[1]]);

	}
}
