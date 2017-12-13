package chessdrills.games;

import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

public class RunningGameInfo {
    private static VBox gameInfoPane;
    public static void setGameInfo(Pane gameInfo){
        gameInfoPane = (VBox)gameInfo;
    }
    
    public static Pane getGameInfo(){
        if(gameInfoPane == null){
            throw new Error("gameInfoPane is null");
        }
        return gameInfoPane;
    }
}
