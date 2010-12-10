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
import net.jxta.document.StructuredDocument;
import net.jxta.id.IDFactory;
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
	private final static int intarziere = 5 * 1000;

	private EventListenerList listenerList;
	private PeerGroup defaultPeerGroup;
	private DiscoveryService discovery;
	private HashMap<PeerGroupID, PeerGroupAdvertisement> groups;
	private javax.swing.Timer ceas;
	private int nrCautari;
	private String numeGrup;
	
	public Groups(PeerGroup netPeerGroup)
	{
		defaultPeerGroup = netPeerGroup;
		listenerList = new EventListenerList();
		discovery = defaultPeerGroup.getDiscoveryService();
		groups = new HashMap<PeerGroupID, PeerGroupAdvertisement>();
		ceas = new javax.swing.Timer(intarziere, this);
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
		System.out.println("InfoG: Cautare locala.");
		searchLocal("Name", numeGrup);
		if (groups.size() < nrMaxGrupe)
		{
			System.out.println("InfoG: Cautare externa.");
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
		System.out.println("InfoG: S-a oprit cautarea.");
		fireSearchFinished();
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
			int aux = groups.size();
			while (groups.size() < nrMaxGrupe && advs.hasMoreElements())
			{
				Advertisement item = advs.nextElement();
				if (item instanceof PeerGroupAdvertisement)
				{
					groups.put(((PeerGroupAdvertisement) item).getPeerGroupID(),
							(PeerGroupAdvertisement) item);
					System.out.println("InfoG: A fost gasit:"
							+ ((PeerGroupAdvertisement) item).getName());
				}
			}
			if (groups.size() != aux) fireContentChanged();
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
		discovery.getRemoteAdvertisements(null, DiscoveryService.GROUP, attr, val, nrMaxGrupe, this);
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
			System.out.println("EroareG: nu s-au putut citi cache-ul local.");
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
		}
		catch (IOException e)
		{
			System.out.println("EroareG: nu s-au putut sterge group advertisements.");
		}
	}

	public HashMap<PeerGroupID, PeerGroupAdvertisement> getGroups()
	{
		return new HashMap<PeerGroupID, PeerGroupAdvertisement>(groups);
	}

	public PeerGroup createGroup(String name, String description) throws Exception
	{
		return createGroup(IDFactory.newPeerGroupID(), name, description);
	}

	public PeerGroup createGroup(PeerGroupID groupID, String name, String description)
			throws Exception
	{
		// Obtain a preformed ModuleImplAdvertisement to
		// use when creating the new peer group.
		ModuleImplAdvertisement implAdv = defaultPeerGroup
				.getAllPurposePeerGroupImplAdvertisement();

		// Create the new group using the Peer Group ID,
		// advertisement, name, and description.
		PeerGroup newGroup = defaultPeerGroup.newGroup(groupID, implAdv, name, description);

		// Need to publish the group remotely only because
		// newGroup() handles publishing to the local peer.
		PeerGroupAdvertisement groupAdv = newGroup.getPeerGroupAdvertisement();
		DiscoveryService discovery = defaultPeerGroup.getDiscoveryService();
		discovery.remotePublish(groupAdv, DiscoveryService.GROUP);

		return newGroup;
	}

	public void joinGroup(PeerGroup group)
	{
		StructuredDocument myCredentials = null;
		try
		{
			AuthenticationCredential myAuthenticationCredential = new AuthenticationCredential(
					group, null, myCredentials);
			MembershipService myMembershipService = group.getMembershipService();
			net.jxta.membership.Authenticator myAuthenticator = myMembershipService
					.apply(myAuthenticationCredential);
			if (!myAuthenticator.isReadyForJoin())
			{
				System.out.println("Authenticator is not complete - nu s-a putut face joined\n");
				return;
			}
			myMembershipService.join(myAuthenticator);
			System.out.println("Group has been joined\n");
		}
		catch (Exception e)
		{
			System.out.println("Authentication failed - group not joined\n");
			e.printStackTrace();
			// System.exit(-1);
		}

	}

	/**
	 * Se ocupa cu evenimentul generat de gasirea unui peer.
	 */
	public void discoveryEvent(DiscoveryEvent event)
	{
		DiscoveryResponseMsg rez = event.getResponse();
		System.out.println("InfoG: DiscoveryEvent de la group");
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
			System.out.println("G Cautarea nr " + nrCautari);
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
	 */
	private synchronized void fireContentChanged()
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.GROUP_FOUND));
		}
	}

	/**
	 * Notifica terminarea cautarii.
	 */
	private synchronized void fireSearchFinished()
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.SEARCH_FINISHED));
		}
	}
}
