package org.chessdrills.games;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public interface Game{
    void start();
    void restart();
    Pane getMenuUI();
    void addDialogButtons(Button... buttons);
    void squareClicked(MouseEvent evt);
    void squareReleased(MouseEvent evt);
    void squarePressed(MouseEvent evt);
    void mouseDragged(MouseEvent evt);
    void mousePressed(MouseEvent evt);
    void mouseReleased(MouseEvent evt);
    void pieceClicked(MouseEvent evt);
    void pieceDragged(MouseEvent evt);
    void pieceReleased(MouseEvent evt);
    String getInstructions();
}
