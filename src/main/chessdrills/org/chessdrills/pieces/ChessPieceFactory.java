package org.chessdrills.pieces;

import org.chessdrills.pieces.ChessPieceData;
import java.util.BitSet;
import org.chessdrills.pieces.BishopData;
import org.chessdrills.pieces.Piece;
import org.chessdrills.Assets;

import java.util.HashMap;
        
/**
 *
 * Generates the data for a given piece type. It can be refactored into seperate class files in the future if needed.
 */
public class ChessPieceFactory {
    
    //private static HashMap<Piece.PieceType, Piece> pieces = new HashMap();
    public static final int boardSize = 64;
    public static final int startingSquare = 36; //Staring square is e4
    private static final int midColumn = 4;
    public static final int columnSize = 8;
    private static final int rowSize = 8;
    private static final int minRow = startingSquare - midColumn;
    private static final int maxRow = startingSquare + midColumn;
    private static final int middleRow = midColumn;

    public static ChessPieceData getData(Piece.PieceType type){
        ChessPieceData pData = null;
        if(type == Piece.PieceType.PAWN_LIGHT || type == Piece.PieceType.PAWN_DARK){ 
            pData = buildPawn(type);
        }
        if(type == Piece.PieceType.ROOK_LIGHT || type == Piece.PieceType.ROOK_DARK){
            pData = buildRook(type);
        }
        if(type == Piece.PieceType.KNIGHT_LIGHT || type == Piece.PieceType.KNIGHT_DARK){
            pData = buildKnight(type);
        }
        if(type == Piece.PieceType.BISHOP_LIGHT || type == Piece.PieceType.BISHOP_DARK){
            pData = buildBishop(type);
        }
        if(type == Piece.PieceType.KING_DARK || type == Piece.PieceType.KING_LIGHT){
            pData = buildKing(type);
        }
        if(type == Piece.PieceType.QUEEN_LIGHT || type == Piece.PieceType.QUEEN_DARK){
            pData = buildQueen(type);
        }
        return pData;
    }
    
    private static void build(ChessPieceData p, Piece.PieceType type){
        for(int s = 0; s < boardSize; s++){
            int srank = s % columnSize;
            int sfile = Math.floorDiv(s, columnSize);
            switch(type){
                case ROOK_LIGHT:
                    buildRookBoard(p, s, srank, sfile);
                    break;
                case BISHOP_LIGHT:
                    buildBishopBoard(p, s, srank, sfile, type);
                    break;
                case BISHOP_DARK:
                    buildBishopBoard(p, s, srank, sfile, type);
                    break;
                case QUEEN_LIGHT:
                    buildBishopBoard(p, s, srank, sfile, type);
                    buildRookBoard(p, s, srank, sfile, false);
                    break;
                case KNIGHT_LIGHT:
                    buildKnightBoard(p, s, srank, sfile);
                    break;
            }
        }
    }

    private static void buildRookBoard(ChessPieceData p, int square, int srank, int sfile){
       buildRookBoard(p,square,srank,sfile,true);
    }

    private static void buildRookBoard(ChessPieceData p, int square, int srank, int sfile, boolean flipPieceSquare){
       for(int i = 0; i < boardSize; i++){
           int rank = i % columnSize;
           int file = Math.floorDiv(i, columnSize);
           if(file == sfile || rank == srank){
               if(i == square && !flipPieceSquare){
                   continue;
               }
               p.getCaptureBoardRef().get(square).flip(i);
           }
       }
    }

    /**
     * 
     *  This generates two capture boards -- one for light and one for dark squares. It's also used by the queen piece type.
     */
    private static <T extends ChessPieceData> void buildBishopBoard (T p, int square, int srank, int sfile, Piece.PieceType type){

        boolean isBishop = false;
        HashMap<Integer, BitSet> cb = p.getCaptureBoardRef();
        if(type == Piece.PieceType.BISHOP_DARK || type == Piece.PieceType.BISHOP_LIGHT){
            isBishop = true;
        }
        
        //The rank and file of the square will be treated as the origin
        int mFile = Math.floorDiv(square, columnSize); 
        int mRank = square % columnSize;
        
        for(int i = 0; i < boardSize; i++){

            int file = Math.floorDiv(i, columnSize);
            int rank = i % columnSize;

            //Check if we're on a diagnol
            int x = Math.abs(file - mFile);
            int rel_rank = Math.abs(mRank - rank);
            if( rel_rank == x ){
                cb.get(square).flip(i);
            }
            
        }

        p.setCaptureBoard(cb);
        if(p.getClass().getSimpleName().equals("BishopData")){
            HashMap<Integer, BitSet> cb2 = new HashMap(cb);
            BishopData pd = (BishopData)p;
            pd.setCaptureBoard2(cb2);
        }

    }

     private static void flipKnightSquare(ChessPieceData p, int square, int toFlip){
        if( toFlip >= 0 && toFlip < boardSize){
            p.getCaptureBoardRef().get(square).flip(toFlip);
        }
     }

      private static void buildKnightBoard(ChessPieceData p, int square, int srank, int sfile){

        int colSize2 = columnSize * 2;
        int fromTop = (square > 0) ? square % columnSize : 0;
        int fromRight = (square > 0) ? columnSize - Math.floorDiv(boardSize, square) : 0;

        if( fromTop > 1 && fromRight < 8){
            int upLeft = square - 2 + columnSize; flipKnightSquare(p, square, upLeft);
        }

        if(fromTop > 1 && fromRight > 0){
            int upRight = square - 2 - columnSize; flipKnightSquare(p, square, upRight);
        }

        if(fromTop > 0 && fromRight < 8){
            int leftUp = square - 1 + colSize2; flipKnightSquare(p, square, leftUp);
        }

        if(fromTop > 0 && fromRight > 1){
            int rightUp = square - 1 - colSize2; flipKnightSquare(p, square, rightUp);
        }

        if(fromTop < 7 && fromRight < 8){
            int leftDown = square + 1 + colSize2; flipKnightSquare(p, square, leftDown);
        }

        if(fromTop < 7 && fromRight > 0){
            int rightDown = square + 1 - colSize2; flipKnightSquare(p, square, rightDown);
        }

        if(fromTop < 6 && fromRight < 8){
            int downLeft = square + 2 + columnSize; flipKnightSquare(p, square, downLeft);
        }

        if(fromTop < 6 && fromRight > 0){
            int downRight = square + 2 - columnSize; flipKnightSquare(p, square, downRight);
        }

        p.getCaptureBoardRef().get(square).flip(square);

    }

    private static ChessPieceData buildPawn(Piece.PieceType type){

        ChessPieceData p = new ChessPieceData(boardSize, type);
        p.lightImage = Assets.PAWN_LIGHT;
        p.darkImage = Assets.PAWN_DARK;
        return p;

    }    

    private static ChessPieceData buildRook(Piece.PieceType type){

        ChessPieceData p = new ChessPieceData(boardSize, type);
        p.lightImage = Assets.ROOK_LIGHT;
        p.darkImage = Assets.ROOK_DARK;
        build(p, Piece.PieceType.ROOK_LIGHT);
        return p;

    }

    private static ChessPieceData buildKnight(Piece.PieceType type){
        ChessPieceData p = new ChessPieceData(boardSize, type);
        p.lightImage = Assets.KNIGHT_LIGHT;
        p.darkImage = Assets.KNIGHT_DARK;
        build(p, Piece.PieceType.KNIGHT_LIGHT);
        return p;
    }

    private static ChessPieceData buildBishop(Piece.PieceType type){
        BishopData p = new BishopData(boardSize, type);
        p.lightImage = Assets.BISHOP_LIGHT;
        p.darkImage = Assets.BISHOP_DARK;
        build(p, type);
        HashMap <Integer,BitSet> cb1 = p.getCaptureBoardRef();
        HashMap <Integer,BitSet> cb2 = p.getCaptureBoardRef2();
        
        for(int i = 0; i < boardSize; i++){
            int file = Math.floorDiv(i, columnSize);
            int rank = i % columnSize;
            if(file % 2 != 1){
                if(rank % 2 != 0){
                    cb1.remove(i);
                }else{
                    cb2.remove(i);
                }
            }
            else{
                if(rank % 2 != 0){
                    cb2.remove(i);
                }else{
                    cb1.remove(i);
                }
            }
        }
        
        return p;
    }

    private static ChessPieceData buildQueen(Piece.PieceType type){
        ChessPieceData p = new ChessPieceData(boardSize, type);
        p.lightImage = Assets.QUEEN_LIGHT;
        p.darkImage = Assets.QUEEN_DARK;
        build(p, Piece.PieceType.QUEEN_LIGHT);
        return p;
    }

    private static ChessPieceData buildKing(Piece.PieceType type){
        ChessPieceData p = new ChessPieceData(boardSize, type);
        p.lightImage = Assets.KING_LIGHT;
        p.darkImage = Assets.KING_DARK;
        return p;
    }
        
}
