package checkers;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import checkers.gui.MainFrame;

/**
 * Clasa Main folosita pentru lansarea aplicatiei.
 *  
 * @author Hasna Octavian-Lucian
 */
public class Main
{
	public static void main(String[] args)
	{
		MainFrame fp = new MainFrame();
		fp.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
	}
}
