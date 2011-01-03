package checkers.core;

public class Multiplayer extends SinglePlayer {
	
	private int piece;
	public Multiplayer(GameRepresentation g){
		super(g);
		piece=GameRepresentation.BLACK;
	}
	
	public boolean makeMove(){
		//Aici trebuie transmis sirul userMove catre celalat jucator iar cand primeste 
		//sirul(deci in metoda de recive) jucatorul trebui sa faca un 
		//game.move(userMove[0], userMove[1], userMove[2], userMove[3]) pentru a se modifica
		//matricea de joc
		return game.move(userMove[0], userMove[1], userMove[2], userMove[3]);
		
	}
	
	public int getPiece(){
		return piece;
	}
}
