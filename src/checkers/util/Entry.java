package checkers.util;

import java.util.Map;

/**
 * Creeaza o pereche (cheie, valoare) cu operatiile aferente.
 * 
 * @author Hasna Octavian-Lucian
 * @version 2010
 */
public class Entry<K, V> implements Map.Entry<K, V>
{
	final private K cheie;
	final private V valoare;

	/**
	 * Construieste o pereche (cheie, valoare).
	 * 
	 * @param cheie
	 * @param valoare
	 */
	public Entry(K cheie, V valoare)
	{
		assert (cheie != null);
		assert (valoare != null);
		this.cheie = cheie;
		this.valoare = valoare;
	}

	/**
	 * @return cheia
	 */
	public K getKey()
	{
		return cheie;
	}

	/**
	 * @return valoarea
	 */
	public V getValue()
	{
		return valoare;
	}

	public String toString()
	{
		return valoare.toString();
	}

	// neutilizat
	public V setValue(V valoare)
	{
		return null;
	}
}
