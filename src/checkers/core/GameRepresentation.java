package checkers.core;

public class GameRepresentation
{
	private int[][] matrix;
	public static final int RED = 1;
	public static final int BLACK = -1;
	public static final int BLACK_KING = -3;
	public static final int RED_KING = 3;
	public static final int EMPTY_BLUE = 0;
	public static final int EMPTY_WHITE = 2;

	private int turn;

	public GameRepresentation()
	{
		int lineColor = 0;
		matrix = new int[8][8];
		turn = RED;
		for (int i = 0; i < 8; i++)
		{
			lineColor = i % 2;
			for (int j = 0; j < 8; j++)
			{
				if (lineColor == 0) matrix[i][j] = EMPTY_WHITE;
				else matrix[i][j] = EMPTY_BLUE;
				lineColor = switchColor(lineColor);
			}

		}
		// Debug
		/*
		 * for(int i=0;i<8;i++){ for(int j=0;j<8;j++) System.out.print(matrix[i][j]+" ");
		 * System.out.println(); }
		 */
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 8; j++)
			{
				if (matrix[i][j] != EMPTY_WHITE) matrix[i][j] = BLACK;
				if (matrix[i + 5][j] != EMPTY_WHITE) matrix[i + 5][j] = RED;

			}

	}

	// switches the background color of the slots
	private int switchColor(int c)
	{
		if (c == 0) return 1;
		else return 0;
	}

	/**
	 * Returns the state of a checkers slot: empty white, empty black, white black
	 * 
	 * @param i Line in matrix
	 * @param j Column in matrix
	 * @return integer representing the slot state
	 */
	public int getSlotState(int i, int j)
	{
		return matrix[i][j];
	}

	/**
	 * Method returns who is next to move red or black
	 */
	public int getTurn()
	{
		return turn;
	}

	public void switchTurn()
	{
		if (turn == RED) turn = BLACK;
		else turn = RED;
	}

	/**
	 * Tests if a player can make jump or not
	 * 
	 * @param ip line of initial slot
	 * @param jp column of initial slot
	 * @param ifin line of final slot
	 * @param jfin column of final slot
	 * @param ii line of intermediate point
	 * @param ij column of intermediate point
	 * @return true if the player can make the jump, false otherwise
	 */
	public boolean canJump(int ip, int jp, int ii, int ij, int ifin, int jfin)
	{

		if (ifin < 0 || ifin >= 8 || jfin < 0 || jfin >= 8) return false; // slot is off the board.

		if (matrix[ifin][jfin] != EMPTY_BLUE) return false; // slot already contains a piece or is
															// white.

		if (turn == RED)
		{
			if (matrix[ip][jp] == RED && ifin > ip) return false; // Regular red piece can only move
																	// up.
			if (matrix[ii][ij] != BLACK && matrix[ii][ij] != BLACK_KING) return false; // There is
																						// no black
																						// piece to
																						// jump.
			return true; // The jump is legal.
		}
		else
		{
			if (matrix[ip][jp] == BLACK && ifin < ip) return false; // Regular black piece can only
																	// move down.
			if (matrix[ii][ij] != RED && matrix[ii][ij] != RED_KING) return false; // There is no
																					// red piece to
																					// jump.
			return true; // The jump is legal.
		}

	}

	/**
	 * Tests if a player can make jump or not
	 * 
	 * @param ip line of initial slot
	 * @param jp column of initial slot
	 * @param ifin line of final slot
	 * @param jfin column of final slot
	 * @return true if the player can make the move, false otherwise
	 */
	public boolean canMove(int ip, int jp, int ifin, int jfin)
	{
		if (ifin < 0 || ifin >= 8 || jfin < 0 || jfin >= 8) return false; // (slot is off the board.

		if (matrix[ifin][jfin] != EMPTY_BLUE) return false; // final slot already contains a piece
															// or is White.

		if (turn == RED)
		{
			if (matrix[ip][jp] == RED)
			{
				if (ifin > ip || Math.abs(ip - ifin) != 1) return false;// Regualr red piece can
																		// only move down.
			}
			else if (matrix[ip][jp] == RED_KING)
			{
				if (Math.abs(ip - ifin) != 1) return false;
			}
			else return false;
			return true; // The move is legal.
		}
		else
		{
			if (matrix[ip][jp] == BLACK)
			{
				if (ifin < ip || Math.abs(ip - ifin) != 1) return false; // Regular black piece can
																			// only move up.
			}
			else if (matrix[ip][jp] == BLACK_KING)
			{
				if (Math.abs(ip - ifin) != 1) return false;
			}
			else return false;
			return true; // The move is legal.
		}

	}

	/**
	 * Makes a Move
	 * 
	 * @param ip line of initial slot
	 * @param jp column of initial slot
	 * @param ifin line of final slot
	 * @param jfin column of final slot
	 * @return true if the move was made, false otherwise
	 */
	public boolean move(int ip, int jp, int ifin, int jfin)
	{
		if (canMove(ip, jp, ifin, jfin))
		{
			if (turn == RED && ifin == 0) matrix[ifin][jfin] = RED_KING;
			else if (turn == BLACK && ifin == 7) matrix[ifin][jfin] = BLACK_KING;
			else matrix[ifin][jfin] = matrix[ip][jp];

			matrix[ip][jp] = EMPTY_BLUE;
			switchTurn();
			return true;
		}

		else
		{
			int ii, ij;
			ii = Math.max(ip, ifin) - 1;
			ij = Math.max(jp, jfin) - 1;
			if (canJump(ip, jp, ii, ij, ifin, jfin))
			{
				if (turn == RED && ifin == 0) matrix[ifin][jfin] = RED_KING;
				else if (turn == BLACK && ifin == 7) matrix[ifin][jfin] = BLACK_KING;
				else matrix[ifin][jfin] = matrix[ip][jp];
				matrix[ip][jp] = EMPTY_BLUE;
				matrix[ii][ij] = EMPTY_BLUE;
				if (!canJumpFrom(ifin, jfin)) switchTurn();
				return true;
			}
			else return false;
		}
	}

	private boolean canJumpFrom(int i, int j)
	{
		if (turn == RED)
		{
			if (canJump(i, j, i - 1, j - 1, i - 2, j - 2)
					|| canJump(i, j, i - 1, j + 1, i - 2, j + 2)) return true;
		}
		if (turn == BLACK)
		{
			if (canJump(i, j, i + 1, j - 1, i + 2, j - 2)
					|| canJump(i, j, i + 1, j + 1, i + 2, j + 2)) return true;
		}
		return false;

	}

	public int checkForWinner()
	{
		boolean foundRed = false;
		boolean foundBlack = false;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
			{
				if (matrix[i][j] == BLACK || matrix[i][j] == BLACK_KING) foundBlack = true;
				if (matrix[i][j] == RED || matrix[i][j] == RED_KING) foundRed = true;
			}

		if (foundRed == true && foundBlack == false) return RED;
		else if (foundRed == false && foundBlack == true) return BLACK;
		else return 100;
	}
}
