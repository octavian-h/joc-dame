package checkers.ai;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import checkers.core.GameRepresentation;

public class LegalMove extends RecursiveAction
{
	private static final long serialVersionUID = -967827141717646834L;
	private ArrayList<ComputerMove> legal;
	private GameRepresentation game;

	public LegalMove(GameRepresentation g)
	{
		game = g;
		legal = new ArrayList<ComputerMove>();
	}

	public void compute()
	{
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				if (game.getSlotState(i, j) == GameRepresentation.BLACK
						|| game.getSlotState(i, j) == GameRepresentation.BLACK_KING)
				{
					if (game.canMove(i, j, i + 1, j - 1)) 
						legal.add(new ComputerMove(i, j, i + 1, j - 1));
					if (game.canMove(i, j, i + 1, j + 1)) 
						legal.add(new ComputerMove(i, j, i + 1, j + 1));
					if (game.canMove(i, j, i - 1, j - 1))
						legal.add(new ComputerMove(i, j, i - 1, j - 1));
					if (game.canMove(i, j, i - 1, j + 1)) 
						legal.add(new ComputerMove(i, j, i - 1, j + 1));
				}
	}

	public ArrayList<ComputerMove> getLegalMoves()
	{
		if (legal.isEmpty()) return null;
		else return legal;
	}
}
