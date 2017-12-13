package chessdrills;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessDrills extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
      
        Parent root = FXMLLoader.load(getClass().getResource("ChessDrills.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/chessdrills/ChessDrills.css");
        stage.setScene(scene);
        stage.setTitle("ChessDrills");
        stage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}