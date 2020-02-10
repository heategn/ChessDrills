package org.chessdrills.menus;

import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.chessdrills.games.GameManager;
import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;
import org.chessdrills.games.Game;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.geometry.Insets;

/**
 *
 * Container class for displaying specific game menus.
 */
public class StartGameMenu extends StackPane{
        
    private String startStr = "Start";
    private String closeStr = "Close";
    private FadeTransition tt;
    private VBox content = new VBox();
    private Text tooltip = new Text();
    private StartGameMenu main = this;
    private double width = 400;
    private double height = 500;
    private Insets margin = new Insets(20);
    
    private class StartGameMenuEvent extends ActionEvent{}
    
    public StartGameMenu(){
        tt  = new FadeTransition(Duration.millis(300), this);
        tooltip.setWrappingWidth(width);
        content.setPrefHeight(height);
        content.setPrefWidth(width);
        content.setMaxHeight(height);
        content.setMaxWidth(width);
        content.setAlignment(Pos.CENTER);
        content.setId("game-menu-content");
        addContentWithMargin(tooltip);
        this.getChildren().add(content);
    }
    
    private void addContentWithMargin(Node n){
        content.getChildren().add(n);
        content.setMargin(n, margin);
    }
    
    private void updateGameMenu(Game ui){
        tooltip.setText(ui.getInstructions());
        addContentWithMargin(ui.getMenuUI());
    }
    
    public void show( Runnable callback ){
        updateGameMenu(GameManager.getGame());
        tt.setFromValue(0);
        tt.setToValue(1);
        tt.setOnFinished((evt) -> { 
            callback.run(); 
        });
        this.setVisible(true);
        tt.play();
   }
    
    public void hide( Runnable callback ){
        tt.setOnFinished((evt) -> { this.setVisible(false); this.content.getChildren().remove(1); callback.run(); });
        tt.setToValue(0);
        tt.play();
    }
    
}
