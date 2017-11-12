package io.github.alexandra.zaharia.search;

import java.util.ArrayList;

/**
 * La classe <code>SearchResults</code> assure la structure de données
 * nécessaire à retenir les résultats de la recherche d'occurrences, de façon à
 * ce qu'ils puissent être exploités plus tard pour calculer les courbes de
 * couverture pour chaque génome.
 * <p>
 * Pour chaque <i>read</i> ayant au moins une occurrence dans au moins un des 
 * génomes dans le fichier d'entrée multi-fasta, l'objet {@code SearchResults}
 * associé contient son identifiant, l'identifiant du génome où il a au moins
 * une occurrence, la longueur du <i>read</i>, la liste d'occurrences, ainsi
 * que le sens de parcours du <i>read</i> pour déterminer ces occurrences (sens
 * direct ou <i>forward</i> ou sens inverse ou <i>reverse</i> pour la séquence
 * inversée et complémentée).
 */
public class SearchResults {
	private String genomeId;
	private String readId;
	private int readLength;
	private boolean forward = true;
	private ArrayList<Integer> occurrences;
	
	
	/**
	 * Constructeur de la classe.
	 */
	public SearchResults() {
	    occurrences = new ArrayList<Integer>();
	}
	
	
	/**
	 * Constructeur de la classe.
	 * <p>
	 * @param genomeId chaîne de caractères représentant l'identifiant du
	 * génome d'intérêt
	 * 
	 * @param readId chaîne de caractères représentant l'identifiant du 
	 * <i>read</i> d'intérêt
	 * 
	 * @param readLength entier représentant la longueur du <i>read</i>
     * d'intérêt
	 * 
	 * @param forward true si le <i>read</i> est dans le sens direct de lecture,
     * ou false sinon
	 * 
	 * @param occurrences <code>ArrayList</code> d'entiers contenant les indices
	 * des occurrences du <i>read</i> dans le génome
	 */
	public SearchResults(String genomeId, String readId, int readLength,
			boolean forward, ArrayList<Integer> occurrences) {
	    this();
		this.genomeId = genomeId;
		this.readId = readId;
		this.readLength = readLength;
		this.forward = forward;
		for (Integer o : occurrences) this.occurrences.add(o);

	}
		
	
	/**
	 * Renvoie l'identifiant du génome.
	 * 
	 * @return l'identifiant du génome
	 */
	public String getGenomeId() {
		return genomeId;
	}
	
	
	/**
	 * Renvoie l'identifiant du <i>read</i>.
	 * 
	 * @return l'identifiant du <i>read</i>
	 */
	public String getReadId() {
		return readId;
	}
	
	
	/**
	 * Renvoie la longueur du <i>read</i>.
	 * 
	 * @return la longueur du <i>read</i>
	 */
	public int getReadLength() {
		return readLength;
	}
	
	
	/**
	 * Détermine si le <i>read</i> est dans le sens direct (<i>forward</i>) ou
	 * inverse (<i>reverse</i>).
	 * 
	 * @return true si le <i>read</i> est dans le sens direct et false sinon
	 */
	public boolean isForward() {
		return forward;
	}
	
	
	/**
	 * Renvoie un {@code ArrayList} d'entiers contenant les position de
	 * toutes les occurrences du <i>read</i> dans le génome.
	 * 
	 * @return la liste des occurrences du <i>read</i> dans le génome
	 */
	public ArrayList<Integer> getOccurrences() {
		return occurrences;
	}
}