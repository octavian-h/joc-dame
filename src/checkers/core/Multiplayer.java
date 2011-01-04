package checkers.core;

public class Multiplayer {
	
	private int piece;
	private GameRepresentation game;//model of the game
	private int[] userMove;
	
	public Multiplayer(GameRepresentation g){
		game=g;
		piece=GameRepresentation.BLACK;
	}
	
	public void getUserInput(int[] uM){
		userMove=uM;
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
	
	public void setPiece(int p){
		piece=p;
	}
}
