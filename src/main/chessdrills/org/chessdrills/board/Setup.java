package org.chessdrills.board;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.chessdrills.pieces.Piece;
import org.chessdrills.util.Util;

/**
 * A BitSet representing the pieces on the board plus other useful methods for calculating their relationships.
 */
public class Setup extends BitSet{
    
	private Piece[][] board = new Piece[8][8];
	private HashMap<Integer, Piece> square2Piece = new HashMap();
	private HashMap<Piece, Integer> piece2Square = new HashMap();
	private ArrayList<Piece> pieces = new ArrayList();
	private int[] occupied;
	private BitSet covered = new BitSet(64);
	private boolean invalidated = false;

	public Setup(HashMap<Integer,Piece> pieceMap){
		super(64);
		this.newSetup(pieceMap);
	}

	public Setup(){
		super(64);
	}

	public void newSetup(HashMap<Integer,Piece> pieces){

		this.square2Piece = (HashMap<Integer,Piece>)pieces.clone();

		for(Map.Entry<Integer,Piece> m : this.square2Piece.entrySet()){
			int sq = m.getKey();
			Piece p = m.getValue();
			int[] coords = Util.getCoords(sq);
			this.board[coords[0]][coords[1]] = p;
			this.set(sq);
			this.piece2Square.put(p,sq);
			this.pieces.add(p);
		}

		this.occupied = this.stream().toArray();
		this.invalidated = true;

	}

	//Add or replace piece on the square
	public void addPiece(int sq, Piece p){
		this.invalidated = true;

		if(this.get(sq)){
			Piece pToRemove = this.square2Piece.get(sq);
			this.pieces.remove(pToRemove);
		}

		this.pieces.add(p);
		this.set(sq,true);
		int[] coords = Util.getCoords(sq);
		this.board[coords[0]][coords[1]] = p;
		this.square2Piece.put(sq,p);
		this.piece2Square.put(p,sq);
	}
	
	public Piece getPiece(int sq){
		return square2Piece.get(sq);
	}

	public int getSquareOfPiece(Piece p){
		int t = -1;
		t = piece2Square.get(p);
		return t;
	}

	public void removePiece(Piece p){

		Integer sq = this.piece2Square.remove(p);

		if(sq == null){
			throw new Error("Piece not found");
		}

		this.invalidated = true;
		this.square2Piece.remove(sq);
		this.set(sq,false);
		this.pieces.remove(p);
		int[] coords = Util.getCoords(sq);
		this.board[coords[0]][coords[1]] = null;
		return;
		
	}

	public void removePiece(int sq){

		Piece p = square2Piece.remove(sq);
		if(p == null){
			throw new Error("Piece not found on square");
		}
		this.invalidated = true;
		piece2Square.remove(p);	
		this.pieces.remove(p);
		this.set(sq,false);
		int[] coords = Util.getCoords(sq);
		this.board[coords[0]][coords[1]] = null;
		return;

	}

	public int[] getSquares(){
		return this.stream().toArray();
	}

  public Piece[][] getBoard(){
		return this.board;
	}
	
	public BitSet getProtectedBitSet(){
		recalculate();	
		BitSet bs = (BitSet)covered.clone();
		return bs;
	}

	public int[] getProtected(){
		return getProtectedBitSet().stream().toArray();
	}

	public ArrayList<Piece> getPieces(){
		return this.pieces;
	}

	public Piece getLastPieceAdded(){
		return this.pieces.get(this.pieces.size()-1);
	}

	public HashMap<Integer,Piece> getPieceMap(){
		return this.square2Piece;
	}

	private void recalculate(){

		if(!this.invalidated){
			return;
		}

		this.covered.clear();
		BitSet obstructed = new BitSet(64);

		for(Map.Entry<Integer,Piece> m : square2Piece.entrySet()){

			Piece p1 = m.getValue();
			int square1 = m.getKey();

			int[] coords1 = Util.getCoords(square1);
			BitSet c = p1.getCaptureSquares(square1);

			if(c.intersects(this) && !Util.isKnight(p1)){

				BitSet intersections = (BitSet)c.clone();
				intersections.and(this);

				for(int square2 : Util.getInts(intersections)){

					if(square1 == square2){
						continue;
					}

					obstructed.clear();
					int[] coords2 = Util.getCoords(square2);
					int dx = coords2[0] - coords1[0];
					int dy = coords2[1] - coords1[1];	
					dx = (dx != 0) ? dx/Math.abs(dx) : 0;
					dy = (dy != 0) ? dy/Math.abs(dy) : 0;
					int cx = coords2[0] + dx;
					int cy = coords2[1] + dy;

					if(dx == 0 && dy == 0){
						throw new Error("problem");
					}

					while(cx >= 0 && cx < 8 && cy >= 0 && cy < 8){
						obstructed.set(Util.getSquare(cx,cy));
						cx += dx;
						cy += dy;
					}

					c.andNot(obstructed);

				}

			}

			covered.or(c);

		}

		this.invalidated = false;
		
	}

}
