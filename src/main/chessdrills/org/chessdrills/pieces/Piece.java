package org.chessdrills.pieces;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import java.util.HashMap;
import java.util.BitSet;
import java.util.ArrayList;
import org.chessdrills.pieces.ChessPieceFactory;
import org.chessdrills.Assets;
import java.io.File;
import org.chessdrills.util.Util;


/**
 *  Represents the piece on the board. It's also a container for underlying data-classes governing it's behavior and provides utility methods for gathering runtime information.
 */
public class Piece extends ImageView{
    
    public static enum PieceColor {LIGHT, DARK};
    public static enum PieceType {PAWN_LIGHT, PAWN_DARK, ROOK_LIGHT,ROOK_DARK, KNIGHT_LIGHT, KNIGHT_DARK, BISHOP_LIGHT, BISHOP_DARK, QUEEN_LIGHT,QUEEN_DARK, KING_LIGHT, KING_DARK}; 

		public ChessPieceData pieceData;
		public PieceType type;
		public boolean darkSquared = false;

    private String imagePath = "/org/chessdrills/assets/";
    private double imageWidth;
    private double imageHeight;
    private String sizeKey; //Used to cache position offsets
    private int startPosition = -1;
		private Point2D position = new Point2D(0,0);
		private double x = 0;
		private double y = 0;

    public Piece(PieceType type){
			Piece(type, 50, true);
    }

		//for bishops
		public Piece(PieceType type, boolean bishopSquares){
			Piece(type, 50, bishopSquares);
		}

		public Piece(PieceType type, int offset){
			Piece(type, offset, false);
		}

		private void Piece(PieceType type, int offset, boolean bishopSquares){

			pieceData = ChessPieceFactory.getData(type);

			if(type == Piece.PieceType.BISHOP_LIGHT || type == Piece.PieceType.BISHOP_DARK){
				setLightSquares(bishopSquares);	
			}
			
			this.type = type;

			if(type.toString().contains("DARK")){
				startPosition = pieceData.darkStartingPosition + offset;
				this.setImage(pieceData.darkImage);
			}
			else{
				startPosition = pieceData.lightStartingPosition + offset;
				this.setImage(pieceData.lightImage);        
			}

			this.x = this.getLayoutX();
			this.y = this.getLayoutY(); 
			sizeKey = Double.toString(imageWidth).concat(Double.toString(imageHeight));

		}

		//for bishops
		public void setLightSquares(boolean lightSquares){
			BishopData d = (BishopData)pieceData;
			d.setLightSquares(lightSquares);
		}

		public Point2D getPositionInParent(){
			Point2D pos = localToParent(this.getLayoutBounds().getCenterX(), this.getLayoutBounds().getCenterY());
			return pos;
		}

		public void translate(double tx, double ty){

			this.toFront();
			double currentX = this.getLayoutX() + this.getLayoutBounds().getCenterX();
			double currentY = this.getLayoutY() + this.getLayoutBounds().getCenterY();
			this.setTranslateX(tx - currentX);
			this.setTranslateY(ty - currentY);

			this.setTranslateZ(10);
			this.x += tx - currentX;
			this.y += ty - currentY;

		}

    public int getStartingPosition(){
      	return startPosition;
    }
   
    public HashMap getCaptureBoard(){
        return pieceData.getCaptureBoard();
    }
 
    public BitSet getCaptureSquares(int square){
        return pieceData.getCaptureSquares(square);
    }
    
		public boolean isAttacking(int s1, int s2){
			return pieceData.isAttacking(s1,s2);
		}

		public boolean isAttacking(int s1, int s2, BitSet board){
			return pieceData.isAttacking(s1,s2,board);
		}

    public ArrayList<Integer> getCaptureSquaresArr(int square){
        ArrayList<Integer> l = new ArrayList();
        BitSet s = getCaptureSquares(square);
        for(int i = s.nextSetBit(0); i >= 0; i = s.nextSetBit(i+1)){
            l.add(i);
        }
        return l;
    }
    
		
		//todo: Depreciated. Still used by Invisible Pairs.
    public boolean isSquareEmptyAndNotAttacking(int square, BitSet board){
        BitSet captureBoardForSquare = (BitSet)getCaptureSquares(square).clone();
        captureBoardForSquare.and(board);
        if(captureBoardForSquare.cardinality() == 0){
					return true;
        }
        return false;
    }
    
		/**
		* Returns how many pieces this piece is attacking.
		* todo: Depreciated. Use Util.numAttacking instead
		*/
    public int getNumberAttacking(int square, BitSet board){
        BitSet captureSquares = (BitSet)getCaptureSquares(square).clone();
        captureSquares.and(board);
        return captureSquares.cardinality();
    }
    
		//Depreciated and not accurate without additional logic to account for obstacles. 
		//todo: Still used by InivisblePairs.
    public ArrayList<Integer> getPiecesAttacking(int square, BitSet board){
        BitSet captureSquares = (BitSet)getCaptureSquares(square).clone();
        captureSquares.and(board);
        return captureSquares.stream().collect(ArrayList<Integer>::new, (al,b) -> {
            if(captureSquares.get(b) == true)
                al.add(b);
        },ArrayList<Integer>::addAll);
    }
    
    public boolean checkForCollision(int currentSquare, int targetSquare, int targetPieceSquare){
        return pieceData.checkForCollision(currentSquare, targetSquare, targetPieceSquare);
    }

    public String getSizeKey(){
        return sizeKey;
    }

}
