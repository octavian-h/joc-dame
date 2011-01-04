package checkers.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import checkers.core.GameRepresentation;
import checkers.core.Multiplayer;
import checkers.core.SinglePlayer;

public class MainFrame extends JFrame implements ActionListener{
	private Board checkersBoard;
	private GameRepresentation game;
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
	
	public MainFrame(){
		menuBar=new JMenuBar();
		fileMenu=new JMenu("File");
		single=new JMenuItem("Single Player");
		single.addActionListener(this);	
		fileMenu.add(single);
		multiple=new JMenuItem("Multiplayer");
		multiple.addActionListener(this);
		fileMenu.add(multiple);
		q=new JMenuItem("Quit");
		q.addActionListener(this);
		fileMenu.add(q);
		helpMenu=new JMenu("Help");
		instr=new JMenuItem("Instructions");
		instr.addActionListener(this);
		helpMenu.add(instr);
		about=new JMenuItem("About");
		about.addActionListener(this);
		helpMenu.add(about);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);
		info=new InfoPane();
		
		
		this.getContentPane().add(info,BorderLayout.NORTH);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==q){
			System.exit(0);
		}
		if(e.getSource()==single){
			game=new GameRepresentation();
			
			user=new SinglePlayer(game);
			if(checkersBoard==null)
				checkersBoard=new Board(game,info, user,null,1);
			else{
				this.remove(info);
				this.remove(checkersBoard);
				checkersBoard=new Board(game,info, user,null,1);
				info=new InfoPane();
			}
			info.setGame(game);
			checkersBoard.updateBoard();
			this.getContentPane().add(info,BorderLayout.NORTH);
			this.getContentPane().add(checkersBoard,BorderLayout.CENTER);
			this.pack();
			this.validate();
		}
		
		if(e.getSource()==about){
			JFrame ab=new JFrame("About");
			JTextPane text=new JTextPane();
			text.setEditable(false);
			text.setAlignmentX(CENTER_ALIGNMENT);
			text.setAlignmentY(CENTER_ALIGNMENT);
			text.setText("Checkers game V1.0\n\n"+
						"       Authors:\n"+
			            "    Hasna Octavian\n"+
			            "    Forna Remus\n"+
						"    Grupa 30231");
			ab.add(text,BorderLayout.CENTER);
			ab.pack();
			ab.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			ab.setVisible(true);
		}
		
		if(e.getSource()==instr){
			String ins=new String();
			 JFrame ab=new JFrame("Instructions");
			 JTextPane text=new JTextPane();
			 JScrollPane slider = new JScrollPane(text);
			  try{
				    
				    FileInputStream fstream = new FileInputStream("graphics/Instructions.txt");
				    
				    DataInputStream in = new DataInputStream(fstream);
				        BufferedReader br = new BufferedReader(new InputStreamReader(in));
				    String strLine;
				    while ((strLine = br.readLine()) != null)   {
				       ins=text.getText();
				    	text.setText(ins+"\n"+strLine);
				    }
				   
				    in.close();
				    }catch (Exception ex){//Catch exception if any
				      System.err.println("Error: " + ex.getMessage());
				    }
				    ab.setPreferredSize(new Dimension(500,500));
				    
			             
					text.setEditable(false);
					text.setAlignmentX(CENTER_ALIGNMENT);
					text.setAlignmentY(CENTER_ALIGNMENT);
					ab.add(slider,BorderLayout.CENTER);
					ab.pack();
					ab.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					ab.setVisible(true);
		}
		if(e.getSource()==multiple){
			game=new GameRepresentation();
			
			
			if(checkersBoard==null)
				checkersBoard=new Board(game,info, null,multiuser,2);
			else{
				this.remove(info);
				this.remove(checkersBoard);
				checkersBoard=new Board(game,info, null,multiuser,2);
				info=new InfoPane();
			}
			multiuser=new Multiplayer(game,checkersBoard);
			info.setGame(game);
			checkersBoard.updateBoard();
			this.getContentPane().add(info,BorderLayout.NORTH);
			this.getContentPane().add(checkersBoard,BorderLayout.CENTER);
			this.pack();
			this.validate();
		}
	}
	
	public static void main(String[] args){
		MainFrame f=new MainFrame();
	}
}
