package checkers.core;

import checkers.ai.Computer;

public class SinglePlayer {
	
	protected GameRepresentation game;//model of the game
	protected int[] userMove;
	
	public SinglePlayer(){
		userMove=new int[4];
	}
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
