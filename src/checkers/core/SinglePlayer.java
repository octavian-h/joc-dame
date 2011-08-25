package checkers.core;

public class SinglePlayer
{
	private GameRepresentation game;// model of the game
	private int[] userMove;

	public SinglePlayer()
	{

		userMove = new int[4];
	}

	public void setGame(GameRepresentation g)
	{
		game = g;
	}

	public void getUserInput(int[] uM)
	{
		userMove = uM;
	}

	public boolean makeMove()
	{
		return game.move(userMove[0], userMove[1], userMove[2], userMove[3]);
	}
}
