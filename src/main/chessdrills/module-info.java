module chessdrills {

	requires java.logging;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	
	opens org.chessdrills to javafx.fxml,javafx.graphics;
	exports org.chessdrills.menus to javafx.fxml;

	exports org.chessdrills;
	exports org.chessdrills.pieces;
	exports org.chessdrills.games.invisiblepairs;
	exports org.chessdrills.games.quickcapture;
	exports org.chessdrills.board;
	exports org.chessdrills.util;

}
