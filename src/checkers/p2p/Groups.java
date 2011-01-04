package checkers.p2p;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.PeerGroupException;
//import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

import checkers.p2p.event.*;

/**
 * Operatii pe grupuri.
 * 
 * @author Hasna Octavian-Lucian
 */
public class Groups implements DiscoveryListener, ActionListener
{
	private final static int nrMaxGrupe = 1;
	private final static int nrMaxCautari = 5;
	private final static int intervalCautari = 5 * 1000;

	private EventListenerList listenerList;

	private DiscoveryService discovery;

	private PeerGroup defaultPeerGroup;
	private HashMap<String, String> groups;
	private javax.swing.Timer ceas;
	private int nrCautari;
	private String numeGrup;
	private PeerGroupAdvertisement firstPeerGroupAdv;

	private boolean isRunning;

	/**
	 * Constructorul pentru clasa Groups.
	 * 
	 * @param netPeerGroup
	 */
	public Groups(PeerGroup netPeerGroup)
	{
		defaultPeerGroup = netPeerGroup;

		listenerList = new EventListenerList();
		discovery = defaultPeerGroup.getDiscoveryService();
		groups = new HashMap<String, String>();
		ceas = new javax.swing.Timer(intervalCautari, this);
	}

	/**
	 * Porneste serviciile.
	 */
	public void start()
	{
		if (!isRunning)
		{
			isRunning = true;
			discovery.addDiscoveryListener(this);
		}
	}

	/**
	 * Opreste serviciile.
	 */
	public void stop()
	{
		if (isRunning)
		{
			isRunning = false;
			ceas.stop();
			groups.clear();
			discovery.removeDiscoveryListener(this);
		}
	}

	/**
	 * Cauta grupurile care au numele specificat.
	 * 
	 * @param groupName
	 *            numele grupul de cautat
	 */
	public void search(String groupName)
	{
		if (ceas.isRunning()) stopSearch();

		numeGrup = groupName;
		System.out.println("Info (Groups): Cautare locala.");
		searchLocal("Name", numeGrup);
		if (groups.size() < nrMaxGrupe)
		{
			System.out.println("Info (Groups): Cautare externa.");
			nrCautari = 0;
			ceas.start();
		}
	}

	/**
	 * Opreste cautarea.
	 */
	public void stopSearch()
	{
		ceas.stop();
		System.out.println("Info (Groups): S-a oprit cautarea.");
		Thread t=new Thread()
		{
			public void run()
			{
				fireSearchFinished(getGroups());
			}
		};
		t.start();		
	}

	/**
	 * Adauga grupurile gasite in lista.
	 * 
	 * @param advs
	 */
	private void addGroups(Enumeration<Advertisement> advs)
	{
		if (advs != null)
		{
			boolean primul = true;
			int aux = groups.size();
			while (groups.size() < nrMaxGrupe && advs.hasMoreElements())
			{
				Advertisement item = advs.nextElement();
				if (item instanceof PeerGroupAdvertisement)
				{
					PeerGroupAdvertisement pga = (PeerGroupAdvertisement) item;
					groups.put(pga.getPeerGroupID().toString(), pga.getName());
					System.out.println("Info (Groups): A fost gasit:" + pga.getName());
					if (primul)
					{
						firstPeerGroupAdv = pga;
						primul = false;
					}
				}
			}
			if (groups.size() != aux)
			{
				Thread t=new Thread()
				{
					public void run()
					{
						fireContentChanged(getGroups());
					}
				};
				t.start();
			}
		}
	}

	/**
	 * Cauta in exterior grupuri care au atributul attr cu valoarea val.
	 * 
	 * @param attr
	 * @param val
	 */
	private void searchRemote(String attr, String val)
	{
		discovery.getRemoteAdvertisements(null, DiscoveryService.GROUP, attr, val, nrMaxGrupe);
	}

	/**
	 * Cauta in cache grupuri care au atributul attr cu valoarea val.
	 * 
	 * @param attr
	 * @param val
	 */
	private void searchLocal(String attr, String val)
	{
		Enumeration<Advertisement> rez;
		try
		{
			rez = discovery.getLocalAdvertisements(DiscoveryService.GROUP, attr, val);
			addGroups(rez);
		}
		catch (IOException e)
		{
			System.out.println("Eroare (Groups): nu s-au putut citi cache-ul local.");
		}
	}

	/**
	 * Sterge grupurile stocate local.
	 */
	public void flush()
	{
		try
		{
			Enumeration<Advertisement> eachAdv = discovery.getLocalAdvertisements(
					DiscoveryService.GROUP, null, null);
			while (eachAdv.hasMoreElements())
			{
				Advertisement anAdv = (Advertisement) eachAdv.nextElement();
				discovery.flushAdvertisement(anAdv);
			}
			groups.clear();
		}
		catch (IOException e)
		{
			System.out.println("Eroare (Groups): nu s-au putut sterge group advertisements.");
		}
	}

	/**
	 * @return primul grup gasit
	 */
	public PeerGroup getFirstGroup()
	{
		PeerGroup primul = null;
		try
		{
			primul = defaultPeerGroup.newGroup(firstPeerGroupAdv);
		}
		catch (PeerGroupException e)
		{
			System.out.println("Eroare (Groups): nu s-au putut crea grupul din PeerGroupAdvertisement.");
		}
		
		return primul;
	}

	/**
	 * @return lista de grupuri
	 */
	public HashMap<String, String> getGroups()
	{
		return new HashMap<String, String>(groups);
	}

	/**
	 * Creeaza un grup cu name si description.
	 * 
	 * @param name
	 * @param description
	 * @return grupul creat
	 * @throws Exception
	 */
	public PeerGroup createGroup(String name, String description) throws Exception
	{
		return createGroup(IDFactory.newPeerGroupID(), name, description);
	}

	/**
	 * Creeaza un grup cu groupID, name si description.
	 * 
	 * @param groupID
	 * @param name
	 * @param description
	 * @return grupul creat
	 */
	public PeerGroup createGroup(PeerGroupID groupID, String name, String description)
	{
		PeerGroup newGroup = null;
		try
		{
			ModuleImplAdvertisement implAdv = defaultPeerGroup
					.getAllPurposePeerGroupImplAdvertisement();
			newGroup = defaultPeerGroup.newGroup(groupID, implAdv, name, description);
			PeerGroupAdvertisement groupAdv = newGroup.getPeerGroupAdvertisement();

			DiscoveryService discovery = defaultPeerGroup.getDiscoveryService();
			discovery.remotePublish(groupAdv, DiscoveryService.GROUP);
		}
		catch (Exception e)
		{
			System.out.println("Eroare (Groups): Nu s-a putut crea noul grup.");
		}
		
		return newGroup;
	}

	/**
	 * Asociaza partenerul local la group.
	 * 
	 * @param group
	 * @return rezultatul operatiei
	 */
	public boolean joinGroup(PeerGroup group)
	{
		AuthenticationCredential cred = new AuthenticationCredential(group, null, null);
		MembershipService membershipService = group.getMembershipService();
		Authenticator authenticator;
		try
		{
			authenticator = membershipService.apply(cred);
			if (authenticator.isReadyForJoin())
			{
				membershipService.join(authenticator);
				System.out.println("Info (Groups): S-a facut join la grupul " + group);
				return true;
			}
		}
		catch (Exception e)
		{
			System.out.println("Eroare (Groups): Nu s-a putut crea noul authenticator-ul.");
		}		

		return false;
	}

	/**
	 * Se ocupa cu evenimentul generat de gasirea unui grup.
	 */
	public void discoveryEvent(DiscoveryEvent event)
	{
		DiscoveryResponseMsg rez = event.getResponse();
		addGroups(rez.getAdvertisements());
	}

	/**
	 * Se ocupa cu evenimentele generate de ceas.
	 */
	public void actionPerformed(ActionEvent event)
	{
		nrCautari++;
		if (nrCautari > nrMaxCautari || groups.size() == nrMaxGrupe) stopSearch();
		else
		{
			System.out.println("Info (Groups): Cautarea nr " + nrCautari);
			searchRemote("Name", numeGrup);
		}
	}

	/**
	 * Adauga un <code>P2PListener</code> la clasa Groups.
	 */
	public synchronized void addP2PListener(P2PListener listener)
	{
		listenerList.add(P2PListener.class, listener);
	}

	/**
	 * Sterge <code>P2PListener</code> de la clasa Groups.
	 */
	public synchronized void removeP2PListener(P2PListener listener)
	{
		listenerList.remove(P2PListener.class, listener);
	}

	/**
	 * Notifica schimbarea continutului listei groups.
	 * 
	 * @param groupsList
	 */
	private synchronized void fireContentChanged(HashMap<String, String> groupsList)
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.GROUP_FOUND, groupsList));
		}
	}

	/**
	 * Notifica terminarea cautarii.
	 * 
	 * @param groupsList
	 */
	private synchronized void fireSearchFinished(HashMap<String, String> groupsList)
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.GROUP_SEARCH_FINISHED, groupsList));
		}
	}
}
