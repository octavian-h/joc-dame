package checkers.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


import javax.swing.BorderFactory;
import javax.swing.ImageIcon;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;




public class Board extends JPanel implements MouseListener, MouseMotionListener{
	//Labels for holding checkers pieces
	private JLabel[][] slots;
	//Image for black
	private ImageIcon black;
	//Image for red
	private ImageIcon red;
	//Image for black king
	private ImageIcon kingBlack;
	//Image for red king
	private ImageIcon kingRed;
	//Initial slot from where the piece is moved
	private JLabel initSlot;
	//Slots over which the piece hovers over
	private JLabel intSlot;
	//Final slot where the piece is dropped
	private JLabel endSlot;
	//Current X coordinate for the mouse cursor
	private int cursorX;
	//Current Y coordinate for the mouse cursor
	private int cursorY;
	//enable moving the pieces true=enabled, false=disabled
	private boolean enableDrag;
	/**
	 * Constructor for the board: draws the board and lays out the pieces
	 */
	public Board(){
		//colors used for the checkers board
		Color[] c=new Color[2];
		c[0]=Color.WHITE;
		c[1]=Color.BLUE;
		//load images for pieces
		black=new ImageIcon("black.jpg");
		kingBlack=new ImageIcon("black_king.jpg");
		red=new ImageIcon("red.jpg");
		kingRed=new ImageIcon("red_king.jpg");
		//disable dragging
		enableDrag=false;
		int cl=0;//initializes the first color of the row
		//set the size of the board
		this.setPreferredSize(new Dimension(500,500));
		//Set 8/8 grid layout
		this.setLayout(new GridLayout(8,8));
		slots=new JLabel[8][8];
		for(int i=0;i<8;i++){
			cl=i%2;
			for(int j=0;j<8;j++){
				slots[i][j]=new JLabel();
				//add mouse listener 
				slots[i][j].addMouseListener(this);
				slots[i][j].addMouseMotionListener(this);
				//allign the image to the center
				slots[i][j].setHorizontalAlignment(JLabel.CENTER);
				slots[i][j].setOpaque(true);
				slots[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				slots[i][j].setBackground(c[cl]);
				//add the label to the panel
				this.add(slots[i][j]);
				cl=switchColor(cl);
			}
		}
		//lay out the pieces on the checkers board
		for(int i=0;i<2;i++)
			for(int j=0;j<8;j++){
				if(slots[i][j].getBackground()==Color.BLUE)
					slots[i][j].setIcon(black);
				if(slots[i+6][j].getBackground()==Color.BLUE)
					slots[i+6][j].setIcon(red);
			
			}
	}
	
	//switches the background color of the slots
	private int switchColor(int c){
		if(c==0)
			return 1;
		else 
			return 0;
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Listens to the event in which the mouse enters a component
	 * @param e indentifies the mouse event that occurred
	 */
	public void mouseEntered(MouseEvent e) {
		//get the JLabel over which the mouse is
		endSlot=(JLabel)e.getComponent();
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Listens to the event in which the mouse is pressed on a component
	 * @param e indentifies the mouse event that occurred
	 */
	public void mousePressed(MouseEvent e) {
		//get the initial JLabel
		initSlot=(JLabel)e.getComponent();
		
		enableDrag=true;
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				if(slots[i][j].equals(initSlot)){
					slots[i][j].setIcon(null);
					
				}
		
	}

	/**
	 * Listens to the event in which the mouse is released over a component
	 * @param e indentifies the mouse event that occurred
	 */
	public void mouseReleased(MouseEvent e) {
		enableDrag=false;//disable drag
		//drop the component
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				if(slots[i][j].equals(endSlot)){
					slots[i][j].setIcon(black);
					
				}
		repaint();
	}

	/**
	 * Listens to the event in which the mouse is dragged over a component
	 * @param e indentifies the mouse event that occurred
	 */
	public void mouseDragged(MouseEvent e) {
		//get cursor position
		cursorX=e.getX();
		cursorY=e.getY();
		//get the component over which the mouse is
		intSlot=(JLabel)e.getComponent();
		//convert the point to JPanel coordinates because the initial position is 
		//relative to the JLabel over which the mouse is
		Point interm=SwingUtilities.convertPoint(intSlot,cursorX,cursorY,this);
		cursorX=interm.x;
		cursorY=interm.y;
		
		repaint();
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	//when mouse is dragging draw a circle with the center at the cursor position
	protected void paintChildren(Graphics g){
		super.paintChildren(g);
		if(enableDrag){
			Graphics2D g2=(Graphics2D)g;
			g2.setColor(getBackground());
			g2.setColor(Color.BLACK);
			g2.fillOval(cursorX-20, cursorY-20, 40, 40);
		}
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


