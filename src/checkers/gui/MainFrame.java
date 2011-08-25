package checkers.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import checkers.core.GameRepresentation;
import checkers.core.Multiplayer;
import checkers.core.SinglePlayer;
import checkers.p2p.Connection;

public class MainFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -8884970490313600105L;

	private Board checkersBoard;
	private InfoPane info;
	private SinglePlayer user;
	private Multiplayer multiuser;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem single;
	private JMenuItem multiple;
	private JMenuItem q;
	private JMenu helpMenu;
	private JMenuItem instr;
	private JMenuItem about;
	private Connection connection;
	private String playerName;

	public MainFrame()
	{
		super("Checkers");
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		single = new JMenuItem("Single Player");
		single.addActionListener(this);
		fileMenu.add(single);
		multiple = new JMenuItem("Multiplayer");
		multiple.addActionListener(this);
		fileMenu.add(multiple);
		q = new JMenuItem("Quit");
		q.addActionListener(this);
		fileMenu.add(q);
		helpMenu = new JMenu("Help");
		instr = new JMenuItem("Instructions");
		instr.addActionListener(this);
		helpMenu.add(instr);
		about = new JMenuItem("About");
		about.addActionListener(this);
		helpMenu.add(about);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);
		info = new InfoPane();
		playerName = findPlayerName();

		this.setTitle("Checkers - " + playerName);
		this.getContentPane().add(info, BorderLayout.NORTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == q)
		{
			System.exit(0);
		}
		if (e.getSource() == single)
		{

			user = new SinglePlayer();
			if (checkersBoard == null) checkersBoard = new Board(info, user, null, 1);
			else
			{
				this.remove(info);
				this.remove(checkersBoard);
				checkersBoard = new Board(info, user, null, 1);
				info = new InfoPane();
			}
			user.setGame(checkersBoard.getGame());
			info.setGame(checkersBoard.getGame());
			checkersBoard.updateBoard();
			this.getContentPane().add(info, BorderLayout.NORTH);
			this.getContentPane().add(checkersBoard, BorderLayout.CENTER);
			this.pack();
			this.validate();
		}

		if (e.getSource() == about)
		{
			JFrame ab = new JFrame("About");
			JTextPane text = new JTextPane();
			text.setEditable(false);
			text.setAlignmentX(CENTER_ALIGNMENT);
			text.setAlignmentY(CENTER_ALIGNMENT);
			text.setText("Checkers game V1.0\n\n" + "       Authors:\n" + "    Hasna Octavian\n"
					+ "    Forna Remus\n" + "    Grupa 30231");
			ab.add(text, BorderLayout.CENTER);
			ab.pack();
			ab.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			ab.setVisible(true);
		}

		if (e.getSource() == instr)
		{
			String ins = new String();
			JFrame ab = new JFrame("Instructions");
			JTextPane text = new JTextPane();
			JScrollPane slider = new JScrollPane(text);
			try
			{
				DataInputStream in = new DataInputStream(this.getClass().getResourceAsStream(
						"/resources/docs/Instructions.txt"));
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				while ((strLine = br.readLine()) != null)
				{
					ins = text.getText();
					text.setText(ins + "\n" + strLine);
				}

				in.close();
			}
			catch (Exception ex)
			{// Catch exception if any
				System.err.println("Error: " + ex.getMessage());
			}
			ab.setPreferredSize(new Dimension(500, 500));

			text.setEditable(false);
			text.setAlignmentX(CENTER_ALIGNMENT);
			text.setAlignmentY(CENTER_ALIGNMENT);
			ab.add(slider, BorderLayout.CENTER);
			ab.pack();
			ab.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			ab.setVisible(true);
		}
		if (e.getSource() == multiple)
		{
			if (connection == null)
			{
				try
				{
					connection = new Connection(playerName);
				}
				catch (IOException e1)
				{
					JOptionPane.showMessageDialog(this,
							"The connection cannot proceed because IO error.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			PeerFrame pf = new PeerFrame(this, connection);
			pf.setVisible(true);
		}
	}

	public void addPlayerNames(String player1, String player2)
	{
		info.updatePlayerNames(player1, player2);
	}

	private String findPlayerName()
	{
		boolean ok = false;
		String name = "";
		while (!ok)
		{
			name = (String) JOptionPane.showInputDialog(this, "Your name:",
					"Checkers - Player name", JOptionPane.INFORMATION_MESSAGE);
			if (name == null)
			{
				System.exit(0);
			}
			else if (!Connection.isValid(name))
			{
				JOptionPane.showMessageDialog(this,
						"Name is empty or contains illegal characters.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			else ok = true;
		}
		return name;
	}

	public void startMultiplayer(String opponentID, String opponentName, int myColor)
	{
		if (myColor == GameRepresentation.BLACK) addPlayerNames(playerName, opponentName);
		else addPlayerNames(opponentName, playerName);

		multiuser = new Multiplayer(connection, opponentID, myColor);

		if (checkersBoard == null) checkersBoard = new Board(info, null, multiuser, 2);
		else
		{
			this.remove(info);
			this.remove(checkersBoard);
			checkersBoard = new Board(info, null, multiuser, 2);
			info = new InfoPane();
		}
		multiuser.setBoard(checkersBoard);
		multiuser.setGame(checkersBoard.getGame());
		info.setGame(checkersBoard.getGame());
		checkersBoard.updateBoard();
		this.getContentPane().add(info, BorderLayout.NORTH);
		this.getContentPane().add(checkersBoard, BorderLayout.CENTER);
		this.pack();
		this.validate();
	}
}
