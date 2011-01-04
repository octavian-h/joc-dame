package checkers.util;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractListModel;


/**
 * Se ocupa cu popularea unei liste.
 * 
 * @author Hasna Octavian-Lucian
 */
public class ModelLista extends AbstractListModel<Entry<String, String>>
{
	private ArrayList<Entry<String, String>> date;
	
	public ModelLista(HashMap<String, String> lista)
	{
		if(lista == null) date = new ArrayList<Entry<String, String>>();
		else setDate(lista);
	}
	
	private void setDate(HashMap<String, String> lista)
	{
		for(java.util.Map.Entry<String, String> item: lista.entrySet())
		{
			date.add(new Entry<String, String>(item.getKey(), item.getValue()));
		}
	}
	
	/**
	 * Schimba sursa listei.
	 */
	public void schimbaLista(HashMap<String, String> listaNoua)
	{
		if (listaNoua != null)
		{
			setDate(listaNoua);
			fireContentsChanged(this, 0, listaNoua.size());
		}
	}
	
	/**
	 * Goleste listei.
	 */
	public void clear()
	{
		int aux = date.size();
		date.clear();
		fireContentsChanged(this, 0, aux);
	}
	
	/**
	 * @return valoarea de la indexul specificat.
	 */
	public Entry<String, String> getElementAt(int index)
	{
		if(index >= 0 && date.size() > index)
		{
			return date.get(index);
		}
		return null;
	}

	/**
	 * @return lungimea listei.
	 */
	public int getSize() 
	{
		return date.size();
	}
}
