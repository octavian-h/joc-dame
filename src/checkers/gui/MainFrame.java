package checkers.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import checkers.core.GameRepresentation;

public class MainFrame extends JFrame {
	private Board checkersBoard;
	private GameRepresentation game;
	private InfoPane info;
	
	public MainFrame(){
		game=new GameRepresentation();
		info=new InfoPane();
		checkersBoard=new Board(game,info);
		this.getContentPane().add(info,BorderLayout.NORTH);
		this.getContentPane().add(checkersBoard,BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	public static void main(String[] args){
		MainFrame f=new MainFrame();
	}
}
