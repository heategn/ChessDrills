package org.chessdrills.games.invisiblepairs;

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
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.BitSet;
import java.util.Random;
import java.util.Stack;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

import org.chessdrills.games.Game;
import org.chessdrills.games.GameManager;
import org.chessdrills.games.invisiblepairs.InvisiblePairsMenu;
import org.chessdrills.board.Board;
import org.chessdrills.pieces.Piece;
import org.chessdrills.board.Square;
import org.chessdrills.games.RunningGameInfo;
import org.chessdrills.ChessDrillsController;
import org.chessdrills.pieces.BishopData;
import java.util.logging.Level;

/**
 * 
 * Contains the logic for the InvisiblePairs game. The meat of the class is handled by the initial piece placement ( placePiece method ) and then the initial move calculations (calculateMoves method).
 */
public class InvisiblePairs implements Game {
    
    public boolean debugging = false;
    public BitSet captureArea = new BitSet(64); //Pieces plus the squares they control
    
    private int gameTotalTurns;
    private int currentPieceSquare = -1;
    private int otherPieceSquare;
    private int currentTurn = 0;
    private enum GameState {INITIAL, INITIAL_RESTART, MOVING, WAITING, FINISHED};
    private InvisiblePairsMenu menuCreator;
    private GameState currentState;
    private final Random rnd = new Random();
    private Pane gameInfo;
    private GridPane gridLayout = new GridPane();
    private HashMap <InvisiblePairsMenu.Field, SimpleStringProperty> gameSettings = new HashMap();
    private final SimpleStringProperty sTurnCount = new SimpleStringProperty();
    private final SimpleStringProperty sStatCount = new SimpleStringProperty();
    private final String flashText = "Click anywhere on the board when ready.";
    private final ArrayList<Move> allMoves = new ArrayList();
    private ArrayList<Piece> pieces;
    private ChessDrillsController chessDrillsController;
    private final static Logger LOG = Logger.getLogger(InvisiblePairs.class.getName());
    private final static ConsoleHandler LOG_HANDLER = new ConsoleHandler();
    
    private final String instructions = "How to play:\n\n1) Select the pieces to be randomly placed on the board, then click start.\n\n2) Memorize the position.\n\n3) When ready, click anywhere on the board. The pieces will become hidden, then, one piece will become visible and move to another location before hiding again. \n\n4) Click the square containing the other piece that is capturable by the previous piece (or, able to capture the previous piece).\n\n5) Repeat the process until the target number of turns is reached";
    
    class Placement extends ArrayList{
       public Placement(Integer square, Piece piece){
           this.add(square);
           this.add(piece);
       }
       public int getSquare(){
           return (int)this.get(0);
       }
       public Piece getPiece(){
           return (Piece)this.get(1);
       }
    }
    
    class Move extends ArrayList{
        
        public int otherPieceSquare;
        public int targetSquare;
        public String debugString = new String();
        
        public Move(Integer squareFrom, Integer squareTo){
            this.add(squareFrom);
            this.add(squareTo);
        }
        
        public int getFrom(){
            return (int)this.get(0);
        }
        
        public int getTo(){
            return (int)this.get(1);
        }
        
    }

    public void addGameSettings(HashMap<InvisiblePairsMenu.Field, SimpleStringProperty> settings){
        gameSettings = settings;
    }
    
    public InvisiblePairs(){}
    public InvisiblePairs( ChessDrillsController cc ){
        
        LOG_HANDLER.setFormatter(new SimpleFormatter());
        LOG_HANDLER.setLevel(Level.ALL);
        LOG.addHandler(LOG_HANDLER);
        LOG.setLevel(Level.OFF);

        chessDrillsController = cc;
        gameInfo = RunningGameInfo.getGameInfo();
        menuCreator = new InvisiblePairsMenu(gridLayout, cc, gameSettings);
        
    }

    public HashMap getGameSettings(){
        return gameSettings;
    }
    
    @Override
    public void pieceClicked(MouseEvent evt){
        Piece p = (Piece)evt.getTarget();
        boardClicked(Board.getSquareByPiece(p));
    }

    @Override
    public void pieceDragged(MouseEvent evt){};

  	@Override 
    public void pieceReleased(MouseEvent evt){};

  	@Override 
    public void squareReleased(MouseEvent evt){};

  	@Override 
    public void squarePressed(MouseEvent evt){};

  	@Override 
    public void mouseReleased(MouseEvent evt){};

  	@Override 
    public void mousePressed(MouseEvent evt){};

  	@Override 
    public void mouseDragged(MouseEvent evt){};


    @Override
    public void squareClicked(MouseEvent evt){
        Square s = (Square)evt.getTarget();
        boardClicked(s.squareNumber);
    }
        
    @Override
    public void restart(){
        start();
    }
    
    @Override
    public String getInstructions(){
        return this.instructions;
    }
    
    Runnable finishedMoving = new Runnable(){
        @Override
        public void run(){
            currentState = GameState.WAITING;
            currentTurn++;
            sTurnCount.set(getTurnCount());
            if(currentTurn == gameTotalTurns){
                end(true);
            }
            if(debugging){
                autoMove();
            }
        }
    };
    
    private String getTurnCount(){
        return "Turn: " + currentTurn + "/" + gameTotalTurns;
    }
  
    
    private void boardClicked(int squareNumber){
        if(currentState == GameState.INITIAL || currentState == GameState.INITIAL_RESTART){
            Board.removeFlashText();
            currentState = GameState.MOVING;
            Board.hide(new Runnable(){
                @Override
                public void run(){
                    move(finishedMoving);
                }
            });
        }
        else if(currentState == GameState.WAITING){
            Move m = allMoves.get(currentTurn);
            if(checkSquare(squareNumber)){
                currentState = GameState.MOVING;
                move(finishedMoving);
            }
            else{
                end(false);
            }
        }
    }
            
    private void setActivePiece(int square){
        currentPieceSquare = square;
    }
    
    private ArrayList<Piece> gatherSettings(){
        ArrayList<Piece> pieces = new ArrayList();
        gameTotalTurns = getField(InvisiblePairsMenu.Field.ROUNDS);
        int nRooks = getField(InvisiblePairsMenu.Field.ROOKS);
        int nBishops = getField(InvisiblePairsMenu.Field.BISHOPS);
        int nKnights = getField(InvisiblePairsMenu.Field.KNIGHTS);
        int nQueen = getField(InvisiblePairsMenu.Field.QUEENS);
        addPieces(Piece.PieceType.ROOK_LIGHT, nRooks,pieces);
        addPieces(Piece.PieceType.BISHOP_LIGHT, nBishops,pieces);
        addPieces(Piece.PieceType.KNIGHT_LIGHT, nKnights,pieces);
        addPieces(Piece.PieceType.QUEEN_LIGHT, nQueen,pieces);
        return pieces;
    }

    public void start(){ 
        start(new ArrayList<Piece>());
    }
    
    public void start(GameState gameState){
        currentState = gameState;
        start();
    }
    
		public void showStartDialog(){

			Alert a = new Alert(Alert.AlertType.NONE, "Click on the board to begin.");
			a.getButtonTypes().addAll(ButtonType.OK);

			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					a.showAndWait();
				}
			});

		}

    public void start(ArrayList<Piece> pieces){
        
        if(pieces.size() == 0){
            pieces = gatherSettings();
        }
        
        boolean success = setupStartPositions(pieces);
        
        intializeGameInfo();
        
        //If the game was restarted after finishing a previous game, don't display the dialog again.
        if(currentState != GameState.INITIAL_RESTART){
					showStartDialog();
        }

        currentState = GameState.INITIAL;
        currentTurn = 0;
        sTurnCount.set(getTurnCount());
        
    }
    
    /**
     * This takes a list of the pieces to be placed the board, places them via the placePiece method, then calculates the specified number of moves for the game via the calculateMoves method.
     * General process:
     * Step 1) Clear the moves list.
     * Step 2) While the moves list needs moves:
     *  Step 1) Generate a new position.
     *  Step 2) Setup board with the new position.
     *  Step 3) Calculate moves.
     *  Step 4) If it's not possible to calculate the required number of moves, generate a new position and start again.
     * @param pieces List of pieces to place on the board.
     * @return boolean Returns success if all pieces were placed, false otherwise.
     */
    private boolean setupStartPositions(ArrayList<Piece> pieces){
        
        HashMap<Integer, Piece> piecePositions = new HashMap();
        
        allMoves.clear();

        while(allMoves.size() < (gameTotalTurns + 1)){
             
            Stack<Placement> placements = new Stack();
            BitSet availableSquares = new BitSet(64);
            BitSet occupiedSquares = new BitSet(64);
            availableSquares.set(0, 63, true);
            occupiedSquares.set(0, 63, false);

            Collections.shuffle(pieces);
            if(!placePiece(pieces, occupiedSquares, availableSquares, placements)){
                continue;
            }
            currentPieceSquare = placements.get(placements.size()-1).getSquare();
            piecePositions = placements.stream().collect(HashMap::new, (hm,v) -> { hm.put(v.getSquare(), v.getPiece()); }, HashMap::putAll);
            
            Board.newPieceSetup(piecePositions);
            BitSet shadowBoard = (BitSet)Board.getBoard().clone();
            
            ArrayList<Move> calculatedMoves = new ArrayList();
            HashMap<Integer,Piece> shadowPieces = new HashMap(piecePositions);

            Piece p = shadowPieces.get(currentPieceSquare);
            if(p == null){
                throw new Error(currentPieceSquare + " not found");
            }

            try{
                calculateMoves(allMoves, currentPieceSquare, -1, shadowBoard, shadowPieces, (gameTotalTurns+1));
            }
            catch(Error e){
                LOG_HANDLER.flush();
                throw new Error(e);
            }
                
        }
        
        if(allMoves.size() == 0){
            return false;
        }
        
        return true;
       
    }
    
    /**
    * Step 1: Add initial position to stack ((int)current piece square, (int)next piece square, (BitSet) board, (HashMap<int,piece>) pieces/locations, (int) totalmoves to process).
    * Step 2: Iteration (while there are moves to process and the possibilites have not been exhausted).
        Step 1: Pop current position from POSITIONS stack. Pop move that led to this position from MOVES stack.
        Step 2: Shuffle the possible moves the current piece can make.
        Step 3: Iteration throught the possible moves/squares.
            Step 1: - If square is the other piece or the current location, continue
                    - If being attacked by more than than 1 piece, continue
                    - If attacking more than one piece, continue
                    - If not attacking a piece nor being attacked, continue
                    - If attacked-by piece and capturable piece are different, continue
                    - If attacked-by piece and capturable piece are the same, validate the move is not illegal for this game (moving past or onto the piece)
                    - If not attacked, validate move is not illegal for this game (moving past or onto capturable piece)
            Step 2: Generate next move, push current move and next move to MOVES stack
            Step 3: Generate position for next move, push to POSITIONS stack.
    *   @param movesA An empty list that will populate with the moves to be made during the game.
    *   @param activeSquare The square where the first piece to move is placed.
    *   @param otherPieceSquare The square where the other piece (capturing or attacking) is placed.
    *   @param board The board containing the piece locations.
    *   @param pieceLocaions A HashMap mapping squares to pieces.
    *   @param totalMoves The total number of moves to calculate.
    *
    *   
    */
    private void calculateMoves(ArrayList<Move> movesA, int activeSquare, int otherPieceSquare, BitSet board, HashMap<Integer,Piece> pieceLocations, int totalMoves) throws Error {
        
        LOG.setLevel(Level.OFF);
        
        Stack<Object[]> positionStack = new Stack();
        Piece piece = pieceLocations.get(activeSquare);
        ArrayList<Integer> cSquares = piece.getCaptureSquaresArr(activeSquare);
        positionStack.add(new Object[]{activeSquare, otherPieceSquare, board, pieceLocations, cSquares});
//        0 = int activeSquare
//        1 = int otherPieceSquare
//        2 = BitSet board
//        3 = HashMap<Integer,Piece> pieceLocations
        
        while(movesA.size() < totalMoves && positionStack.size() > 0){
            
            Move currentMove = (!movesA.isEmpty()) ? movesA.remove(movesA.size()-1) : null;
            Object[] currentPosition = positionStack.pop();
            activeSquare = (int)currentPosition[0];
            otherPieceSquare = (int)currentPosition[1];
            board = (BitSet)currentPosition[2];
            pieceLocations = (HashMap<Integer,Piece>)currentPosition[3];
            cSquares = (ArrayList)currentPosition[4];
            piece = pieceLocations.get(activeSquare);
            
            LOG.log(Level.FINE, "[New Iteration] activeSquare: {0} otherPieceSquare: {1} board: {2} pieceLocations: {3} piece: {4} cSquares {5}", new Object[]{currentPosition[0], currentPosition[1], currentPosition[2], currentPosition[3], piece.type.toString(), cSquares});
            
            LOG.log(Level.FINEST, "cSquares.size(): {0} isBishop: {1}", new Object[]{cSquares.size(), isBishop(piece)});
            
            Collections.shuffle(cSquares);
            BitSet pl = (BitSet)board.clone();
            pl.flip(activeSquare);
            
            while(cSquares.size() > 0){
                int square = cSquares.remove(0);
                LOG.log(Level.FINEST, " Checking square {0}", new Object[]{square});
                if(square == activeSquare || square == otherPieceSquare){
                    LOG.log(Level.FINEST, " Continuing...");
                    continue;
                }
                BitSet captureSquares = piece.getCaptureSquares(square);
                BitSet c = null;
                boolean isAttacked = false;
                int capturables = 0;
                HashMap<Integer,Piece> pieceLocationsNoActive = new HashMap(pieceLocations);
                pieceLocationsNoActive.remove(activeSquare);
                LOG.log(Level.FINEST, " pieceLocationsNoActive: {0}", pieceLocationsNoActive);
                ArrayList<Integer> attackingPieces = attackedByNum(square, pieceLocationsNoActive);
                int nAttackingPieces = attackingPieces.size();

                if(nAttackingPieces > 1){
                    LOG.log(Level.FINEST, " Attacking pieces larger than 1, continuing...");
                    continue;
                }

                if(nAttackingPieces == 1){
                    isAttacked = true;
                    LOG.log(Level.FINEST, " isAttacked: true");
                }
  
                c = (BitSet)captureSquares.clone();
                c.and(pl);
                //How many other pieces this pieces can capture
                capturables = c.cardinality();
                if( capturables > 1 || (capturables == 0 && !isAttacked)){
                    LOG.log(Level.FINEST, " (capturables > 1 || capturables == 0 && !isAttacked) continuing...");
                    continue;
                }
                
                int capturableSquare = c.nextSetBit(0);

                if(isAttacked){
                    if(capturables == 1){
                        int attackingPieceSquare = attackingPieces.get(0);
                        LOG.log(Level.FINEST, " attacking: {0} attacked by: {1}", new Object[]{capturableSquare, attackingPieceSquare});
                        if(attackingPieceSquare != otherPieceSquare || capturableSquare != otherPieceSquare){
                            LOG.log(Level.FINEST,"  (attackingPieceSquare != otherPieceSquare || capturableSquare != otherPieceSquare) continuing...");
                            continue;
                        }
                        if(piece.checkForCollision(activeSquare, square, otherPieceSquare)){
                            LOG.log(Level.FINEST,"  (p.checkForCollision(activeSquare, square, otherPieceSquare) == true) continuing...");
                            continue;
                        }
                    }
                    else{
                        otherPieceSquare = attackingPieces.get(0);
                        LOG.log(Level.FINEST,"  Attacked only. otherPieceSquare: {0}", otherPieceSquare);
                    }
                }
                else{
                    otherPieceSquare = capturableSquare;
                    Piece pcheck = pieceLocations.get(otherPieceSquare);
                    LOG.log(Level.FINEST,"  otherPieceSquare: {0}", otherPieceSquare);
                    if(pcheck == null){
                        throw new Error("  otherpiece square not found " + otherPieceSquare);
                    }
                    if(piece.checkForCollision(activeSquare, square, otherPieceSquare)){ 
                        LOG.log(Level.FINEST,"  (p.checkForCollision(activeSquare, square, otherPieceSquare) == true) activeSquare: {0} square: {1} otherPieceSquare {2} continuing...", new Object[]{activeSquare, square, otherPieceSquare});
                        continue;
                    }
                }

                if(otherPieceSquare == -1){
                    throw new Error("otherPieceSquare invalid");
                }

                if(currentMove != null){
                    movesA.add(currentMove);
                }
                
                Move nextMove = new Move(activeSquare, square);
                nextMove.otherPieceSquare = otherPieceSquare;
                nextMove.targetSquare = square;
                movesA.add(nextMove);
                
                pl.flip(square);
                Piece otherPiece = pieceLocations.get(otherPieceSquare);
                pieceLocationsNoActive.put(square, pieceLocations.get(activeSquare));
                ArrayList<Integer> cs = otherPiece.getCaptureSquaresArr(otherPieceSquare);
                if(cs.size() == 0 && !isBishop(otherPiece)){
                    throw new Error("otherPieceSquare: " + otherPieceSquare + " " + otherPiece.type.toString());
                }
                else{
                    LOG.log(Level.FINEST, " otherPiece: {0} cs.size(): {1}", new Object[]{otherPiece.type.toString(), cs.size()});
                }
                
                positionStack.add(currentPosition);
                positionStack.add(new Object[]{otherPieceSquare, square, pl, pieceLocationsNoActive, otherPiece.getCaptureSquaresArr(otherPieceSquare)});
                break;
            }
            
        }

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
    
    private boolean checkSquare(int square){
        Move m = allMoves.get(currentTurn);
        
        if(m.getFrom() == square){
            return true;
        }
        return false;
    }

    private int getField(InvisiblePairsMenu.Field field){
        return Integer.parseInt(gameSettings.get(field).getValue());
    }
    
    private void addPieces(Piece.PieceType type, int num, ArrayList<Piece> pieces){
        Piece.PieceType chosenType;
        for(int i = 1; i <= num; i++){
            Piece p = new Piece(type);
            if( isBishop(p)){
                if(i % 2 != 1){
                    BishopData d = (BishopData)p.pieceData;
                    d.setLightSquares(false);
                }
            }
            pieces.add(p);
        }
    }
    
    //Returns list of indexes where the bits are not set.
    private ArrayList<Integer> getPoolFromBitSet(BitSet bitset, boolean getTrue){
        ArrayList<Integer> pool = new ArrayList();
        for(int i = 0; i < bitset.size(); i++){
            if(bitset.get(i) == getTrue){
                pool.add(i);
            } 
        }
        return pool;
    }
    
    /**
     * Step 1) While there are pieces to place
     *  Step 1) Select a piece.
     *  Step 2) Iterate through the available squares.
     *      - If not the last piece, then:
     *          Step 1) If the square is legal (concerning bishops), place piece if square is either attacking or attacked.
     *          Step 2) Update the board
     *          Step 3) Try to place the next piece (recursive call). If fail, update the board and try the next square
     *      - If the last piece, then:
     *          Step 1) If the piece is the correct color, and is attacking one other piece or being attacked by one other piece, place piece.
     * @param pieces
     * @param occupiedSquares
     * @param availableSquares
     * @param placements
     * @return 
     */
    //Remove next piece from array
    //Place it on unoccupied and unattacked/attacking square
    //Place last piece on attacked/attacking square
    private boolean placePiece(ArrayList<Piece> pieces, BitSet occupiedSquares, BitSet availableSquares, Stack<Placement> placements){
   
        ArrayList<Piece> updatedPieces = new ArrayList(pieces);
        BitSet updatedOccupiedSquares = (BitSet)occupiedSquares.clone();
        Piece piece = updatedPieces.remove(0);
        ArrayList<Integer> squarePool = getPoolFromBitSet(availableSquares, true);
        Collections.shuffle(squarePool);
        
        if(updatedPieces.size() > 0){
            for(int square : squarePool){
                if(!isPieceWCorrectColor(piece, square)){
                    continue;
                }
                
                if(piece.isSquareEmptyAndNotAttacking(square, occupiedSquares)){
                    //Is the square being attacked?
                    ArrayList<Integer> aAttackedBy = attackedByNum(square, placements);
                    if(aAttackedBy.size() > 0){//If so, move on to the next square
                        continue;
                    }
                    
                    placements.push(new Placement(square,piece));
                    updatedOccupiedSquares.set(square);
                    BitSet updatedAvailableSquares = (BitSet)availableSquares.clone();
                    updatedAvailableSquares.xor(piece.getCaptureSquares(square));
                    
                    if(!placePiece(updatedPieces, updatedOccupiedSquares, updatedAvailableSquares, placements)){
                        placements.pop();
                        updatedOccupiedSquares.set(square, false);
                        continue;
                    }
                    
                    
                    return true;
                }
            }
            return false;
        }
        else{
            ArrayList<Integer> unoccupiedSquares = getPoolFromBitSet(updatedOccupiedSquares, false);
            Collections.shuffle(unoccupiedSquares);
            boolean done = false;
            for(int square : unoccupiedSquares){
                
                //If there is more than one bishop, alternate the squares the bishop uses i.e. dark squares/light squares
                if(!isPieceWCorrectColor(piece, square)){
                    continue;
                }
            
                ArrayList<Integer> aAttackedBy = attackedByNum(square, placements);
                int nAttacking = piece.getNumberAttacking(square, occupiedSquares);
                
                if(nAttacking == 1 && aAttackedBy.size() == 0){
                    done = true;
                }
                if(nAttacking == 0 && aAttackedBy.size() == 1){
                    done = true;
                }
                if(nAttacking == 1 && aAttackedBy.size() == 1){
                    ArrayList<Integer> attacking = piece.getPiecesAttacking(square, occupiedSquares);
                    if(attacking.get(0) == aAttackedBy.get(0)){
                        done = true;
                    }
                }
                if(done){
                    placements.push(new Placement(square, piece));
                    return true;
                }
            }
            return false;
        }
    }
    
    private boolean isPieceWCorrectColor(Piece piece,int square){
        if(isBishop(piece)){
            BishopData d = (BishopData)piece.pieceData;
            int file = Math.floorDiv(square, 8);
            int rank = square % 8;
            
            if(d.isLightSquared()){
                if( (file % 2 == 0 && rank % 2 == 0) || 
                    (file % 2 == 1 && rank % 2 == 1) ){
                    return true;
                }
            }
            else{
                if( (file % 2 == 0 && rank % 2 == 1) || 
                    (file % 2 == 1 && rank % 2 == 0) ){
                    return true;
                }
            }
            return false;
  
        }
        return true;
    }
    
    /**
     * Used for debugging.
     */
    private void autoMove(){
        if(currentTurn == allMoves.size()){
            end(true);
        }
        else{
            move(finishedMoving);
        }
    }

    private ArrayList<Integer> attackedByNum(int square, HashMap<Integer, Piece> pieceLocations){
        ArrayList<Integer> attackedBy = new ArrayList();
        boolean dark = false;
        for(Map.Entry<Integer,Piece> e : pieceLocations.entrySet()){
            Piece p = e.getValue();
            BitSet captureSquares = p.getCaptureSquares(e.getKey());
            if(captureSquares.get(square)){
                attackedBy.add(e.getKey());
            }
        }
        return attackedBy;
    }
    
    private ArrayList<Integer> attackedByNum(int square, Stack placements){

        ArrayList<Integer> attackedBy = new ArrayList();
        
        for(int i = 0; i < placements.size(); i++){
            Placement placed = (Placement)placements.get(i);
            Piece p = placed.getPiece();
            BitSet captureSquares = p.getCaptureSquares(placed.getSquare());
            
            if(captureSquares.get(square)){
                attackedBy.add(placed.getSquare());
            }
        }
        return attackedBy;
        
    }

    private boolean isBishop(Piece p){
        if(p.type.toString().contains("BISHOP")){
            return true;
        }
        return false;
    }
    
    private void move(Runnable finished){
        Move m = allMoves.get(currentTurn);
        if(Board.getPiece(m.getFrom()) == null){
            throw new Error("no piece at: " + m.getFrom() + " supposed to move to : " + m.getTo() + " turn: " + currentTurn);
        }
        Board.movePiece(m.getFrom(), m.getTo(), finished);
    }
    
    public void end(boolean win){
        Board.show();
        Alert a = new Alert(Alert.AlertType.NONE, "Start again with same settings?");
        a.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        
        if(!win){
            Board.highlightSquare(allMoves.get(currentTurn).getFrom());
            a.setTitle("Game Over!");
        }
				else{
					a.setTitle("You Win!");
				}

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
        
        currentState = GameState.FINISHED;
    }

    public GridPane getMenuUI(){
        return gridLayout;
    }

    public void addDialogButtons(Button ...buttons){
       gridLayout.addColumn(0, buttons[0]);
       gridLayout.addColumn(1, buttons[1]);
    }

}
