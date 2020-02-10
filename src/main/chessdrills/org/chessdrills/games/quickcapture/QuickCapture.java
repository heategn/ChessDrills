package org.chessdrills.games.quickcapture;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Point2D;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.BitSet;
import java.util.Random;
import java.util.Stack;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.IntStream;
import java.time.LocalTime;

import org.chessdrills.games.Game;
import org.chessdrills.games.GameManager;
import org.chessdrills.games.quickcapture.QuickCaptureMenu;
import org.chessdrills.board.Board;
import org.chessdrills.pieces.Piece;
import org.chessdrills.board.Square;
import org.chessdrills.board.Setup;
import org.chessdrills.games.RunningGameInfo;
import org.chessdrills.ChessDrillsController;
import org.chessdrills.pieces.BishopData;
import org.chessdrills.util.Util;
import java.util.logging.Level;

public class QuickCapture implements Game {
    
    public boolean debugging = false;
    public BitSet captureArea = new BitSet(64); //Pieces plus the squares they control
    
    private int gameTotalTurns = 15;
    private int currentPieceSquare = -1;
    private int otherPieceSquare;
    private int currentTurn = 0;
    private enum GameState {INITIAL, INITIAL_RESTART, WAITING, MOVING, FINISHED};
    private QuickCaptureMenu menuCreator;
    private GameState currentState;
    private Position currentPosition;
    private final Random rnd = new Random();
    private Pane gameInfo;
    private GridPane gridLayout = new GridPane();
    private HashMap <QuickCaptureMenu.Field, SimpleStringProperty> gameSettings = new HashMap();
    private final SimpleStringProperty sTurnCount = new SimpleStringProperty();
    private final SimpleStringProperty sStatCount = new SimpleStringProperty();
    private final String flashText = "Click anywhere on the board when ready.";
    private ArrayList<Piece> pieces;
    private ChessDrillsController chessDrillsController;
    private final static Logger LOG = Logger.getLogger(QuickCapture.class.getName());
    private final static ConsoleHandler LOG_HANDLER = new ConsoleHandler();
		private int startTime = 0;
		private Piece selectedPiece = null;
			
    private final Stack<Position> positions = new Stack();

    private final String instructions = "How to play:\n\n1) Capture the undefended piece.";
    
		class Position{

			private Setup position;

			public Position(Setup position){
				this.position = position;
			}

			public HashMap<Integer,Piece> getPosition(){
				return position.getPieceMap();
			}

			public Piece getPiece(int square){
				return this.position.getPiece(square);
			}

			public Piece getAttackingPiece(){
				ArrayList<Piece> pieces = this.position.getPieces();
				return pieces.get(pieces.size()-1);
			}

			public Piece getAttackedPiece(){
				ArrayList<Piece> pieces = this.position.getPieces();
				return pieces.get(pieces.size()-2);
			}

			public int getAttacker(){
				Piece p = this.getAttackingPiece();
				return this.position.getSquareOfPiece(p);
			}

			public int getAttacked(){
				Piece p = this.getAttackedPiece();
				return this.position.getSquareOfPiece(p);
			}

		}

    public QuickCapture(){}
    public QuickCapture( ChessDrillsController cc ){
        
        LOG_HANDLER.setFormatter(new SimpleFormatter());
        LOG_HANDLER.setLevel(Level.ALL);
        LOG.addHandler(LOG_HANDLER);
        LOG.setLevel(Level.OFF);

        chessDrillsController = cc;
        gameInfo = RunningGameInfo.getGameInfo();
        menuCreator = new QuickCaptureMenu(gridLayout, cc, this.gameSettings);
        
    }

    @Override
    public void pieceClicked(MouseEvent evt){
      Piece p = (Piece)evt.getTarget();
    }

    @Override
    public void mouseDragged(MouseEvent evt){

			if(this.currentState != GameState.MOVING){
				return;
			}

			this.currentState = GameState.MOVING;
			double x = evt.getX();
			double y = evt.getY();

			int sq = Board.mouseToSquare(x,y);	

			this.selectedPiece.translate(x,y);
			evt.consume();

    }
   
		@Override
		public void squareClicked(MouseEvent evt){}

		@Override
		public void squarePressed(MouseEvent evt){}

		@Override
		public void mousePressed(MouseEvent evt){

			if(this.currentState != GameState.WAITING){
				return;
			}
			double x = evt.getX();
			double y = evt.getY();

			int squareClicked = Board.mouseToSquare(x,y);

			Piece p = this.currentPosition.getPiece(squareClicked);
			if(p != this.currentPosition.getAttackingPiece()){
				return;
			}

			this.selectedPiece = p;
			this.currentState = GameState.MOVING;
			evt.consume();

		}

		@Override
		public void squareReleased(MouseEvent evt){
		}

    @Override
    public void pieceDragged(MouseEvent evt){
		}

    @Override
    public void pieceReleased(MouseEvent evt){}

    @Override
    public void mouseReleased(MouseEvent evt){

			if(this.currentState != GameState.MOVING){
				return;
			}

			this.currentState = GameState.WAITING;
			int toSquare = Board.mouseToSquare(evt.getX(), evt.getY());

			if(toSquare == this.currentPosition.getAttacked()){
				if(this.currentTurn == this.gameTotalTurns){
					end();
				}
				else{
					Board.updatePiecePosition(this.selectedPiece);
					this.currentTurn++;
					this.currentPosition = this.positions.pop();
					Board.newPieceSetup(this.currentPosition.getPosition());
				}
			}
			else{
				Board.translatePiece(this.selectedPiece, this.currentPosition.getAttacker());
			}

			this.selectedPiece = null;

		}

    @Override
    public void restart(){
      start();
    }
    
    @Override
    public String getInstructions(){
      return this.instructions;
    }
    
    private String getTurnCount(){
        return "Turn: " + this.currentTurn + "/" + this.gameTotalTurns;
    }
  
    private void setActivePiece(int square){
        currentPieceSquare = square;
    }
    
    private ArrayList<Piece> gatherSettings(){

        ArrayList<Piece> pieces = new ArrayList();
        this.gameTotalTurns = getField(QuickCaptureMenu.Field.ROUNDS);
        int nRooks = getField(QuickCaptureMenu.Field.ROOKS);
        int nBishops = getField(QuickCaptureMenu.Field.BISHOPS);
        int nKnights = getField(QuickCaptureMenu.Field.KNIGHTS);
        int nQueen = getField(QuickCaptureMenu.Field.QUEENS);

        addPieces(Piece.PieceType.ROOK_LIGHT, nRooks,pieces);
        addPieces(Piece.PieceType.BISHOP_LIGHT, nBishops,pieces);
        addPieces(Piece.PieceType.KNIGHT_LIGHT, nKnights,pieces);
        addPieces(Piece.PieceType.QUEEN_LIGHT, nQueen,pieces);

        return pieces;

    }

		private Piece.PieceType randomDarkPiece(){
			ArrayList<Piece.PieceType> types = new ArrayList();
			types.add(Piece.PieceType.ROOK_DARK);
			types.add(Piece.PieceType.BISHOP_DARK);
			types.add(Piece.PieceType.KNIGHT_DARK);
			types.add(Piece.PieceType.QUEEN_DARK);
			return randomPiece(types);
		}

		private Piece.PieceType randomLightPiece(){
			ArrayList<Piece.PieceType> types = new ArrayList();
			types.add(Piece.PieceType.ROOK_LIGHT);
			types.add(Piece.PieceType.BISHOP_LIGHT);
			types.add(Piece.PieceType.KNIGHT_LIGHT);
			types.add(Piece.PieceType.QUEEN_LIGHT);
			return randomPiece(types);
		}

		private Piece.PieceType randomPiece(ArrayList<Piece.PieceType> types){
			return types.get(rnd.nextInt(types.size()));
		}

    public void start(){ 
        start(new ArrayList<Piece>());
    }
    
    public void start(GameState gameState){
        this.currentState = gameState;
        start();
    }
    
    public void start(ArrayList<Piece> pieces){

				this.positions.clear();

        if(pieces.size() == 0){
          pieces = gatherSettings();
        }

				for(int i = 0; i < this.gameTotalTurns; i++){

					Setup p = setupStartPositions(pieces);

					if(p == null){
						LOG.setLevel(Level.SEVERE);
						LOG.log(Level.SEVERE,"No possible setup found with current pieces." + pieces);
						return;
					}

					this.positions.add(new Position(p));	

				}	

        intializeGameInfo();

				this.startTime = LocalTime.now().getSecond();
        this.currentState = GameState.WAITING;
        this.currentTurn = 1;
				this.currentPosition = this.positions.pop();
        this.sTurnCount.set(getTurnCount());

				Board.newPieceSetup(this.currentPosition.getPosition());
				        
    }
    
		private Setup setupStartPositions(ArrayList<Piece> availablePieces){

				Board.clearBoard(false);

				HashMap<Integer,Piece> piecePositions = new HashMap();
				Setup position = new Setup(piecePositions);
				ArrayList<Piece> pieces = (ArrayList<Piece>)availablePieces.clone();

				Collections.shuffle(pieces);
				boolean r = generatePosition(position,pieces,0);

				if(!r){
					throw new Error("No valid position generated!");
				}

				return position;
					
		}

		private boolean generatePosition(Setup currentSetup, ArrayList<Piece> pieces, int index){

			int[] available;
			int lastSquare = -1;

			//last piece, should not be attacked.	
			if(index == pieces.size()-1){

				Piece p = pieces.get(index);
				BitSet bs = currentSetup.getProtectedBitSet();
				int firstSquare = currentSetup.getSquareOfPiece(pieces.get(0));

				bs.flip(0,64);

				if(bs.cardinality() == 0){
					return false;
				}

				List<Integer> finalSquares = Util.shuffle(bs.stream().toArray());	

				for(int last : finalSquares){

					Piece attacker = new Piece(this.randomDarkPiece());
					int[] shuffled = Util.getCoveredSquares(attacker, last, currentSetup).stream().toArray();

					//Find square where last piece is capturable by only the attacking piece.
					for(int i : shuffled){

						if(i == last){
							continue;
						}

						ArrayList<Integer> attackedSquares = Util.getSquaresAttackedBy(i,attacker,currentSetup);

						boolean invalid = false;

						for(int attacked : attackedSquares){
							if(Util.numAttackingThisSquare(attacked,currentSetup) == 0){
								invalid = true;
								break;
							}
						}

						if(invalid){
							continue;
						}

						currentSetup.addPiece(last,p);
						currentSetup.addPiece(i,attacker);
						return true;
			
					}

				}
				
				return false;

			}
			else if(index == 0){
				//Middle of board
				available = IntStream.of(26,27,28,29,34,35,36,37,42,43,44,45).toArray();
			}
			else{
				Piece lastPiece = currentSetup.getLastPieceAdded();
				lastSquare = currentSetup.getSquareOfPiece(lastPiece);
				BitSet bs = Util.getCoveredSquares(lastPiece, lastSquare, currentSetup);
				bs.or(currentSetup);
				available = bs.stream().toArray();
			}

			Piece	p = pieces.get(index);
			List<Integer> shuffled = Util.shuffle(available);
			boolean bishop =  Util.isBishop(p);

			for(int i : shuffled){

				if(bishop){
					if(p.getCaptureSquares(i).cardinality() == 0){
						//Wrong color. 
						continue;
					}
				}

				//Two or more pieces are now on the board.
				if(lastSquare > 0){
					ArrayList<Integer> onLine = Util.getPiecesOnLine(lastSquare,i,currentSetup);
					if(onLine.size() > 0){
						continue;
					}
				}

				currentSetup.addPiece(i,p);

				if(generatePosition(currentSetup,pieces,index + 1)){
					return true;
				}
		
				currentSetup.removePiece(i);

			}

			return false;

		}

    /**
     * Displays information about the current game.
     */
    private void intializeGameInfo(){
        gameInfo.getChildren().clear();
        Text turn = new Text();
        turn.setStyle("-fx-fill: teal;-fx-font-weight:bold;");
        turn.setTextAlignment(TextAlignment.CENTER);
        turn.textProperty().bind(sTurnCount);
        gameInfo.getChildren().add(turn);
    }
    
//    private boolean checkSquare(int square){
//    		return true;
//    }

    private int getField(QuickCaptureMenu.Field field){
      	return Integer.parseInt(gameSettings.get(field).getValue());
    }
    
		//Debugging
    private void addPieces(Piece.PieceType type, int num, ArrayList<Piece> pieces){
        Piece.PieceType chosenType;
        for(int i = 1; i <= num; i++){
            Piece p = new Piece(type);
            pieces.add(p);
        }
    }
    
	 	public void showEndGame(){

			Alert a = new Alert(Alert.AlertType.NONE, "Start again with same settings?");
			a.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					a.showAndWait().ifPresent(r->{
							if(r == ButtonType.YES){
								start(GameState.INITIAL_RESTART);
							} 
							else{
								GameManager.endGame();
							}
						});
				}
			});

		}

		public void showTotalTime(){

			int totalTime = LocalTime.now().getSecond() - this.startTime;

			totalTime = (totalTime < 0) ? 60 + totalTime : totalTime;
			Alert end = new Alert(Alert.AlertType.NONE, "Time: " + totalTime + " seconds.");
			end.getButtonTypes().addAll(ButtonType.OK);

			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					end.showAndWait().ifPresent(r->{
						showEndGame();
					});
				}
			});

		}

    public void end(){
			this.showTotalTime();
			this.currentState = GameState.FINISHED;
    }

    public GridPane getMenuUI(){
        return gridLayout;
    }

    public void addDialogButtons(Button ...buttons){
       gridLayout.addColumn(0, buttons[0]);
       gridLayout.addColumn(1, buttons[1]);
    }

}
