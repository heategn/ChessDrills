package chessdrills.board;

import chessdrills.pieces.Piece;
import chessdrills.pieces.ChessPieceFactory;
import chessdrills.pieces.ChessPieceData;
import chessdrills.board.Square;
import chessdrills.ChessDrillsController;
import chessdrills.games.Game;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.animation.KeyFrame;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.effect.DropShadow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.BitSet;

/**
 *
 * This contains the graphical layout and the data representation of the board and it's pieces. It provides methods for manipulating the pieces and other utility methods.
 */
public class Board{
    
    private static HashMap<Integer, Piece> pieces = new HashMap(64);
    private static HashMap<Integer, Rectangle> squares = new HashMap(64);
    private static HashMap<Piece, Integer> pieceLocations = new HashMap(64);
    private static GridPane drawing;
    private static final double defaultWidth = 800;
    private static final double defaultHeight = 800;
    private static BitSet board = new BitSet(64);
    private static final String _piecePath = "chessdrills.pieces.";
    private static double squareSize;
    private static final int rowNum = 8;
    private static final int boardSize = 64;
    private static final Color lightColor = Color.WHITE;
    private static final Color darkColor = Color.GREY;
    private static Game gameClient;
    private static SimpleDoubleProperty squareSizeProperty = new SimpleDoubleProperty();
    private static SimpleDoubleProperty flashTextXProp = new SimpleDoubleProperty();
    private static double offset;
    private static Piece activePiece = null;
    private static Runnable genericCallback;
    private static GridPane main_grid;
    private static Text flashText;
    
    public static void initialize(GridPane gc, GridPane main){
        
        drawing = gc;
        main_grid = main;
        
        int offset = getOffset().intValue();

        //Get piece data with the starting positions
        ChessPieceData rookData = ChessPieceFactory.getData(Piece.PieceType.ROOK_LIGHT);
        ChessPieceData bishopData = ChessPieceFactory.getData(Piece.PieceType.BISHOP_LIGHT);
        ChessPieceData knightData = ChessPieceFactory.getData(Piece.PieceType.KNIGHT_DARK);
        ChessPieceData queenData = ChessPieceFactory.getData(Piece.PieceType.QUEEN_DARK);
        ChessPieceData kingData = ChessPieceFactory.getData(Piece.PieceType.KING_LIGHT);
        ChessPieceData pawnData = ChessPieceFactory.getData(Piece.PieceType.PAWN_LIGHT);
        
        for(int i = 0; i < 8; i++){
            Piece pawnL = new Piece(Piece.PieceType.PAWN_LIGHT, offset);
            Piece pawnD = new Piece(Piece.PieceType.PAWN_DARK, offset);
            int lightStart = 48 + i;
            int darkStart = 8 + i;
            pieces.put( lightStart, pawnL );
            pieces.put( darkStart, pawnD );
        }
        
        pieces.put(0, new Piece(Piece.PieceType.ROOK_DARK));
        pieces.put(7, new Piece(Piece.PieceType.ROOK_DARK));
        pieces.put(56, new Piece(Piece.PieceType.ROOK_LIGHT));
        pieces.put(63, new Piece(Piece.PieceType.ROOK_LIGHT));
        pieces.put(1, new Piece(Piece.PieceType.KNIGHT_DARK));
        pieces.put(6, new Piece(Piece.PieceType.KNIGHT_DARK));
        pieces.put(57, new Piece(Piece.PieceType.KNIGHT_LIGHT));
        pieces.put(62, new Piece(Piece.PieceType.KNIGHT_LIGHT));
        pieces.put(2, new Piece(Piece.PieceType.BISHOP_DARK));
        pieces.put(5, new Piece(Piece.PieceType.BISHOP_DARK));
        pieces.put(58, new Piece(Piece.PieceType.BISHOP_LIGHT));
        pieces.put(61, new Piece(Piece.PieceType.BISHOP_LIGHT));
        pieces.put(3, new Piece(Piece.PieceType.QUEEN_DARK));
        pieces.put(59, new Piece(Piece.PieceType.QUEEN_LIGHT));
        pieces.put(4, new Piece(Piece.PieceType.KING_DARK));
        pieces.put(60, new Piece(Piece.PieceType.KING_LIGHT));
        
        calculateSquareSize(defaultWidth, defaultHeight);
        setupBoard();
        
        DropShadow ds = new DropShadow();
        ds.setOffsetY(2.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        double textWidth = (squareSizeProperty.get()*8) * .8;
        flashText = new Text("Click on the board when ready.");
        flashText.setEffect(ds);
        flashText.textAlignmentProperty().set(TextAlignment.CENTER);
        flashText.setWrappingWidth(textWidth);
        flashTextXProp.set(getCenter() - flashText.getWrappingWidth()/2);
        flashTextXProp.set(0);
        flashText.translateXProperty().bind(flashTextXProp);
        flashText.setTranslateY(100);
        flashText.setId("flash-text");
        
    }

    /*
    * Returns the center relative to the main layout
    */
    public static double getGlobalCenter(){
        return ((drawing.getWidth()) + (drawing.getBoundsInParent().getMinX()));
    }
    
    /*
    * Returns the center relative to the board;
    */
    public static double getCenter(){
        return drawing.getWidth()/2;
    }
    
    public static void removeFlashText(){
        main_grid.getChildren().removeAll(flashText);
    }
    
    public static void setFlashText(String txt){
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), flashText);
        ft.setFromValue(1f);
        ft.setToValue(0f);
        ft.setOnFinished((e)->{main_grid.getChildren().removeAll(flashText); flashText.setOpacity(1);});
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2500),e -> ft.play()));
        timeline.play();
        main_grid.add(flashText, 1,0);
        
    }
    
    public static HashMap<Piece,Integer> getPieceLocations(){
        return pieceLocations;
    }
    
    public static void setGameClient(Game game){
        gameClient = game;
    }
    
    public static void removeGameClient(){
        gameClient = null;
    }
    
    public static BitSet getBoard(){
        BitSet b = new BitSet(64);
        for(int i : pieces.keySet()){
            b.set(i);
        }
        return b;
    }
    
    /*
    *   Hides the pieces. The board itself remains visible.
    */
    public static void hide(){
        for(Entry<Integer, Piece> p : pieces.entrySet()){
            hidePiece(p.getValue());
        }
    }
    
    public static void hide(Runnable callback){
        genericCallback = callback;
        hide();
    }
    
    /*
    *   Show the pieces.
    */
    public static void show(){

        for(Entry<Integer, Piece> p : pieces.entrySet()){
            try{
                p.getValue().setVisible(true);
                showPiece(p.getValue());
            }
            catch(Exception e){
                throw new Error(pieces.toString());
            }
        }
    }
    
    public static void show(Runnable callback){
        genericCallback = callback;
        show();
    }

    public static void showPiece(int square){
        showPiece(pieces.get(square));
    }
    
    public static void showPiece (Piece p){
        FadeTransition fade = new FadeTransition(Duration.millis(300), p);
        fade.setFromValue(0f);
        fade.setToValue(1.0f);
        fade.setOnFinished((e)->{finishedFadeTransition();});
        fade.play();
    }

    public static void hidePiece(int square){
        hidePiece(pieces.get(square));
    }
    
    public static void hidePiece(Piece p){
        FadeTransition fade = new FadeTransition(Duration.millis(300), p);
        fade.setFromValue(1.0f);
        fade.setToValue(0.0f);
        fade.setOnFinished((e)->{finishedFadeTransition();});
        fade.play();
    }
     
    private static int piecesFaded = 0;
    private static void finishedFadeTransition(){
        if(genericCallback == null){
            return;
        }
        piecesFaded++;
        if(piecesFaded == pieces.size()){
            piecesFaded = 0;
            genericCallback.run();
            genericCallback = null;
        }
    }
    
    public static void movePiece(int fromSquare, int toSquare, Runnable callback){
        
        Piece p = pieces.get(fromSquare);
        
        if(p == null){
            throw new Error("No piece at " + fromSquare);
        }
        
        showPiece(p);
        
        double fromX = squares.get(fromSquare).getLayoutX();
        double fromY = squares.get(fromSquare).getLayoutY();
        double toX = squares.get(toSquare).getLayoutX();
        double toY = squares.get(toSquare).getLayoutY();
        double dX = (toX - fromX);
        double dY = (toY - fromY);
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), p);
        tt.setByX(dX);
        tt.setByY(dY);
        tt.setOnFinished((e) -> {
            hidePiece(p);
            callback.run();
        });
        tt.play();
        
        pieceLocations.put(p,toSquare);
        pieces.remove(fromSquare);
        pieces.put(toSquare, p);
        
    }
    
    public static void highlightSquare(int square){
        Square s = (Square)squares.get(square);
        s.highlight();
    }
    
    public static HashMap<Integer,Piece> getPieces(){
        return (HashMap)pieces.clone();
    }
    
    public static Piece getPiece(int squareNumber){
        return pieces.get(squareNumber);
    }
    
    public static int getSquareByPiece(Piece p){
        return pieceLocations.get(p);
    }
    
    private static Double getOffset(){
        return squareSizeProperty.get()/2;
    }

    public static void resize(GridPane main_grid, VBox side_nav){
        double width = main_grid.getWidth() - side_nav.getWidth();
        double height = main_grid.getHeight();
        flashTextXProp.set(getCenter() - (flashText.getWrappingWidth()/2));
        calculateSquareSize(width, height);
    }
    
    private static void calculateSquareSize(double width, double height){
        squareSize = ( width < height ) ? width / rowNum : height / rowNum;
        squareSizeProperty.set(squareSize);
        offset = getOffset();
    }

    public static void flip(int square){
        board.flip(square);
    }
    
    private static void checkIfANull(HashMap<Integer,Piece> pieceList){
        pieceList.forEach((k,v)->{
            if(v == null){
                throw new Error(k + " is null");
            }
        });
    }
    
    /*
    * Adds pieces to the board and related data;
    */
    public static void newPieceSetup(HashMap<Integer, Piece> pieceList){
        if(pieceList.size() == 0){
            throw new Error("pieceList is empty.");
        }
        checkIfANull(pieceList);
        clearBoard();
        
        for(Entry<Integer,Piece> p : pieceList.entrySet()){
            int row = Math.floorDiv(p.getKey(), 8);
            int col = p.getKey() % rowNum;
            addPieceGraphicToBoard(p.getValue(),col,row);
            Board.pieceLocations.put(p.getValue(),p.getKey());
            Board.board.flip(p.getKey());
        }
        
        Board.pieces = (HashMap<Integer,Piece>)pieceList.clone();

    }
    
    /*
    * Clears pieces from the board and related data;
    */
    public static void clearBoard(){
        if(drawing == null){
            return;
        }
        ArrayList<Node> toremove = new ArrayList();
        for(Node s : drawing.getChildren()){
            if(s instanceof Piece){
                toremove.add(s);
            }
            if(s instanceof Square){
                Square p = (Square)s;
                p.unHighlight();
            }
        }
        pieceLocations.clear();
        pieces.clear();
        drawing.getChildren().removeAll(toremove);
    }
    
    /*
    *   Adds the graphical component of the piece to the board.
    */
    private static void addPieceGraphicToBoard(Piece p, int col, int row){
        if(drawing == null){
            return;
        }
        p.fitHeightProperty().bind(squareSizeProperty);
        p.fitWidthProperty().bind(squareSizeProperty);
        p.setOnMouseClicked(ChessDrillsController.pieceClicked);
        drawing.add(p,col,row);
    }

    /*
    *   Creates the board and the related data.
    */
    private static void setupBoard(){
        drawing.getChildren().clear();
        boolean white = false;
        InnerShadow shadow = new InnerShadow();
        shadow.setHeight(5);
        shadow.setWidth(5);
        for(int row = 0; row < 8; row++){
            white = !white;
            for(int col = 0; col < 8; col++){
                Integer squareNum = row * 8 + col;
                
                Square square;
                if(white){
                    square = new Square(squareNum, squareSize, squareSize, lightColor);
                }
                else{
                    square = new Square(squareNum, squareSize, squareSize, darkColor);
                }
               
                square.setEffect(shadow);
                square.widthProperty().bind(squareSizeProperty);
                square.heightProperty().bind(squareSizeProperty);
                square.setOnMouseClicked(ChessDrillsController.squareClicked);
                squares.put(squareNum,square);
                drawing.add(square, col, row);
                
                //Debug
                //javafx.scene.text.Text t = new javafx.scene.text.Text(0,0,squareNum.toString());
                //drawing.add(t, col, row);
                
                white = !white;
               
                Piece p = pieces.get(squareNum);
                
                 if( p != null ){
                     addPieceGraphicToBoard(p,col,row);
                }

            }
            
        }
     
    }
    
}