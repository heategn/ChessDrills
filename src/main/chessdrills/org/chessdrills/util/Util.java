package org.chessdrills.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.BitSet;
import java.util.stream.IntStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;

import org.chessdrills.pieces.Piece;
import org.chessdrills.board.Board;
import org.chessdrills.board.Setup;

public class Util{

	private static HashMap<Integer,int[]> sq2Coords = new HashMap();
	private static int[][] coords2Sq = new int[8][8];

	private static Point2D orig = new Point2D(1,0);
	private static boolean initiated = false;

	public static void init(){
		if(initiated){
			return;
		}

		for(int r = 0; r < 8; r++){
			for(int c = 0; c < 8; c++){
				int sq = r * 8 + c;
				int[] coords = {c,r}; 
				sq2Coords.put(sq,coords);
				coords2Sq[c][r] = sq;
			}
		}
		initiated = true;
	}

	public static List<Integer> shuffle(int[] ints){
		List<Integer> list = Arrays.stream(ints).boxed().collect(Collectors.toList());
		Collections.shuffle(list);
		return list;
	}

	public static int[] getInts(BitSet bs){
		return bs.stream().toArray();
	}

	public static Point2D getPointFromSquare(int sq){
		int[] pnt = getCoords(sq);
		return new Point2D(pnt[0],pnt[1]);
	}

	public static int[] getCoords(int sq){
		init();
		return sq2Coords.get(sq);
	}

	public static int getSquare(int x, int y){
		init();
		return coords2Sq[x][y];
	}

	public static int[][] getIntBoard(BitSet board){

		int[] squares = getInts(board);
		int[][] intBoard = new int[8][8];

		for(int i : squares){
			int[] coords = getCoords(i);
			intBoard[(int)coords[0]][(int)coords[1]] = 1;
		}

		return intBoard;

	}

	/**
	* Takes a BitSet of the piece positions, then checks the ray between the two given squares
	* Similar function: ChessPieceData.isAttacking(int, int, BitSet)
	*/
	public static boolean isRayClear(int currentSquare, int targetSquare, BitSet board){
		int[] s = board.stream().toArray();
		return isRayClear(currentSquare,targetSquare,s);
	}

	public static boolean isRayClear(int squareFrom, int squareTo, int[] occupied){

		Point2D pnt = getPointFromSquare(squareFrom);
		Point2D pnt2 = getPointFromSquare(squareTo);	

		double dx = pnt2.getX() - pnt.getX();
		double dy = pnt2.getY() - pnt.getY();
		dx = (dx != 0) ? dx/Math.abs(dx) : 0;
		dy = (dy != 0) ? dy/Math.abs(dy) : 0;

		if(dx != 0 && dy != 0 && Math.abs(dy/dx) != 1 ){
			throw new Error("Slope is not diagnol, horizontal, nor vertical");
		}

		double closest = (Math.abs(dx) > Math.abs(dy)) ? Math.abs(dx) : Math.abs(dy);

		for(int i : occupied){

			if(i == squareFrom || i == squareTo){
				continue;
			}

			Point2D p = getPointFromSquare(i);
			double dx2 = pnt.getX() - p.getX();
			double dy2 = pnt.getY() - p.getY();
			dx2 = (dx2 != 0) ? dx2/Math.abs(dx2) : 0;
			dy2 = (dy2 != 0) ? dy2/Math.abs(dy2) : 0;

			if(dx == dx2 && dy == dy2){
				double dist = (Math.abs(dx2) > Math.abs(dy2)) ? Math.abs(dx2) : Math.abs(dy2);
				if(dist < closest){
					closest = dist;
					continue;
				}
				return false;
			}

		}

		return true;	

	}

	public static int normalize(int d){
		return (d != 0) ? d / Math.abs(d) : 0;
	}

	/**
	* Returns a list of squares being attacked by the piece. 
	*/
	public static ArrayList<Integer> getSquaresAttackedBy(int pieceSquare, Piece p, BitSet occupied){

		BitSet captureBoard = p.getCaptureSquares(pieceSquare);
		captureBoard.set(pieceSquare, false);
		captureBoard.and(occupied);
		ArrayList<Integer> attacked = new ArrayList();
		int[] aAttacking = captureBoard.stream().toArray();
		int[] coords1 = Util.getCoords(pieceSquare);
			
		if(!isKnight(p) && aAttacking.length > 0){

			int[][] angles = new int[3][3]; 
			int[][] distances = new int[3][3]; 

			for(int a = 0; a < 3; a++){
				Arrays.fill(angles[a],-1);
				Arrays.fill(distances[a],-1);
			}

			//Eliminate multiple "hits" on the same row/column.
			for(int sq : aAttacking){

				if(sq == pieceSquare){
					continue;
				}

				int[] coords2 = Util.getCoords(sq);
				int dx = coords2[0] - coords1[0];
				int dy = coords2[1] - coords1[1];	
				int dist = (Math.abs(dx) > Math.abs(dy)) ? Math.abs(dx) : Math.abs(dy);

				//Get normalized vector and add 1 to avoid negative indexes. So {-1,1}, representing a vector to the upper-left, will becomes {0,2}.
				dx = (dx != 0) ? dx/Math.abs(dx) : 0;
				dy = (dy != 0) ? dy/Math.abs(dy) : 0;
				dx++;
				dy++;

				if(angles[dx][dy] < 0){
					angles[dx][dy] = sq;
					distances[dx][dy] = dist;
				}
				else if(distances[dx][dy] > dist){
					angles[dx][dy] = sq;
					distances[dx][dy] = dist;
				}
						
			}

			for(int i = 0; i < angles.length; i++){
				for(int k = 0; k < angles[i].length; k++){
					if(angles[i][k] >= 0){
						attacked.add(angles[i][k]);
					}
				}
			}

		}
		else{
			for(int i : aAttacking){
				attacked.add(i);
			}
		}
			
		return attacked;

	}


	/**
	* Returns how many pieces are attacking the given square -- colorblind.
	*/
  public static int numAttackingThisSquare(int targetSquare, Setup board){
		BitSet bs = new BitSet(64);
		bs.set(targetSquare,true);
		ArrayList<Piece> attacking = getPiecesAttackingSquare(bs,board);
		return attacking.size();
	}

  public static int numAttackingThisSquare(BitSet targetPieceBoard, Setup board){
		ArrayList<Piece> attacking = getPiecesAttackingSquare(targetPieceBoard,board);
		return attacking.size();
	}

	public static ArrayList<Piece> getPiecesAttackingSquare(int sq, Setup board){
		BitSet bs = new BitSet(64);
		bs.set(sq,true);
		return getPiecesAttackingSquare(bs,board);
	}

	public static ArrayList<Piece> getPiecesAttackingSquare(BitSet targetPieceBoard, Setup board){

		int targetSquare = targetPieceBoard.stream().toArray()[0];

		ArrayList<Piece> attacking = new ArrayList();

		for(Map.Entry<Integer,Piece> m : board.getPieceMap().entrySet()){

			int square = m.getKey();

			//Avoid double counting the target piece.
			if(square == targetSquare){
				continue;
			}

			Piece p = m.getValue();	
			if(p.isAttacking(square, targetSquare, board)){
				attacking.add(p);
			}
	
		}

    return attacking;

  }

  public static int getDistance(int[] c1, int[] c2){

		int x = Math.abs(c1[0] - c2[0]);
		return (x > 0) ? x : Math.abs(c1[1] - c2[1]);

  }

	public static int getDistance(int s1, int s2){

		if(s1 == s2){
			return 0;
		}

		int[] p1 = getCoords(s1);
		int[] p2 = getCoords(s2);
		return getDistance(p1,p2);

 	}

	/**
	* Return the closest square to the targetSquare
	*/
	public static int getClosest(int targetSquare, int sq1, int sq2){
		return (getDistance(targetSquare,sq1) < getDistance(targetSquare,sq2)) ? sq1 : sq2;
	}

	public static int[] vector_normal(int s1, int s2){

		int[] v = vector(s1, s2);
		int dx = (v[0] != 0) ? v[0] / Math.abs(v[0]) : 0;
		int dy = (v[1] != 0) ? v[1] / Math.abs(v[1]) : 0;
		int[] r = {dx,dy};
		return r;

	}

	public static int[] vector(int s1, int s2){

		int[] c1 = getCoords(s1);
		int[] c2 = getCoords(s2);
		int dx = c2[0] - c1[0];
		int dy = c2[1] - c1[1];
		int[] r = {dx,dy};
		return r;

	}

	/**
	* Determines if there is another piece along the same infinite line defined by the points.
	*/
	public static ArrayList<Integer> getPiecesOnLine(int squareFrom, int squareTo, BitSet occupied){
		return getPiecesOnLine(squareFrom, squareTo, occupied.stream().toArray());
	}
	
	public static ArrayList<Integer> getPiecesOnLine(int squareFrom, int squareTo, int[] occupied){

		int[] c1;
		int[] c2;
		int[] v1 = vector_normal(squareFrom, squareTo);
		ArrayList<Integer> onLine = new ArrayList();	

		for(int i : occupied){

			int[] v2 = vector_normal(squareFrom,i);

			if(v1[0] == v2[0] && v1[1] == v2[1]){
				onLine.add(i);
			}

		}

		return onLine;

	}

	/**
	* Get angle using square1 as the origin.
	*/
	public static double getAngle(int square1, int square2){
		return getAngle(getPointFromSquare(square1),getPointFromSquare(square2));	
	}

	public static double getAngle(Point2D p1, Point2D p2){
		Point2D p2_l = p2.subtract(p1);
		return p2_l.normalize().angle(1,0);
	}

	public static int pointToSquare(Point2D p){

		double squareSize = Board.getSquareSize();	

		int x = (int)Math.floor(p.getX() / squareSize);
		int y = (int)Math.floor(p.getY() / squareSize);

		return y * 8 + x;

	}

	/**
	* Get closest occupied square along a line between square1 and square2.
	*/
	public static int getClosestSquareOnLine(int square1, int square2, BitSet occupied){
		
		int[] squares = occupied.stream().toArray();
		double angle = getAngle(square1,square2);

		int dist = getDistance(square1,square2);
		int closest = square2;

		for(int i = 0; i < squares.length; i++){
			int sq = squares[i];
			if(getAngle(square1,sq) != angle){
				continue;
			}

			int d = getDistance(square1,sq);	
			if(d < dist){
				dist = d;
				closest = sq;
			}
		}

		return closest;

	}

	public static boolean isBishop(Piece p){
		return (p.type == Piece.PieceType.BISHOP_DARK || p.type == Piece.PieceType.BISHOP_LIGHT);
	}

	public static boolean isKnight(Piece p){
		return (p.type == Piece.PieceType.KNIGHT_DARK || p.type == Piece.PieceType.KNIGHT_LIGHT);
	}

	public static boolean isRook(Piece p){
		return (p.type == Piece.PieceType.ROOK_DARK || p.type == Piece.PieceType.ROOK_LIGHT);
	}

	public static boolean isQueen(Piece p){
		return (p.type == Piece.PieceType.QUEEN_DARK || p.type == Piece.PieceType.QUEEN_LIGHT);
	}

	public static BitSet getCoveredSquares(Piece p, int square, BitSet occupied){
		return getCoveredSquares(p,square,occupied,new BitSet(64));	
	}

	/**
	* Marks the empty squares this piece is protecting on a board with other pieces.
	**/
	public static BitSet getCoveredSquares(Piece p, int square, BitSet occupied, BitSet board){

		int[][] iBoard = Util.getIntBoard(occupied);

		Point2D point = getPointFromSquare(square);	
		int col = (int)point.getX();
		int row = (int)point.getY();

		if(isKnight(p)){
			BitSet cp = p.getCaptureSquares(square);
			cp.andNot(board);
			return cp;
		}

		if(isRook(p) || isQueen(p)){
			//Up
			for(int r = row-1; r >= 0; r--){
				if(iBoard[col][r] == 1){
					break;	
				}
				board.set(r * 8 + col,true);
			}
			//Right
			for(int c = col+1; c < 8; c++){
				if(iBoard[c][row] == 1){
					break;
				}
				board.set(row * 8 + c,true);
			}
			//Down
			for(int r = row+1; r < 8; r++){
				if(iBoard[col][r] == 1){
					break;
				}
				board.set(r * 8 + col,true);
			}
			//Left
			for(int c = col-1; c >= 0; c--){
				if(iBoard[c][row] == 1){
					break;
				}
				board.set(row * 8 + c,true);
			}
		}

		if(isBishop(p) || isQueen(p)){
			//Up Left
			for(int r = row-1,c = col-1; r >= 0 && c >= 0; r--,c--){
				if(iBoard[c][r] == 1){
					break;	
				}
				board.set(r * 8 + c,true);
			}
			//Up Right
			for(int r = row-1,c = col+1; r >= 0 && c < 8; r--,c++){
				if(iBoard[c][r] == 1){
					break;
				}
				board.set(r * 8 + c,true);
			}
			//Down Left
			for(int r = row+1,c = col-1; r < 8 && c >= 0; r++,c--){
				if(iBoard[c][r] == 1){
					break;
				}
				board.set(r * 8 + c,true);
			}
			//Down Right
			for(int r = row+1,c = col+1; r < 8 && c < 8; r++,c++){
				if(iBoard[c][r] == 1){
					break;
				}
				board.set(r * 8 + c,true);
			}
		}
			
		return board;

	}

}
