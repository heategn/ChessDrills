package chessdrills.games.invisiblepairs;

import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.beans.property.SimpleStringProperty;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import java.lang.Exception;
import java.util.HashMap;
import java.util.BitSet;
import java.util.ArrayList;
import chessdrills.ChessDrillsController;
import chessdrills.pieces.Piece;
import chessdrills.games.invisiblepairs.InvisiblePairsMenu;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class InvisiblePairsTest{
    
    private GridPane gridLayout = new GridPane();
    private InvisiblePairsMenu menuCreator;
    private HashMap <InvisiblePairsMenu.Field, SimpleStringProperty> gameSettings = new HashMap<InvisiblePairsMenu.Field, SimpleStringProperty>();
    
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
    
    public InvisiblePairsTest(){}
    
    public void start(Stage stage) throws Exception {}
    
    @BeforeClass
    public static void setUpClass() {

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    private ChessDrillsController cc;
    private InvisiblePairs ii;
 
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {}

//  @Test
    public void testMove(){}
    
    private ArrayList<Piece> util_getPieces(){
        ArrayList<Piece> p = new ArrayList();
        p.add(new Piece(Piece.PieceType.ROOK_LIGHT));
        p.add(new Piece(Piece.PieceType.ROOK_LIGHT));
        p.add(new Piece(Piece.PieceType.KNIGHT_LIGHT));
        p.add(new Piece(Piece.PieceType.KNIGHT_LIGHT));
        p.add(new Piece(Piece.PieceType.BISHOP_LIGHT));
        p.add(new Piece(Piece.PieceType.BISHOP_LIGHT));
        p.add(new Piece(Piece.PieceType.QUEEN_LIGHT));
        return p;
    }

//    @Test
    public void testSetupBoard(){
        Class<?> c;
        Method m;
        float success = 0;
        float fail = 0;
        int attempt = 0;
        
        try{
            c = Class.forName("chessdrills.games.invisiblepairs.InvisiblePairs");
            Object ip = c.newInstance();
            Field turns = c.getDeclaredField("gameTotalTurns");
            turns.setAccessible(true);
            turns.set(ip, 15);
            m = c.getDeclaredMethod("setupStartPositions", new Class[]{ ArrayList.class });
            m.setAccessible(true);
            Object r = m.invoke(ip, util_getPieces());
            assert(Boolean)r == true;
        }
        catch(Exception e){
            throw new Error(e);
        }
    }
    
//    @Test
    public void testSetupBoardExperiment() {
        
        Class<?> c;
        Method m;
        float success = 0;
        float fail = 0;
        int attempt = 0;
        
        try{
            c = Class.forName("chessdrills.games.invisiblepairs.InvisiblePairs");
            Object ip = c.newInstance();
            Field turns = c.getDeclaredField("gameTotalTurns");
            turns.setAccessible(true);
            turns.set(ip, 15);
            m = c.getDeclaredMethod("setupStartPositions", new Class[]{ ArrayList.class });
            m.setAccessible(true);
            for(int i = 0; i < 1; i++){
                attempt++;
                Object r = m.invoke(ip, util_getPieces());
                if(r.toString() == "true"){
                    success++;
                }
                else{
                    fail++;
                }
                if(attempt % 10 == 0){
                    System.out.println("attempt: " + attempt + " success: " + success + " fail: " + fail);
                }
                
            }
        }
        catch(Exception e){
            throw new Error(e);
        }
        
        float percentage = fail / success;
        System.out.println(percentage + " failure percentage.");
        
    }

//  @Test
    public void testStartNew(){
        ii.start();
    }
    
//  @Test
    public void testStart(){
        gameSettings.put(InvisiblePairsMenu.Field.ROOKS, new SimpleStringProperty("2"));
        gameSettings.put(InvisiblePairsMenu.Field.BISHOPS, new SimpleStringProperty("2"));
        gameSettings.put(InvisiblePairsMenu.Field.KNIGHTS, new SimpleStringProperty("2"));
        gameSettings.put(InvisiblePairsMenu.Field.QUEENS, new SimpleStringProperty("8"));
        gameSettings.put(InvisiblePairsMenu.Field.ROUNDS, new SimpleStringProperty("15"));
        ii.addGameSettings(gameSettings);
        ii.start();
    }

}