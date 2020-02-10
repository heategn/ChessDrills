package org.chessdrills_test.games.quickcapture;

import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.beans.property.SimpleStringProperty;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.Exception;
import java.util.HashMap;
import java.util.BitSet;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import javafx.stage.Stage;
import javafx.application.Platform;

import org.chessdrills.ChessDrillsController;
import org.chessdrills.ChessDrills;
import org.chessdrills.pieces.Piece;
import org.chessdrills.games.quickcapture.QuickCaptureMenu;
import org.chessdrills.games.quickcapture.QuickCapture;
import org.chessdrills.board.Board;

public class QuickCaptureTest{
    
    private QuickCaptureMenu menuCreator;
    private HashMap <QuickCaptureMenu.Field, SimpleStringProperty> gameSettings = new HashMap<QuickCaptureMenu.Field, SimpleStringProperty>();
    
    private void printOut(BitSet data){
        for(int i = 0; i < 64; i++){
            int offset = i % 8;
            if(offset == 0 && i > 0){
                System.out.println();
            }
            if(data.get(i)){
                System.out.print( " 1 " );
            }
            else{
                System.out.print(" - ");
            }
        }
    }
    
    public QuickCaptureTest(){}
    
    public void start(Stage stage) throws Exception {}
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    private ChessDrillsController cc;
    private QuickCapture ii;
 
    private ArrayList<Piece> util_getPieces(){
        ArrayList<Piece> p = new ArrayList();
        p.add(new Piece(Piece.PieceType.ROOK_LIGHT));
        p.add(new Piece(Piece.PieceType.KNIGHT_LIGHT));
        p.add(new Piece(Piece.PieceType.KNIGHT_LIGHT));
        p.add(new Piece(Piece.PieceType.BISHOP_LIGHT));
        p.add(new Piece(Piece.PieceType.QUEEN_LIGHT));
        return p;
    }

		@Test
		public void testStartNew(){
			String[] args = {"testMode","quickcapture"};
			ChessDrills.main(args);
			Platform.exit();
		}

}
