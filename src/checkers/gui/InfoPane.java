package checkers.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import checkers.core.GameRepresentation;

public class InfoPane extends JPanel
{
	private static final long serialVersionUID = -5890778534467905182L;
	private JLabel blackPlayer;
	private JLabel redPlayer;
	private GameRepresentation game;

	public InfoPane()
	{
		this.setPreferredSize(new Dimension(500, 50));
		this.setLayout(new GridLayout(1, 2));
		blackPlayer = new JLabel("Player Black");
		blackPlayer.setOpaque(true);

		blackPlayer.setHorizontalAlignment(JLabel.CENTER);
		blackPlayer.setVerticalAlignment(JLabel.CENTER);

		redPlayer = new JLabel("Player Red");
		redPlayer.setOpaque(true);
		redPlayer.setForeground(Color.RED);
		redPlayer.setBackground(Color.ORANGE);
		redPlayer.setHorizontalAlignment(JLabel.CENTER);
		redPlayer.setVerticalAlignment(JLabel.CENTER);
		this.add(blackPlayer);
		this.add(redPlayer);
	}

	public void updateInfo()
	{
		if (game.getTurn() == GameRepresentation.RED)
		{
			blackPlayer.setBackground(null);
			redPlayer.setBackground(Color.ORANGE);
		}
		else
		{
			blackPlayer.setBackground(Color.ORANGE);
			redPlayer.setBackground(null);
		}
		if (game.checkForWinner() == GameRepresentation.RED) 
			redPlayer.setText(redPlayer.getText() + " Winner");

		if (game.checkForWinner() == GameRepresentation.BLACK) 
			blackPlayer.setText(blackPlayer.getText() + " Winner");
	}

	public void setGame(GameRepresentation g)
	{
		game = g;
		updateInfo();
	}

	public void updatePlayerNames(String player1, String player2)
	{
		blackPlayer.setText(player1);
		redPlayer.setText(player2);
	}
}
