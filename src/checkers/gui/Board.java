package checkers.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel{
	JLabel[][] slots;

	public Board(){
		Color[] c=new Color[2];
		c[0]=Color.WHITE;
		c[1]=Color.BLUE;
		int cl=0;
		this.setPreferredSize(new Dimension(500,500));
		this.setLayout(new GridLayout(8,8));
		slots=new JLabel[8][8];
		for(int i=0;i<8;i++){
			cl=i%2;
			for(int j=0;j<8;j++){
				slots[i][j]=new JLabel();
				slots[i][j].setOpaque(true);
				slots[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				slots[i][j].setBackground(c[cl]);
				this.add(slots[i][j]);
				cl=switchColor(cl);
			}
		}
		
	}
	
	private int switchColor(int c){
		if(c==0)
			return 1;
		else 
			return 0;
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Testing");
		Board b=new Board();
		
		f.getContentPane().add(b,BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		
	}
}


