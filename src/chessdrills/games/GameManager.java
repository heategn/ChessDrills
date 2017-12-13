package chessdrills.games;

import chessdrills.games.Game;
import chessdrills.board.Board;
import chessdrills.exceptions.gamemanager.GameRunningException;
import chessdrills.ChessDrillsController;
import chessdrills.games.invisiblepairs.InvisiblePairs;
import java.util.HashMap;
import javafx.scene.input.MouseEvent;

/**
 *
 *  Load and unloads a game type. Passes user-events through to the loaded game and handles other administration tasks.
 */
public class GameManager {
    
    private static Game game;
    public static Boolean gameRunning = false;
    public static enum GameType { INVISIBLE_PAIRS };
    
    private static HashMap<GameType, Game> gameCache = new HashMap();
    
    public static Game getGame(){
        if(game == null){
            throw new RuntimeException("Game not loaded");
        }
        return game;
    }
    
    public static void newGame(GameType gameType, ChessDrillsController cc) throws GameRunningException{
        
        if(gameRunning == true){
            throw new GameRunningException();
        }
        
        game = gameCache.get(gameType);
        
        if(game == null){
            switch(gameType){
                case INVISIBLE_PAIRS:
                    game = new InvisiblePairs(cc);
                    gameCache.put(GameType.INVISIBLE_PAIRS, game);
                    break;
                default:
                    throw new RuntimeException("Game type not found");
            }
        }
        
    }
    
    public static void squareClicked(MouseEvent evt){
        if(gameRunning){
            game.squareClicked(evt);
        }
    }
    
    public static void pieceClicked(MouseEvent evt){
        if(gameRunning){
            game.pieceClicked(evt);
        }
    }
    
    public static void startGame(){
        if(game == null){
            throw new RuntimeException("Game not loaded!");
        }
        //db stuff
        Board.setGameClient(game);
        gameRunning = true;
        game.start();
    }
    
    public static void endGame(){
        //save stuff
        game = null;
        gameRunning = false;
        Board.removeGameClient();
    }
    
}