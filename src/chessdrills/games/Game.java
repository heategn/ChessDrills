package chessdrills.games;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public interface Game{
    void start();
    void restart();
    Pane getMenuUI();
    void addDialogButtons(Button... buttons);
    void squareClicked(MouseEvent evt);
    void pieceClicked(MouseEvent evt);
    String getInstructions();
}
