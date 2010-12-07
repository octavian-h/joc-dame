package checkers.p2p;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;

import checkers.p2p.event.*;

/**
 * Operatii pe peer-uri.
 * 
 * @author Hasna Octavian-Lucian
 */
public class Peers implements DiscoveryListener, ActionListener
{
	private final static int nrMaxPeers = 10;
	private final static int nrMaxCautari = 5;
	private final static int intarziere = 5 * 1000;

	private EventListenerList listenerList;
	private DiscoveryService discovery;
	private HashMap<PeerID, PeerAdvertisement> peers;
	private javax.swing.Timer ceas;
	private int nrCautari;
	private String numePeer;

	public Peers(PeerGroup defaultPeerGroup)
	{
		listenerList = new EventListenerList();
		discovery = defaultPeerGroup.getDiscoveryService();
		peers = new HashMap<PeerID, PeerAdvertisement>();
		ceas = new javax.swing.Timer(intarziere, this);
	}

	/**
	 * Cauta toate peer-urile din defaultPeerGroup.
	 */
	public void search()
	{
		search(null);
	}
	/**
	 * Cauta peer-urile care au numele specificat.
	 * 
	 * @param peerName
	 *            numele peer-ului de cautat
	 */
	public void search(String peerName)
	{
		if (ceas.isRunning()) stopSearch();

		numePeer = peerName;
		searchLocal("Name", numePeer);
		// searchLocal(null,null);
		if (peers.size() < nrMaxPeers)
		{
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
		System.out.println("Info: S-a oprit cautarea.");
		fireSearchFinished();
	}

	/**
	 * Adauga peer-urile gasite in lista.
	 * 
	 * @param advs
	 */
	private void addPeers(Enumeration<Advertisement> advs)
	{
		if (advs != null)
		{
			int aux = peers.size();
			while (peers.size() < nrMaxPeers && advs.hasMoreElements())
			{
				Advertisement item = advs.nextElement();
				if (item instanceof PeerAdvertisement)
				{
					peers.put(((PeerAdvertisement) item).getPeerID(), (PeerAdvertisement) item);
					System.out.println("Info: A fost gasit:" + ((PeerAdvertisement) item).getName());
				}
			}
			if (peers.size() != aux) fireContentChanged();
		}
	}

	/**
	 * Cauta in exterior peer-uri care au atributul attr cu valoarea val.
	 * 
	 * @param attr
	 * @param val
	 */
	private void searchRemote(String attr, String val)
	{
		discovery.getRemoteAdvertisements(null, DiscoveryService.PEER, attr, val, nrMaxPeers, this);
	}

	/**
	 * Cauta in cache peer-uri care au atributul attr cu valoarea val.
	 * 
	 * @param attr
	 * @param val
	 */
	private void searchLocal(String attr, String val)
	{
		Enumeration<Advertisement> rez;
		try
		{
			rez = discovery.getLocalAdvertisements(DiscoveryService.PEER, attr, val);
			addPeers(rez);
		}
		catch (IOException e)
		{
			System.out.println("Eroare: nu s-au putut citi cache-ul local.");
		}
	}

	/**
	 * Sterge peer-urile stocate local.
	 */
	public void flush()
	{
		try
		{
			Enumeration<Advertisement> eachAdv = discovery.getLocalAdvertisements(
					DiscoveryService.PEER, null, null);
			while (eachAdv.hasMoreElements())
			{
				Advertisement anAdv = (Advertisement) eachAdv.nextElement();
				discovery.flushAdvertisement(anAdv);
			}
		}
		catch (IOException e)
		{
			System.out.println("Eroare: nu s-au putut sterge peer advertisements.");
		}
	}

	/**
	 * @return lista de peer-uri
	 */
	public HashMap<PeerID, PeerAdvertisement> getPeers()
	{
		return new HashMap<PeerID, PeerAdvertisement>(peers);
	}

	/**
	 * Se ocupa cu evenimentul generat de gasirea unui peer.
	 */
	public void discoveryEvent(DiscoveryEvent event)
	{
		DiscoveryResponseMsg rez = event.getResponse();
		System.out.println("Info: DiscoveryEvent de la peer");
		addPeers(rez.getAdvertisements());
	}

	/**
	 * Se ocupa cu evenimentele generate de ceas.
	 */
	public void actionPerformed(ActionEvent event)
	{
		nrCautari++;
		if (nrCautari > nrMaxCautari || peers.size() == nrMaxPeers) stopSearch();
		else
		{
			System.out.println("Cautarea nr " + nrCautari);
			searchRemote("Name", numePeer);
			// searchRemote(null,null);
		}
	}

	/**
	 * Adauga un <code>P2PListener</code> la clasa Peers.
	 */
	public synchronized void addP2PListener(P2PListener listener)
	{
		listenerList.add(P2PListener.class, listener);
	}

	/**
	 * Sterge <code>P2PListener</code> de la clasa Peers.
	 */
	public synchronized void removeP2PListener(P2PListener listener)
	{
		listenerList.remove(P2PListener.class, listener);
	}

	/**
	 * Notifica schimbarea continutului listei peers.
	 */
	private synchronized void fireContentChanged()
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.PEER_FOUND));
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
