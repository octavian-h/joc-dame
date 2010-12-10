package checkers.p2p.event;

public class P2PEvent extends java.util.EventObject
{
	public static final int PEER_FOUND = 1;
	public static final int GROUP_FOUND = 2;
	public static final int SEARCH_FINISHED = 0;
	public static final int MESSAGE_RECEIVED = 3;
	
	private int tip;
	
	public P2PEvent(Object source)
	{
		super(source);
		setTip(0);
	}
	
	public P2PEvent(Object source, int tip)
	{
		super(source);
		this.setTip(tip);
	}

	private void setTip(int tip)
	{
		this.tip = tip;
	}

	public int getTip()
	{
		return tip;
	}
}
