/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chessdrills.pieces;

import java.util.HashMap;
import java.util.BitSet;
import java.util.Arrays;
import javafx.scene.image.Image;
import org.chessdrills.pieces.Piece;
import org.chessdrills.util.Util;

/**
 * Contains data information, such as what squares the piece can capture, and utility methods useful for gathering information at runtime.
 */
public class ChessPieceData {
    
    public int lightStartingPosition;
    public int darkStartingPosition;
    public int startingPosition;
    public HashMap<Integer, Piece.PieceColor> startingPositions = new HashMap();
    public Image lightImage;
    public Image darkImage;
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

		//TODO: DEPRECIATED. Moved to Util.java
		private double[] getCoords(int square){
        double[] c = { square % 8, Math.floorDiv(square, 8) };
        return c;
    }

		//TODO: DEPRECIATED. Moved to Util.java
    private double getDistance(double[] c1, double[] c2){
        return Math.sqrt( Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2) );
    }

		/**
		* For Bishops, rooks, and queens
		*/
		public boolean isAttackable(int dx, int dy){

			boolean onDiagnol = Math.abs(dx) == Math.abs(dy);
			boolean onLine = (dx == 0 || dy == 0);
			boolean isBishop = (type == Piece.PieceType.BISHOP_LIGHT || type == Piece.PieceType.BISHOP_DARK);
			boolean isRook = (type == Piece.PieceType.ROOK_LIGHT || type == Piece.PieceType.ROOK_DARK);

			if(!onDiagnol && !onLine){
				return false;
			}

			if(!onDiagnol && isBishop || !onLine && isRook){
				return false;
			}

			return true;

		}

    public boolean isAttacking(int currentSquare, int targetSquare){
			return isAttacking(currentSquare,targetSquare,new BitSet(64));	
		}

		public boolean isAttacking(int currentSquare, int targetSquare, BitSet currentBoard){

//		int currentCol = currentSquare % 8;
//		int currentRow = (int)Math.floor(currentSquare / 8);
//		int targetCol = targetSquare % 8;
//		int targetRow = (int)Math.floor(targetSquare / 8);

			int[] currentCoords = Util.getCoords(currentSquare);
			int[] targetCoords = Util.getCoords(targetSquare);
			int dx = targetCoords[0] - currentCoords[0];
			int dy = targetCoords[1] - currentCoords[1];

			if(type == Piece.PieceType.KNIGHT_LIGHT || type == Piece.PieceType.KNIGHT_DARK){
				if(Math.abs(dy) != 0 && Math.abs(dx) != 0 && Math.abs(dy) <= 2){
					if(	Math.abs(dx) == 1 && Math.abs(dy) == 2 ||
							Math.abs(dx) == 2 && Math.abs(dy) == 1){
						return true;
					}
				}
				return false;
			}

			if(!isAttackable(dx,dy)){
				return false;
			}

			//Check for obstacles
			if(currentBoard.cardinality() > 0){
				int n_dx = Util.normalize(dx);
				int n_dy = Util.normalize(dy);

				for(int p : currentBoard.stream().toArray()){
					if(p == currentSquare || p == targetSquare){
						continue;
					}

					int[] lcoords = Util.getCoords(p);
					int dx2 = lcoords[0] - currentCoords[0];
					int dy2 = lcoords[1] - currentCoords[1];

					if(!isAttackable(dx2,dy2)){
						continue;
					}

					int n_dx2 = Util.normalize(dx2);
					int n_dy2 = Util.normalize(dy2);

					if(n_dx == n_dx2 && n_dy == n_dy2 && (Math.abs(dx2) < Math.abs(dx) || Math.abs(dy2) < Math.abs(dy))){
						return false;
					}

				}

			}

			return true;

		}

		/**
		 * Depreciated.
		 * This is useful if the pieces are already known to be along the same line.
		 * Use "isAttacking" or "Util.isRayClear" instead.
		 **/
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

        int[] current = Util.getCoords(currentSquare);
        int[] target = Util.getCoords(targetSquare);
        int[] other = Util.getCoords(targetPieceSquare);
        int distanceT = Util.getDistance(target,current);
        int distanceOther = Util.getDistance(other,current);
        
        if(distanceT >= distanceOther){
            return true;
        }
        
        return false;
        
    }

    
}
