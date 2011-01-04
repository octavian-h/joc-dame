package checkers.core;

import checkers.ai.Computer;

public class SinglePlayer {
	
	private GameRepresentation game;//model of the game
	private int[] userMove;
	
	
	public SinglePlayer(GameRepresentation g){
		
		game=g;
		userMove=new int[4];
	}
	
	public void getUserInput(int[] uM){
		userMove=uM;
	}
	
	public boolean makeMove(){
	
		return game.move(userMove[0], userMove[1], userMove[2], userMove[3]);
		
	}
}
