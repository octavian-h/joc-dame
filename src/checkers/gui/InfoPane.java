package checkers.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoPane extends JPanel{
	
	private JLabel blackPlayer;
	private JLabel redPlayer;
	
	public InfoPane(){
		this.setPreferredSize(new Dimension(500,50));
		this.setLayout(new GridLayout(1,2));
		blackPlayer=new JLabel("Player Black");
		blackPlayer.setOpaque(true);
		
		blackPlayer.setHorizontalAlignment(JLabel.CENTER);
		blackPlayer.setVerticalAlignment(JLabel.CENTER);
		
		redPlayer=new JLabel("Player Red");
		redPlayer.setOpaque(true);
		redPlayer.setForeground(Color.RED);
		redPlayer.setBackground(Color.ORANGE);
		redPlayer.setHorizontalAlignment(JLabel.CENTER);
		redPlayer.setVerticalAlignment(JLabel.CENTER);
		this.add(blackPlayer);
		this.add(redPlayer);
	}
	
	public void updateInfo(){
		if(blackPlayer.getBackground()==Color.ORANGE){
			blackPlayer.setBackground(null);
			redPlayer.setBackground(Color.ORANGE);
		}
		else{
			blackPlayer.setBackground(Color.ORANGE);
			redPlayer.setBackground(null);
		}
	}
}
