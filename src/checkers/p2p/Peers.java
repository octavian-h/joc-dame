package checkers.p2p;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.event.EventListenerList;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.*;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

import checkers.p2p.event.*;

/**
 * Operatii pe parteneri.
 * 
 * @author Hasna Octavian-Lucian
 */
public class Peers implements DiscoveryListener, ActionListener, PipeMsgListener,
		OutputPipeListener
{
	private final static int nrMaxPeers = 10;
	private final static int nrMaxCautari = 5;
	private final static int intervalCautari = 1000;
	private final static String pid = "urn:jxta:uuid-59616261646162614E504720503250338BDD512C72FE462EAE54E9948FF4C23E04";
	private PipeAdvertisement pipeAdv;

	private EventListenerList listenerList;

	private DiscoveryService discovery;
	private PipeService pipeService;

	private HashMap<String, String> peers;
	private javax.swing.Timer ceas;
	private int nrCautari;
	private String numePeer;

	private PeerID peerID;
	private String peerName;
	private InputPipe inputPipe;
	private OutputPipe outputPipe;

	private boolean isRunning;

	/**
	 * Constructorul pentru clasa Peers.
	 * 
	 * @param defaultPeerGroup
	 *            grupul in care se afla partenerii
	 */
	public Peers(PeerGroup defaultPeerGroup)
	{
		listenerList = new EventListenerList();

		discovery = defaultPeerGroup.getDiscoveryService();
		pipeService = defaultPeerGroup.getPipeService();

		peers = new HashMap<String, String>();
		ceas = new javax.swing.Timer(intervalCautari, this);
		peerID = defaultPeerGroup.getPeerID();
		peerName = defaultPeerGroup.getPeerName();
		pipeAdv = getPipeAdvertisement();

		isRunning = false;
	}

	/**
	 * Creeaza un pipe advertisement cu pipeID format din defaultPeerGroup
	 * 
	 * @return pipe advertisement
	 */
	public static PipeAdvertisement getPipeAdvertisement()
	{
		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());

		PipeID pipeID = null;
		try
		{
			pipeID = (PipeID) IDFactory.fromURI(new URI(pid));
		}
		catch (URISyntaxException e)
		{
			System.out.println("Eroare (Peers): peer id incorect!");
		}
		advertisement.setPipeID(pipeID);
		advertisement.setType(PipeService.PropagateType);
		advertisement.setName("CheckerPipe");
		return advertisement;
	}

	/**
	 * Anunta prezenta partenerului in retea P2P. Durata de viata = timpul de
	 * expirare = 2 min
	 */
	public void announce()
	{
		announce(60 * 2 * 1000, 60 * 2 * 1000);
	}

	/**
	 * Anunta prezenta partenerului in retea P2P.
	 * 
	 * @param lifetime
	 *            durata de existenta a acestui advertisement
	 * @param expiration
	 *            durata de pastrare a acestui advertisement de catre ceilalti
	 *            parteneri
	 */
	public void announce(final long lifetime, final long expiration)
	{
		try
		{
			discovery.publish(pipeAdv);//, lifetime, expiration);
			discovery.remotePublish(peerID.toString(), pipeAdv);//, expiration);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			try
			{
				announce();
				inputPipe = pipeService.createInputPipe(pipeAdv, this);
				// outputPipe = pipeService.createOutputPipe(pipeAdv, 1000);
				pipeService.createOutputPipe(pipeAdv, this);
			}
			catch (IOException e)
			{
				System.out.println("Eroare (Peers): Nu s-a putut crea input/output pipe.");
			}

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
			peers.clear();
			outputPipe.close();
			outputPipe = null;
			inputPipe.close();
			inputPipe = null;
			discovery.removeDiscoveryListener(this);
		}
	}

	/**
	 * Cauta toti partenerii din defaultPeerGroup.
	 */
	public void search()
	{
		search(null);
	}

	/**
	 * Cauta partenerii care au numele specificat.
	 * 
	 * @param peerName
	 *            numele partenerului de cautat
	 */
	public void search(String peerName)
	{
		if (ceas.isRunning()) stopSearch();

		announce();
		numePeer = peerName;
		System.out.println("Info (Peers): Cautare locala.");
		searchLocal("Name", numePeer);
		if (peers.size() < nrMaxPeers)
		{
			System.out.println("Info (Peers): Cautare externa.");
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
		System.out.println("Info (Peers): S-a oprit cautarea.");
		
		
		Thread t = new Thread()
		{
			public void run()
			{
				fireSearchFinished(getPeers());
			}
		};
		t.start();
	}

	/**
	 * Adauga partenerii gasiti in lista.
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
					PeerAdvertisement pa = (PeerAdvertisement) item;
					if(!pa.getPeerID().equals(peerID))
					{
						peers.put(pa.getPeerID().toString(), pa.getName());
						System.out.println("Info (Peers): A fost gasit:" + pa.getName());
					}
					else System.out.println("Info (Peers): Adv-ul meu");
				}
			}
			if (peers.size() != aux)
			{
				Thread t = new Thread()
				{
					public void run()
					{
						fireContentChanged(getPeers());
					}
				};
				t.start();
			}
		}
	}

	/**
	 * Cauta in exterior parteneri care au atributul attr cu valoarea val.
	 * 
	 * @param attr
	 * @param val
	 */
	private void searchRemote(String attr, String val)
	{
		discovery.getRemoteAdvertisements(null, DiscoveryService.PEER, attr, val, nrMaxPeers);
	}

	/**
	 * Cauta in cache parteneri care au atributul attr cu valoarea val.
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
			System.out.println("Eroare (Peers): nu s-au putut citi cache-ul local.");
		}
	}

	/**
	 * Sterge partenerii stocati local.
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
			peers.clear();
		}
		catch (IOException e)
		{
			System.out.println("Eroare (Peers): nu s-au putut sterge peer advertisements.");
		}
	}

	/**
	 * @return lista de parteneri
	 */
	public HashMap<String, String> getPeers()
	{
		return new HashMap<String, String>(peers);
	}

	/**
	 * Se ocupa cu evenimentul generat de gasirea unui partener.
	 */
	public void discoveryEvent(DiscoveryEvent event)
	{
		DiscoveryResponseMsg rez = event.getResponse();
		//System.out.println("Info (Peers): DiscoveryEvent");
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
			System.out.println("Info (Peers): Cautarea nr " + nrCautari);
			searchRemote("Name", numePeer);
		}
	}

	/**
	 * Trimite un mesaj prin output pipe.
	 * 
	 * @param toID id-ul partenerului la care se trimite mesajul
	 * @param message mesajul de trimis
	 * @return true daca s-a reusit trimiterea
	 */
	public boolean sendMessage(String toID, String message)
	{
		if (outputPipe != null)
		{
			Message msg = new Message();
			StringMessageElement senderID = new StringMessageElement("SenderID", peerID.toString(),	null);
			StringMessageElement senderName = new StringMessageElement("SenderName", peerName, null);
			StringMessageElement receiverID = new StringMessageElement("ReceiverID", toID, null);
			StringMessageElement data = new StringMessageElement("Data", message, null);
			msg.addMessageElement("CheckerMessage", senderID);
			msg.addMessageElement("CheckerMessage", senderName);
			msg.addMessageElement("CheckerMessage", receiverID);
			msg.addMessageElement("CheckerMessage", data);
			try
			{
				return outputPipe.send(msg);
			}
			catch (IOException e)
			{
				System.out.println("Eroare (Peers): nu s-a putut trimite mesajul.");
			}
		}
		return false;
	}

	/**
	 * Se ocupa cu evenimentele generate de primirea unui mesaj prin input pipe.
	 */
	public void pipeMsgEvent(PipeMsgEvent event)
	{
		Message msg = event.getMessage();
		System.out.println("Info (Peers): PipeMsgEvent");
		if (msg != null)
		{
			final MessageElement receiverID = msg.getMessageElement("CheckerMessage", "ReceiverID");
			if (receiverID != null && receiverID.toString().equals(peerID.toString()))
			{
				final MessageElement senderID = msg.getMessageElement("CheckerMessage", "SenderID");
				if (senderID != null && !senderID.toString().equals(peerID.toString()))
				{
					final MessageElement senderName = msg.getMessageElement("CheckerMessage",
							"SenderName");
					final MessageElement data = msg.getMessageElement("CheckerMessage", "Data");
					if (senderName != null && data != null)
					{

						System.out.println("Info (Peers): s-a primit un mesaj de la " + senderName
								+ " ce contine [" + data.toString() + "]");
						Thread t = new Thread()
						{
							public void run()
							{
								fireMessageReceived(senderID.toString(), senderName.toString(),
										data.toString());
							}
						};
						t.start();
					}
				}
			}
		}
	}

	/**
	 * Se ocupa cu evenimentul generat de crearea lui output pipe.
	 */
	public void outputPipeEvent(OutputPipeEvent event)
	{
		outputPipe = event.getOutputPipe();
		fireOutputPipeReady();
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
	 * Notifica schimbarea continutului listei de parteneri.
	 * 
	 * @param peersList
	 */
	private synchronized void fireOutputPipeReady()
	{
		P2PListener[] listeners = listenerList.getListeners(P2PListener.class);

		for (int i = listeners.length - 1; i >= 0; --i)
		{
			listeners[i].stateChanged(new P2PEvent(this, P2PEvent.PEER_READY));
		}
	}

	/**
	 * Notifica schimbarea continutului listei de parteneri.
	 * 
	 * @param peersList
	 */
	private synchronized void fireContentChanged(HashMap<String, String> peersList)
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
	private synchronized void fireSearchFinished(HashMap<String, String> peersList)
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
	 * @param senderID id-ul partenerului care a trimis
	 * @param senderName numele partenerului care a trimis
	 * @param data mesajul trimis
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
