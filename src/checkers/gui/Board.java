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

import checkers.ai.Computer;
import checkers.core.GameRepresentation;




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
	private int xi,yi;
	//Slots over which the piece hovers over
	private JLabel intSlot;
	private ImageIcon intermediate;
	//Final slot where the piece is dropped
	private JLabel endSlot;
	
	//Current X coordinate for the mouse cursor
	private int cursorX;
	//Current Y coordinate for the mouse cursor
	private int cursorY;
	//enable moving the pieces true=enabled, false=disabled
	private boolean enableDrag;
	private GameRepresentation game;//model of the game
	private InfoPane info;
	private Computer comp;
	/**
	 * Constructor for the board: draws the board and lays out the pieces
	 */
	public Board(GameRepresentation g, InfoPane inf){
		game=g;
		info=inf;
		comp=new Computer(game);
		//load images for pieces
		black=new ImageIcon("graphics/black.jpg");
		kingBlack=new ImageIcon("graphics/black_king.jpg");
		red=new ImageIcon("graphics/red.jpg");
		kingRed=new ImageIcon("graphics/red_king.jpg");
		//disable dragging
		enableDrag=false;
		int cl=0;//initializes the first color of the row
		//set the size of the board
		this.setPreferredSize(new Dimension(500,500));
		//Set 8/8 grid layout
		this.setLayout(new GridLayout(8,8));
		slots=new JLabel[8][8];
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				slots[i][j]=new JLabel();
				//add mouse listener 
				slots[i][j].addMouseListener(this);
				slots[i][j].addMouseMotionListener(this);
				//allign the image to the center
				slots[i][j].setHorizontalAlignment(JLabel.CENTER);
				slots[i][j].setOpaque(true);
				slots[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				cl=game.getSlotState(i, j);
				if(cl==GameRepresentation.EMPTY_WHITE)
					slots[i][j].setBackground(Color.WHITE);
				else 
					slots[i][j].setBackground(Color.BLUE);
				//add the label to the panel
				this.add(slots[i][j]);
				
			}
		}
		//lay out the pieces on the checkers board
		for(int i=0;i<3;i++)
			for(int j=0;j<8;j++){
				cl=game.getSlotState(i, j);
				if(cl!=GameRepresentation.EMPTY_WHITE)
					slots[i][j].setIcon(black);
				
				
				cl=game.getSlotState(i+5, j);
				if(cl!=GameRepresentation.EMPTY_WHITE)
					
					slots[i+5][j].setIcon(red);
				
			}
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
		intermediate=(ImageIcon)initSlot.getIcon();
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				if(slots[i][j].equals(initSlot)){
					xi=i;
					yi=j;
										
					if(game.getTurn()==GameRepresentation.RED&&(game.getSlotState(i, j)==GameRepresentation.RED||game.getSlotState(i, j)==GameRepresentation.RED_KING)){
						slots[i][j].setIcon(null);
						enableDrag=true;
					}
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
				if(game.getTurn()==GameRepresentation.RED){
					if(slots[i][j].equals(endSlot))
						if(game.canMove(xi,yi,i,j)){
							if(i==0)
								slots[i][j].setIcon(kingRed);
						
							else
								slots[i][j].setIcon(intermediate);
						game.move(xi,yi,i,j);
						info.updateInfo();
						}
						else{
							int ii, ij;
							ii=Math.max(xi, i)-1;
							ij=Math.max(yi, j)-1;
							if(game.canJump(xi,yi,ii,ij,i,j)){
								if(i==0)
									slots[i][j].setIcon(kingRed);
								else
									slots[i][j].setIcon(intermediate);
							slots[ii][ij].setIcon(null);
							game.move(xi,yi,i,j);
							info.updateInfo();
							
							}
							else 						
								slots[xi][yi].setIcon(intermediate);
						}
				
					repaint();		
					}
				else{
					int[] aux;
					aux=comp.getNextMove();
						if(aux.length==4){
							if(aux[2]==7)
								slots[aux[2]][aux[3]].setIcon(kingBlack);
						
							else
								slots[aux[2]][aux[3]].setIcon(black);
						slots[aux[0]][aux[1]].setIcon(null);
						game.move(aux[0],aux[1],aux[2],aux[3]);
						info.updateInfo();
						}
						else{
								if(aux[4]==0)
									slots[aux[4]][aux[5]].setIcon(kingBlack);
								else
									slots[aux[4]][aux[5]].setIcon(intermediate);
							slots[aux[0]][aux[1]].setIcon(null);
							slots[aux[2]][aux[3]].setIcon(null);
							game.move(aux[0],aux[1],aux[4],aux[5]);
							info.updateInfo();
							
							}
						repaint();	
						}
					
				}
			
		/**if(game.getTurn()==GameRepresentation.BLACK){
			int[] aux;
			aux=comp.getNextMove();
			System.out.println();
			for(int x=0;x<aux.length;x++)
				System.out.print(aux[x]+" ");
			if(aux.length==4){
				slots[aux[0]][aux[1]].setIcon(null);
				slots[aux[2]][aux[3]].setIcon(black);
				game.move(aux[0], aux[1], aux[2], aux[3]);
				info.updateInfo();
			}
			else{
				slots[aux[0]][aux[1]].setIcon(null);
				slots[aux[2]][aux[3]].setIcon(null);
				slots[aux[4]][aux[5]].setIcon(black);
				game.move(aux[0], aux[1], aux[4], aux[5]);
				info.updateInfo();
			}
			repaint();
		}**/
			
	

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
			g2.setColor(Color.YELLOW);
			g2.fillOval(cursorX-20, cursorY-20, 40, 40);
		}
	}
	
	
	
}


