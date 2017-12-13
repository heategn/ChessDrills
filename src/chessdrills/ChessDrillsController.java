package chessdrills;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

import chessdrills.board.Board;
import chessdrills.games.GameManager;
import chessdrills.exceptions.gamemanager.GameRunningException;
import chessdrills.menus.StartGameMenu;
import chessdrills.games.RunningGameInfo;

/**
 * Main controller class than routes events to respective modules.
 */
public class ChessDrillsController implements Initializable {
    
    public static enum ChessDrillsAlertType {GAMEISRUNNING,CUSTOM}
    
    @FXML
    protected GridPane main_grid;
    
    @FXML
    protected ColumnConstraints canvas_col_cell;
    
    @FXML
    protected RowConstraints canvas_row_cell;

    @FXML
    protected StartGameMenu gameMenu;
    
    @FXML
    protected Pane game_info;
    
    @FXML
    protected Button quit_btn;
    
    @FXML
    private AnchorPane main_pane;
    
    @FXML
    private VBox side_nav;
    
    private boolean _running = false;
    private GridPane drawing;
    
    private static Alert alertBox = new Alert(Alert.AlertType.CONFIRMATION);
    private final String sVersion = "ChessDrills version 0.5 alpha";
    private final String sInfo = "\nChessDrills is a suite of mini-games designed for improving your chess.\n\nGitHub: github.com/heategn/java/chessdrills/ <brsimm3@gmail.com>\n\nBackground image credit: https://www.flickr.com/people/colinsite\n\n";
    private final String sLicense = "This is free and unencumbered software released into the public domain.\n" +
"\n" +
"Anyone is free to copy, modify, publish, use, compile, sell, or\n" +
"distribute this software, either in source code form or as a compiled\n" +
"binary, for any purpose, commercial or non-commercial, and by any\n" +
"means.\n" +
"\n" +
"In jurisdictions that recognize copyright laws, the author or authors\n" +
"of this software dedicate any and all copyright interest in the\n" +
"software to the public domain. We make this dedication for the benefit\n" +
"of the public at large and to the detriment of our heirs and\n" +
"successors. We intend this dedication to be an overt act of\n" +
"relinquishment in perpetuity of all present and future rights to this\n" +
"software under copyright law.\n" +
"\n" +
"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,\n" +
"EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF\n" +
"MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.\n" +
"IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR\n" +
"OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,\n" +
"ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR\n" +
"OTHER DEALINGS IN THE SOFTWARE.\n" +
"\n" +
"For more information, please refer to <http://unlicense.org>";
    
    private Alert infoBox;
    
    
    public EventHandler<ActionEvent> startClicked = new EventHandler<ActionEvent>(){
        @Override public void handle(ActionEvent evt){
            gameMenu.hide(new Runnable(){
                @Override
                public void run(){
                    GameManager.startGame();
                }
            });
        }
    };
    
    public EventHandler<ActionEvent> closeClicked = new EventHandler<ActionEvent>(){
        @Override public void handle(ActionEvent evt){
            gameMenu.hide(hideMenu);
        }
    };
    
    public static EventHandler<MouseEvent> squareClicked = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.squareClicked(evt);
       }
    };
    
    public static EventHandler<MouseEvent> pieceClicked = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.pieceClicked(evt);
       }
    };
    
    public Runnable showMenu = new Runnable(){
        @Override public void run(){}
    };
    
    public Runnable hideMenu = new Runnable(){
        @Override public void run(){}
    };
    
    private EventHandler[] handlers = { startClicked, closeClicked };

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        
        RunningGameInfo.setGameInfo(game_info);
        
        drawing = new GridPane();
        drawing.setPadding(new Insets(0, 15, 0, 0));
        main_grid.add(drawing, 1, 0);
       
        AnchorPane.setRightAnchor(main_grid, 15d);
        AnchorPane.setLeftAnchor(main_grid, 15d);
        AnchorPane.setTopAnchor(main_grid, 15d);
        AnchorPane.setBottomAnchor(main_grid, 15d);

        this.main_grid.layoutBoundsProperty().addListener((newV)->Board.resize(main_grid, side_nav));
        
        ImageView quit_img = new ImageView(new Image("chessdrills/assets/exit.png"));
        quit_btn.setGraphic(quit_img);
        
        Board.initialize(drawing, main_grid);
        
    }    
    
    public static void showAlert(ChessDrillsAlertType type, String... s){
        switch(type){
            case GAMEISRUNNING:
                ChessDrillsController.alertBox.setContentText("A game is still running. Continue?");
                ChessDrillsController.alertBox.setAlertType(Alert.AlertType.CONFIRMATION);
                ChessDrillsController.alertBox.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> GameManager.endGame());
                break;

            case CUSTOM:
                ChessDrillsController.alertBox.setAlertType(Alert.AlertType.ERROR);
                ChessDrillsController.alertBox.setContentText(s[0]);
                ChessDrillsController.alertBox.showAndWait();
                break;
        }
    }

    public EventHandler[] getMenuHandlers(){
        return handlers;
    }
    
    /*
    * Used for debugging. Useful in situations when a player needs to be simulated i.e. making a turn.
    */
    public void triggerEvent(){
        invisible_pairs_clicked(new Event(EventType.ROOT));
        GameManager.startGame();
        gameMenu.hide(hideMenu);
    }
    
    private void showInfoBox(){
        
        if(infoBox == null){
            infoBox = new Alert(Alert.AlertType.NONE);
            infoBox.setHeaderText(sVersion);
            infoBox.getDialogPane().setContentText(sInfo);
            infoBox.getButtonTypes().addAll(ButtonType.CLOSE);
            Text t = new Text(sInfo);
            Text l = new Text(sLicense);
            TextArea p = new TextArea();
            p.setEditable(false);
            p.setText(sLicense);
            p.setMinHeight(300);
            VBox vb = new VBox();
            vb.getChildren().addAll(t,p);
            infoBox.getDialogPane().setContent(vb);
            
        }
        
        infoBox.getDialogPane().setExpanded(true);
        infoBox.showAndWait().filter(response -> response == ButtonType.CLOSE).ifPresent(response -> alertBox.close());
    }
    
    @FXML
    protected void invisible_pairs_clicked(Event evt){
        
        try{
            if(!GameManager.gameRunning){
                GameManager.newGame(GameManager.GameType.INVISIBLE_PAIRS, this);
                gameMenu.show(showMenu);
            }
        }
        catch(GameRunningException e){
            showAlert(ChessDrillsAlertType.GAMEISRUNNING);
        }
        catch(Exception e){
            throw e;
        }

    }
    
    @FXML 
    protected void quit_clicked(Event evt){
        Platform.exit();
    }
    
    @FXML
    protected void about_clicked(Event evt){
        showInfoBox();
    }
    
}