package checkers.core;

import checkers.gui.Board;
import checkers.p2p.Connection;
import checkers.p2p.event.P2PEvent;
import checkers.p2p.event.P2PListener;

public class Multiplayer implements P2PListener
{

	private int piece;
	private GameRepresentation game;// model of the game
	private int[] userMove;
	private Board brd;
	private Connection connection;
	private String receiverID;

	public Multiplayer(Connection c, String rid, GameRepresentation g, int p)
	{
		game = g;		
		piece = p;
		connection = c;
		receiverID = rid;
		connection.addP2PListener(this);
		userMove = new int[4];
	}

	public void getUserInput(int[] uM)
	{
		userMove = uM;
	}

	public boolean makeMove()
	{
		// Aici trebuie transmis sirul userMove catre celalat jucator iar cand
		// primeste
		// sirul(deci in metoda de recive) jucatorul trebui sa faca un
		// game.move(userMove[0], userMove[1], userMove[2], userMove[3]) pentru
		// a se modifica
		// matricea de joc
		connection.sendMessage(receiverID, convertUserMoveToString());
		return game.move(userMove[0], userMove[1], userMove[2], userMove[3]);

	}

	public void setBoard(Board b)
	{
		brd = b;
	}
	public int getPiece()
	{
		return piece;
	}

	public void setPiece(int p)
	{
		piece = p;
	}

	public boolean convertMessageToUserMove(String message)
	{
		int j = 0;
		if (message.charAt(0) == 'm')
		{
			for (int i = 1; i < 9; i++)
				if (Character.isDigit(message.charAt(i)))
				{
					userMove[j] = Character.digit(message.charAt(i), 10);
					j++;
				}
			return true;
		}
		else return false;

	}

	public String convertUserMoveToString()
	{
		StringBuffer buf = new StringBuffer("m");
		for (int i = 0; i < 4; i++)
			buf.append(" " + userMove[i]);
		return buf.toString();
	}

	@Override
	public void stateChanged(P2PEvent event)
	{
		switch (event.getTip())
		{
			case P2PEvent.MESSAGE_RECEIVED:
			{
				if(event.getSenderID().equals(receiverID))
				{
					//ne-a trimis cel cu care jucam
					boolean ok=convertMessageToUserMove(event.getMessage());
					if(ok)
					{
						game.move(userMove[0], userMove[1], userMove[2], userMove[3]);
						brd.updateBoard();
					}						
				}
			}
		}		
	}
}
