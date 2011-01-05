package checkers.p2p;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager;
import checkers.p2p.event.*;

/**
 * Se ocupa cu realizarea conexiunii P2P.
 * 
 * @author Hasna Octavian-Lucian
 */
public class Connection implements P2PListener
{
	protected EventListenerList listenerList;
	private NetworkManager manager;

	private final static String gid = "urn:jxta:uuid-F256F83F63904289A362BDFCF7F226B602";
	private PeerGroup checkersGroup, netPeerGroup;
	private Groups groups;
	private Peers peers;

	private boolean isRunning, ready;

	/**
	 * Constructorul pentru clasa Connection
	 * 
	 * @param peerName
	 *            numele partenerului local
	 * @throws IOException
	 */
	public Connection(String peerName) throws IOException
	{
		Logger.getLogger("net.jxta").setLevel(Level.SEVERE);

		listenerList = new EventListenerList();
		manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, peerName, new File(new File(
				".cache"), "CheckersGame " + peerName).toURI());

		isRunning = false;
		ready = false;
	}

	/**
	 * Porneste conexiunea.
	 * 
	 * @throws IOException
	 * @throws PeerGroupException
	 */
	public void start() throws PeerGroupException, IOException
	{
		if (!isRunning)
		{
			System.out.println("Info (Connection): S-a deschis conexiunea.");
			manager.startNetwork();
			isRunning = true;
			ready = false;
			netPeerGroup = manager.getNetPeerGroup();
			groups = new Groups(netPeerGroup);
			groups.start();
			groups.addP2PListener(this);
			groups.flush();
			groups.search("CheckersGroup");
		}
	}

	public boolean isStarted()
	{
		return isRunning;
	}

	public boolean isReady()
	{
		return ready;
	}

	/**
	 * Opreste conexiunea.
	 */
	public void stop()
	{
		if (isRunning)
		{
			System.out.println("Info (Connection): S-a inchis conexiunea.");
			if (peers != null)
			{
				peers.removeP2PListener(this);
				peers.stop();
				peers = null;
			}
			groups.removeP2PListener(this);
			groups.stop();
			groups = null;
			checkersGroup = null;
			manager.stopNetwork();
			isRunning = false;
			ready = false;
		}
	}

	/**
	 * Cauta toti partenerii din grupul CheckersGroup.
	 */
	public void searchPeers()
	{
		if (peers != null)
		{
			//peers.flush();
			peers.search();
		}
	}

	/**
	 * Cauta partenerii care au nume asemanator cu nameFilter.
	 * 
	 * @param nameFilter
	 */
	public void searchPeers(String nameFilter)
	{
		if (peers != null)
		{
			peers.flush();
			if (isValid(nameFilter)) peers.search("*" + nameFilter + "*");
		}
	}

	/**
	 * Opreste cautarea.
	 */
	public void stopSearching()
	{
		if (peers != null) peers.stopSearch();
	}

	public static boolean isValid(String s)
	{
		Pattern p = Pattern.compile("\\w*");// a-z A-Z _ 0-9
		Matcher m = p.matcher(s);
		return m.matches();
	}

	/**
	 * Trimite un mesaj prin output pipe.
	 * 
	 * @param receiverID
	 *            id-ul partenerului la care se trimite
	 * @param message
	 *            mesajul de trimis
	 */
	public void sendMessage(String receiverID, String message)
	{
		if (peers != null) peers.sendMessage(receiverID, message);
	}

	/**
	 * @return lista de parteneri (id, nume_partener)
	 */
	public HashMap<String, String> getPeers()
	{
		if (peers != null) return peers.getPeers();
		else return null;
	}

	/**
	 * Adauga un <code>P2PListener</code> la clasa Connection.
	 */
	public void addP2PListener(P2PListener listener)
	{
		listenerList.add(P2PListener.class, listener);
	}

	/**
	 * Sterge <code>P2PListener</code> de la clasa Connection.
	 */
	public void removeP2PListener(P2PListener listener)
	{
		listenerList.remove(P2PListener.class, listener);
	}

	/**
	 * Se ocupa cu evenimentul generate de P2P de gasirea unui grup.
	 */
	public void stateChanged(P2PEvent event)
	{
		switch (event.getTip())
		{
			case P2PEvent.GROUP_FOUND:
			{
				System.out.println("Info (Connection): A fost gasit grupul.");

				checkersGroup = groups.getFirstGroup();
				/*
				if (!groups.joinGroup(checkersGroup))
				{
					System.out.println("Info (Connection): Nu s-a putut face join!");
				}
				*/
				peers = new Peers(netPeerGroup); // checkersGroup
				peers.addP2PListener(this);
				peers.start();
				break;
			}
			case P2PEvent.GROUP_SEARCH_FINISHED:
			{
				if (checkersGroup == null)
				{
					System.out.println("Info (Connection): Creaza grup nou.");
					PeerGroupID groupID = null;
					try
					{
						groupID = (PeerGroupID) IDFactory.fromURI(new URI(gid));
					}
					catch (URISyntaxException e)
					{
						System.out.println("Eroare (Peers): peer id incorect!");
					}
					checkersGroup = groups.createGroup(groupID, "CheckersGroup",
							"Group for checkers game.");
					/*
					if (!groups.joinGroup(checkersGroup))
					{
						System.out.println("Info (Connection): Nu s-a putut face join!");
					}
					*/
					peers = new Peers(netPeerGroup); // checkersGroup
					peers.addP2PListener(this);
					peers.start();

				}
				break;
			}
			case P2PEvent.MESSAGE_RECEIVED:
			{
				System.out.println("Info (Connection): s-a primit un mesaj de la " + event.getSenderName()
						+ " ce contine [" + event.getMessage() + "]");
				
				fireMessageReceived(event.getSenderID(), event.getSenderName(), event.getMessage());
				break;
			}
			case P2PEvent.PEER_FOUND:
			{
				fireContentChanged(event.getList());
				break;
			}
			case P2PEvent.PEER_SEARCH_FINISHED:
			{
				fireSearchFinished(event.getList());
				break;
			}
			case P2PEvent.PEER_READY:
			{
				ready = true;
				// fireConnectionReady();
				// break;
			}
		}
	}

	/**
	 * Notifica asocierea la grupul checkersGroup.
	 */
	private void fireConnectionReady()
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.CONNECTION_READY));
		}
	}

	/**
	 * Notifica schimbarea continutului listei de parteneri.
	 * 
	 * @param peersList
	 */
	private void fireContentChanged(HashMap<String, String> peersList)
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.PEER_FOUND, peersList));
		}
	}

	/**
	 * Notifica terminarea cautarii.
	 * 
	 * @param peersList
	 */
	private void fireSearchFinished(HashMap<String, String> peersList)
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.PEER_SEARCH_FINISHED, peersList));
		}
	}

	/**
	 * Notifica primirea unui mesaj.
	 * 
	 * @param senderID
	 *            id-ul partenerului care a trimis
	 * @param senderName
	 *            numele partenerului care a trimis
	 * @param data
	 *            mesajul trimis
	 */
	private synchronized void fireMessageReceived(String senderID, String senderName, String data)
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.MESSAGE_RECEIVED, senderID,
					senderName, data));
		}
	}
}
