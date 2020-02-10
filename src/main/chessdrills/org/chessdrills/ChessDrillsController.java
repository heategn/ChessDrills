package org.chessdrills;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

import org.chessdrills.board.Board;
import org.chessdrills.games.GameManager;
import org.chessdrills.exceptions.gamemanager.GameRunningException;
import org.chessdrills.menus.StartGameMenu;
import org.chessdrills.games.RunningGameInfo;
import org.chessdrills.Assets;
import org.chessdrills.ChessDrills;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.chessdrills.pieces.Piece;

/**
 * Main controller class than routes events to respective modules.
 */
public class ChessDrillsController implements Initializable {
    
    public static enum ChessDrillsAlertType {GAMEISRUNNING,GAMENOTFOUND,CUSTOM}

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
    protected Button about_btn;
    
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
    
    private Dialog infoBox;
    
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

    public static EventHandler<MouseEvent> squareReleased = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.squareReleased(evt);
       }
    };

    public static EventHandler<MouseEvent> squarePressed = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.squarePressed(evt);
       }
    };

    public static EventHandler<MouseEvent> pieceClicked = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.pieceClicked(evt);
       }
    };

    public static EventHandler<MouseEvent> pieceDragged = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.pieceDragged(evt);
       }
    };

    public static EventHandler<MouseEvent> mouseDragged = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.mouseDragged(evt);
       }
    };

    public static EventHandler<MouseEvent> mousePressed = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.mousePressed(evt);
       }
    };

    public static EventHandler<MouseEvent> mouseReleased = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.mouseReleased(evt);
       }
    };

		public static EventHandler<MouseEvent> pieceReleased = new EventHandler<MouseEvent>(){
       @Override public void handle(MouseEvent evt){
           GameManager.pieceReleased(evt);
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

				prepInfoBox();
				Assets.loadAssets();

				ImageView quit_img = new ImageView(Assets.EXIT);
				quit_btn.setGraphic(quit_img);
				Board.initialize(drawing, main_grid);
				drawing.setOnMouseDragged(ChessDrillsController.mouseDragged);
				drawing.setOnMousePressed(ChessDrillsController.mousePressed);
				drawing.setOnMouseReleased(ChessDrillsController.mouseReleased);

       	if(ChessDrills.testMode){
					triggerEvent_debug(ChessDrills.testToRun);
				}

    }    
    
		public static void showAlert(ChessDrillsAlertType type, String... s){
			
        switch(type){
            case GAMEISRUNNING:
                alertBox.setContentText("A game is still running. Continue?");
								alertBox.setAlertType(Alert.AlertType.CONFIRMATION);
                alertBox.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> GameManager.endGame());
                break;

            case GAMENOTFOUND:
								alertBox.setContentText("Game type not found.");
								alertBox.setAlertType(Alert.AlertType.ERROR);
								alertBox.showAndWait();
                break;

            case CUSTOM:
                alertBox.setContentText(s[0]);
								alertBox.setAlertType(Alert.AlertType.INFORMATION);
                alertBox.showAndWait();
                break;
        }
    }

    public EventHandler[] getMenuHandlers(){
        return handlers;
    }
    
    /*
    * Used for debugging. Useful in situations when a player needs to be simulated i.e. making a turn.
    */
		public static void exitApp_debug(){
			Platform.exit();
		}

    public void triggerEvent_debug(String type){

			switch(type){
				case "invisiblepairs":
					invisible_pairs_clicked(new Event(EventType.ROOT));
					GameManager.startGame();
					gameMenu.hide(hideMenu);
					break;

			case "quickcapture":
					quick_capture_clicked(new Event(EventType.ROOT));
					GameManager.startGame();
					gameMenu.hide(hideMenu);
					break;
			}

    }

		private void prepInfoBox(){

				infoBox = new Alert(Alert.AlertType.INFORMATION);
				infoBox.getDialogPane().setExpanded(true);
				infoBox.setHeaderText(sVersion);
				Text t = new Text(sInfo);
				Text l = new Text(sLicense);
				TextArea p = new TextArea();
				p.setEditable(false);
				p.setText(sLicense);
				p.setMinHeight(300);
				VBox vb = new VBox();
				infoBox.getDialogPane().setContent(vb);
				vb.getChildren().addAll(t,p);

		}

    private void showInfoBox(){
				infoBox.showAndWait();
    }
    
		private void loadGame(GameManager.GameType gameType){

				if(!GameManager.gameRunning){
					if(GameManager.newGame(gameType, this)){
						gameMenu.show(showMenu);
					}
					else{
						showAlert(ChessDrillsAlertType.GAMENOTFOUND);
					}
				}
				else{
					showAlert(ChessDrillsAlertType.GAMEISRUNNING);
				}

				GameManager.newGame(gameType, this);

		}

    @FXML
    protected void invisible_pairs_clicked(Event evt){
			loadGame(GameManager.GameType.INVISIBLE_PAIRS);        
    }

    @FXML
    protected void quick_capture_clicked(Event evt){
      loadGame(GameManager.GameType.QUICK_CAPTURE); 
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
