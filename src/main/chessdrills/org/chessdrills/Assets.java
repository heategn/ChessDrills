package org.chessdrills;

import javafx.scene.image.Image;
import java.io.InputStream;

import org.chessdrills.pieces.Piece;

public class Assets{
	
	public static Image PAWN_LIGHT;
	public static Image PAWN_DARK;
	public static Image BISHOP_LIGHT;
	public static Image BISHOP_DARK;
	public static Image KNIGHT_LIGHT;
	public static Image KNIGHT_DARK;
	public static Image ROOK_LIGHT;
	public static Image ROOK_DARK;
	public static Image QUEEN_LIGHT;
	public static Image QUEEN_DARK;
	public static Image KING_LIGHT;
	public static Image KING_DARK;
	public static Image EXIT;

	public static String assetDir = "/org/chessdrills/assets/";

	public static void loadAssets(){

			PAWN_LIGHT = loadImage("wP.png");
			PAWN_DARK = loadImage("bP.png");
			BISHOP_LIGHT = loadImage("wB.png");
			BISHOP_DARK = loadImage("bB.png");
			KNIGHT_LIGHT = loadImage("wN.png");
			KNIGHT_DARK = loadImage("bN.png");
			ROOK_LIGHT = loadImage("wR.png");
			ROOK_DARK = loadImage("bR.png");
			QUEEN_LIGHT = loadImage("wQ.png");
			QUEEN_DARK = loadImage("bQ.png");
			KING_LIGHT = loadImage("wK.png");
			KING_DARK = loadImage("bK.png");
			EXIT = loadImage("exit.png");

	}

	private static Image loadImage(String type){
		String fullType = Assets.assetDir + type;	
		return new Image(Assets.class.getResourceAsStream(fullType));
	}

}
