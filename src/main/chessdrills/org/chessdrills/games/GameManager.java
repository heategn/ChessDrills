package org.chessdrills.games;

import org.chessdrills.games.Game;
import org.chessdrills.board.Board;
import org.chessdrills.ChessDrillsController;
import org.chessdrills.games.invisiblepairs.*;
import org.chessdrills.games.quickcapture.*;
import java.util.HashMap;
import javafx.scene.input.MouseEvent;

/**
 *
 *  Load and unloads a game type. Passes user-events through to the loaded game and handles other administration tasks.
 */
public class GameManager {
    
    private static Game game;
    public static Boolean gameRunning = false;
		public static enum GameType {INVISIBLE_PAIRS, QUICK_CAPTURE };
    
    private static HashMap<GameType, Game> gameCache = new HashMap();
    
    public static Game getGame(){
        if(game == null){
            throw new RuntimeException("Game not loaded");
        }
        return game;
    }
    
    public static boolean newGame(GameType gameType, ChessDrillsController cc){
        
        if(gameRunning == true){
					return false;
        }
        
        game = gameCache.get(gameType);
        
        if(game == null){
            switch(gameType){
                case INVISIBLE_PAIRS:
                    game = new InvisiblePairs(cc);
                    gameCache.put(GameType.INVISIBLE_PAIRS, game);
                    break;
                case QUICK_CAPTURE:
										game = new QuickCapture(cc);
                    gameCache.put(GameType.QUICK_CAPTURE, game);
                    break;
				    }

        }

				return true;

    }
    
    public static void squareClicked(MouseEvent evt){
        if(gameRunning){
            game.squareClicked(evt);
        }
    }

    public static void squareReleased(MouseEvent evt){
        if(gameRunning){
            game.squareReleased(evt);
        }
    }

    public static void squarePressed(MouseEvent evt){
        if(gameRunning){
            game.squarePressed(evt);
        }
    }
  
    public static void pieceClicked(MouseEvent evt){
        if(gameRunning){
            game.pieceClicked(evt);
        }
    }

		public static void mouseDragged(MouseEvent evt){
        if(gameRunning){
            game.mouseDragged(evt);
        }
    }

		public static void mousePressed(MouseEvent evt){
        if(gameRunning){
            game.mousePressed(evt);
        }
    }

    public static void mouseReleased(MouseEvent evt){
        if(gameRunning){
            game.mouseReleased(evt);
        }
    }

    public static void pieceDragged(MouseEvent evt){
        if(gameRunning){
            game.pieceDragged(evt);
        }
    }
 
    public static void pieceReleased(MouseEvent evt){
        if(gameRunning){
            game.pieceReleased(evt);
        }
    }
  
    public static void startGame(){
        if(game == null){
            throw new RuntimeException("Game not loaded!");
        }
        //todo: db stuff
        gameRunning = true;
        game.start();
    }
    
    public static void endGame(){
        //todo: save stuff
        game = null;
        gameRunning = false;
    }
    
}
