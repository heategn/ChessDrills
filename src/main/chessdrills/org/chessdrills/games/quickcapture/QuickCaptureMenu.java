/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chessdrills.games.quickcapture;

import org.chessdrills.games.GameMenu;
import org.chessdrills.ChessDrillsController;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.beans.property.SimpleStringProperty;
import java.util.HashMap;
/**
 * Provides options for the number of pieces to place on the board and the number of rounds.
 */
public class QuickCaptureMenu extends GameMenu{
  
    private double Hgap = 15;
    private double Vgap = 15;
    public static enum Field {ROUNDS, ROOKS, KNIGHTS, BISHOPS, QUEENS};
    public static String roundsStr = "Number of rounds: ";
    public static String startStr = "Start";
    public static String closeStr = "Close";
    public static String rooksStr = "Rooks";
    public static String knightsStr = "Knights";
    public static String bishopsStr = "Bishops";
    public static String queensStr = "Queens";
    private String[] rndOptions = {"5", "10", "15", "20", "30"};
    private GridPane layout;
    private HashMap<Field, SimpleStringProperty> gameSettings;
    
    public static HashMap<Field, String> optionLabels = new HashMap<Field, String>();
    
    public QuickCaptureMenu(GridPane gridLayout, ChessDrillsController cc, HashMap<Field, SimpleStringProperty> gameSettings){
        
        super();

        optionLabels.put(Field.BISHOPS, bishopsStr);
        optionLabels.put(Field.KNIGHTS, knightsStr);
        optionLabels.put(Field.ROOKS, rooksStr);
        optionLabels.put(Field.QUEENS, queensStr);
        optionLabels.put(Field.ROUNDS, roundsStr);


        for(Field k : QuickCaptureMenu.optionLabels.keySet()){
            SimpleStringProperty si = new SimpleStringProperty();
            gameSettings.put(k, si);
        }

        this.gameSettings = gameSettings;

        layout = gridLayout;
        gridLayout.setVgap(15);
        gridLayout.setHgap(15);

				addGameChoices(Field.ROOKS, rooksStr);
				addGameChoices(Field.KNIGHTS, knightsStr);
				addGameChoices(Field.BISHOPS, bishopsStr);
				addGameChoices(Field.QUEENS, queensStr);
				addGameChoices(Field.ROUNDS, rndOptions, "15", roundsStr);

        EventHandler[] handlers = cc.getMenuHandlers();
        Button startGameButton = new Button(startStr);
        startGameButton.setOnAction(handlers[0]);
        Button closeButton = new Button(closeStr);
        closeButton.setOnAction(handlers[1]);
        gridLayout.addColumn(0, startGameButton);
        gridLayout.addColumn(1, closeButton);

        gridLayout.setAlignment(Pos.CENTER);

    }
    
    public GridPane getLayout(){
        return layout;
    }
    
    private void addGameChoices(Field pieceType, String id){
        String[] options = new String[2];
        options[0] = "1";
        options[1] = "2";
        addGameChoices(pieceType, options, "2", id);
    }

    private void addGameChoices(Field pieceType, String[] options, String selected, String id){
        TilePane tb = new TilePane();
        tb.setHgap(10);
        tb.setVgap(10);
        tb.setPrefColumns(2);
        tb.setMinWidth(width);
        tb.setAlignment(Pos.CENTER);
        ChoiceBox cb = new ChoiceBox();

        for(String s : options){
            cb.getItems().add(s);
        }

        cb.setValue(selected);
        gameSettings.get(pieceType).bind(cb.valueProperty());

        cb.setId(id);
        Label lbl = new Label(optionLabels.get(pieceType));
        layout.addColumn(0, lbl);
        layout.addColumn(1, cb);
   }
        
}
