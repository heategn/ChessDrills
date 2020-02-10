package org.chessdrills.games;

import javafx.scene.layout.Pane;

public abstract class GameMenu {
        protected double width = 400;
        protected double Hgap = 15;
        protected double Vgap = 15;

        public abstract Pane  getLayout();
}
