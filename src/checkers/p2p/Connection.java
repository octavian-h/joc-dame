package checkers.p2p;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

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
	private boolean isRunning;

	public static void main(String[] args) 
	{
		//pentru testare
		
		try
		{
			Connection c =new Connection("Alabala");
			c.open();
			Thread.sleep(10000);
			c.searchPeers();
			c.close();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection(String numePeer) throws IOException
	{
		Logger.getLogger("net.jxta").setLevel(Level.SEVERE);

		listenerList = new EventListenerList();
		manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, numePeer, new File(new File(
				".cache"), "CheckersGame " + numePeer).toURI());
		isRunning = false;
	}

	public void open() throws PeerGroupException, IOException
	{
		if (!isRunning)
		{
			System.out.println("S-a deschis conexiunea");
			isRunning = true;
			manager.startNetwork();
			netPeerGroup = manager.getNetPeerGroup();
			groups = new Groups(netPeerGroup);
			groups.addP2PListener(this);
			// groups.flush();
			groups.search("CheckersGroup");
		}
	}

	public synchronized void close()
	{
		if (isRunning)
		{
			peers.stopSearch();
			groups.stopSearch();
			isRunning = false;			
			P2PListener[] listeners = listenerList.getListeners(P2PListener.class);
			for (int i = listeners.length - 1; i >= 0; --i)
			{
				peers.removeP2PListener(listeners[i]);
			}
			groups.removeP2PListener(this);			
			peers = null;
			groups = null;
			checkersGroup = null;
			manager.stopNetwork();
			System.out.println("S-a inchis conexiunea");
		}
	}

	public void searchPeers()
	{
		peers.flush();
		peers.search();
	}

	public void searchPeers(String nameFilter)
	{
		// peers.flush();
		if (isValid(nameFilter)) peers.search("*" + nameFilter + "*");
		else
		{
			// TODO
		}
	}

	public void stopSearching()
	{
		peers.stopSearch();
	}

	private boolean isValid(String s)
	{
		Pattern p = Pattern.compile("\\w");// a-z A-Z _ 0-9
		Matcher m = p.matcher(s);
		return m.matches();
	}

	public HashMap<PeerID, PeerAdvertisement> getPeers()
	{
		return peers.getPeers();
	}

	/**
	 * Adauga un <code>P2PListener</code> la clasa Peers.
	 */
	public synchronized void addP2PListener(P2PListener listener)
	{
		listenerList.add(P2PListener.class, listener);
		peers.addP2PListener(listener);
	}

	/**
	 * Sterge <code>P2PListener</code> de la clasa Peers.
	 */
	public synchronized void removeP2PListener(P2PListener listener)
	{
		listenerList.remove(P2PListener.class, listener);
		peers.removeP2PListener(listener);
	}

	@Override
	public void stateChanged(P2PEvent event)
	{
		switch (event.getTip())
		{
			case P2PEvent.GROUP_FOUND:
			{
				try
				{
					HashMap<PeerGroupID, PeerGroupAdvertisement> pg = groups.getGroups();
					checkersGroup = netPeerGroup.newGroup(pg.entrySet().iterator().next()
							.getValue());
					groups.joinGroup(checkersGroup);
					peers = new Peers(netPeerGroup);//checkersGroup);
					break;
				}
				catch (PeerGroupException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			case P2PEvent.SEARCH_FINISHED:
			{
				if (checkersGroup == null)
				{
					try
					{
						PeerGroupID groupID = (PeerGroupID) IDFactory.fromURI(new URI(gid));
						checkersGroup = groups.createGroup(groupID, "CheckersGroup",
								"Group for checkers game.");
						groups.joinGroup(checkersGroup);
						peers = new Peers(netPeerGroup);//checkersGroup
					}
					catch (Exception e)
					{
						System.out.println("Nu s-a putut crea grupul!");
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
