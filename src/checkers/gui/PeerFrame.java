package checkers.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import checkers.p2p.Connection;
import checkers.p2p.event.P2PEvent;
import checkers.p2p.event.P2PListener;
import checkers.util.Entry;
import checkers.util.ModelLista;

/**
 * Fereastra de invitatie.
 * 
 * @author Hasna Octavian-Lucian
 */

public class PeerFrame extends JFrame implements ActionListener,
P2PListener
{
	private Connection connection;
	private JButton bCauta, bInvita, bIesire;
	private JList<Entry<String, String>> lista;
	private ModelLista mLista;
	private String invitatID;
	
	public PeerFrame(Connection connection)
	{
		super("Checkers - Invite");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		connection.addP2PListener(this);
		this.connection = connection;
		if(!connection.isStarted()) try
		{
			connection.start();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "No connection.", "Error",
					JOptionPane.ERROR_MESSAGE);	
			this.dispose();
		}		
		mLista = new ModelLista(null);
		invitatID = ""; // nu am invitat pe nimeni
		
		//String s = (String) JOptionPane.showInputDialog(this, "Numele tau:", "Checkers", JOptionPane.INFORMATION_MESSAGE);
		
		// adauga continut
		setContentPane(getContinut());		
		pack();

		// centreaza fereastra
		Dimension size = getToolkit().getScreenSize();
		setLocation(size.width / 2 - getWidth() / 2, size.height / 2 - getHeight() / 2);
		setVisible(true);
	}
	
	/**
	 * Construieste continutul ferestrei "Checkers - Invite".
	 * 
	 * @return continut (JPanel)
	 */
	private JPanel getContinut()
	{
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		
		jp.add(getZonaCautare(), BorderLayout.NORTH);
		jp.add(getZonaLista(), BorderLayout.CENTER);
		jp.add(getZonaButoane(), BorderLayout.SOUTH);
		return jp;
	}
	
	private JPanel getZonaCautare()
	{
		JPanel jp = new JPanel();
		jp.setBorder(new EmptyBorder(5, 5, 5, 5));
		jp.setFocusable(true);
		jp.setLayout(new GridLayout(1, 1));
		bCauta = new JButton("Search peers");
		bCauta.addActionListener(this);
		jp.add(bCauta);
		return jp;
	}
	
	private JPanel getZonaLista()
	{
		JPanel jp = new JPanel();
		jp.setBorder(new EmptyBorder(0, 5, 5, 5));		
		jp.setLayout(new GridLayout(1, 1));
		jp.setPreferredSize(new Dimension(100, 200));
		
		lista = new JList<Entry<String, String>>(mLista);
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setSelectedIndex(0);
		lista.setVisibleRowCount(5);
		
		JScrollPane jsp = new JScrollPane(lista);
		jp.add(jsp);
		return jp;
	}
	
	private JPanel getZonaButoane()
	{
		JPanel jp = new JPanel();
		jp.setBorder(new EmptyBorder(0, 5, 5, 5));
		jp.setFocusable(true);
		jp.setLayout(new GridLayout(1, 2));
		bInvita = new JButton("Invite");
		bInvita.addActionListener(this);
		jp.add(bInvita);
		bIesire = new JButton("Cancel");
		bIesire.addActionListener(this);
		jp.add(bIesire);
		return jp;
	}

	/**
	 * Se ocupa cu evenimentele din fereastra Invite.
	 */
	public void actionPerformed(ActionEvent event)
	{
		Object sursa = event.getSource();
		if (sursa instanceof JButton)
		{
			if (sursa == bCauta)
			{
				if(bCauta.getText().equals("Search peers"))
				{
					connection.searchPeers();
					mLista.clear();
					bCauta.setText("Stop searching");
				}
				else
				{
					connection.stopSearching();
					bCauta.setText("Search peers");
				}
			}
			else if(sursa == bInvita)
			{
				if(!connection.isReady())
				{
					JOptionPane.showMessageDialog(this, "The connection is not ready.", "Error",
							JOptionPane.ERROR_MESSAGE);					
				}
				else
				{
					Entry<String, String> item = lista.getSelectedValue();
					if(item != null)
					{
						invitatID = lista.getSelectedValue().getKey();
						connection.sendMessage("i ask");
					}
					else
					{
						JOptionPane.showMessageDialog(this, "No peer selected.", "Error",
								JOptionPane.ERROR_MESSAGE);	
					}
				}
			}
			else if(sursa == bIesire)
			{
				this.dispose();
			}
		}
		
	}
	
	private void incepeJocul(String playerID, String playerName)
	{
		//aici trebuie scris
	}
	
	@Override
	public void stateChanged(P2PEvent event)
	{
		switch (event.getTip())
		{
			case P2PEvent.PEER_FOUND:
			{
				mLista.schimbaLista(event.getList());
				break;
			}
			case P2PEvent.PEER_SEARCH_FINISHED:
			{
				mLista.schimbaLista(event.getList());
				bCauta.setText("Search peers");
				break;
			}
			case P2PEvent.MESSAGE_RECEIVED:
			{
				String s = event.getMessage();
				if(s.equals("i ask"))
				{
					//ne-a trimis cineva o invitatie 
					String message = "Do you accept the invitation from "+event.getSenderName()+"?";
				    String title = "Accept invitation?";
				    int reply = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
				    if (reply == JOptionPane.YES_OPTION)
				    {
				    	incepeJocul(event.getSenderID(), event.getSenderName());
				    }
				}
				else if(s.equals("i ok"))
				{
					//ne-a raspuns cineva la invitatie
					if(invitatID.equals(event.getSenderID()))
					{
						incepeJocul(event.getSenderID(), event.getSenderName());
					}
				}
				break;
			}
		}		
	}
}
