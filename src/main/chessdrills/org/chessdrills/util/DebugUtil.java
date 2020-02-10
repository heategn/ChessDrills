package org.chessdrills.util;

import org.chessdrills.board.Board;
import org.chessdrills.pieces.Piece;

import java.util.BitSet;

public class DebugUtil {
    /**
     * Prints a BitBoard formatted 8x8
     * @param data The BitSet to print.
     */
    public static void printOut(BitSet data){
        for(int i = 0; i < 64; i++){
            int offset = i % 8;
            if(offset == 0 && i > 0){
                System.out.println();
            }
            if(data.get(i)){
                Piece p = Board.getPiece(i);
                String t = " 1 ";
                switch(p.type){
                    case BISHOP_LIGHT:
                        t = "B";
                        break;
                    case KNIGHT_LIGHT:
                        t = "N";
                        break;
                    case ROOK_LIGHT:
                        t = "R";
                        break;
                }
                System.out.print( " *" + t + " " );
            }
            else{
                String out = (i < 10) ? "0" + Integer.toString(i) : Integer.toString(i);
                System.out.print(" " + out + " ");
            }
        }
        System.out.println("");
        System.out.println("");
    }
}
