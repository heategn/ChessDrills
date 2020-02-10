package org.chessdrills;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessDrills extends Application {
    
		public static boolean testMode = false;
		public static String testToRun;

    @Override
    public void start(Stage stage) throws Exception {
      
        Parent root = FXMLLoader.load(getClass().getResource("ChessDrills.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("ChessDrills.css").toString());
        stage.setScene(scene);
        stage.setTitle("ChessDrills");
        stage.show();
    }

    public static void main(String[] args) {

				if(args.length > 0 && args[0] == "testMode"){
					testMode = true;
					testToRun = args[1];
				}

        launch(args);
    }
    
}
