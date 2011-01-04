package checkers.core;

import checkers.gui.Board;

public class Multiplayer {
	
	private int piece;
	private GameRepresentation game;//model of the game
	private int[] userMove;
	private  Board brd;
	
	public Multiplayer(GameRepresentation g,Board b, int p){
		game=g;
		brd=b;
		piece=p;
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
	
	public boolean convertMessageToUserMove(String message){
		int j=0;
		if(message.charAt(0)=='m'){
			for(int i=1;i<9;i++)
				if(Character.isDigit(message.charAt(i))){
					userMove[j]=Character.digit(message.charAt(i), 10);
					j++;
					}
			return true;
		}
		else
			return false;
				
		
	}
	
	public String convertUserMoveToString(){
		StringBuffer buf=new StringBuffer("m");
		for(int i=0;i<4;i++)
			buf.append(" "+userMove[i]);
		return buf.toString();
	}
}
