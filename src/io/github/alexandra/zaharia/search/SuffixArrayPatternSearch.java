package io.github.alexandra.zaharia.search;

import io.github.alexandra.zaharia.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


/**
 * La classe <code>SuffixArrayPatternSearch</code> étend la classe abstraite
 * {@link PatternSearch}, permettant de trouver toutes les occurrences exactes 
 * (avec chevauchement) d'un motif nucléotidique dans un ensemble de génomes par
 * recherche par tableau de suffixes.
 * <p>
 * Toutes les occurrences exactes de chaque motif sont trouvées (avec
 * chevauchement) dans chacun des génomes transmis à l'instance de cette classe
 * au moment de la création de l'objet.
 */
public class SuffixArrayPatternSearch extends PatternSearch {
    /**
     * Constructeur de la classe.
     *
     * @param fastaFile chaîne de caractères spécifiant le chemin vers le
     * fichier multi-fasta contenant les génomes et leurs identifiants
     * 
     * @param fastqFile chaîne de caractères spécifiant le chemin vers le 
     * fichier FastQ contenant les <i>reads</i> et leurs identifiants
     * 
     * @throws IOException si l'une des chaînes de caractères
     * <code>fastaFile</code> ou <code>fastqFile</code> vaut <code>null</code>
     * ou est vide, ou si le fichier indiqué par cette chaîne n'existe pas, 
     * s'il n'est pas un fichier normal, s'il ne peut pas être lu ou s'il est
     * vide
     * 
     * @throws IllegalArgumentException si l'un des deux <code>HashMap</code> 
     * renvoyés par les méthodes <code>parse</code> des classes héritant de
     * {@link Parser} est vide ou s'il contient
     * des clés ou valeurs illégales
     * 
     * @throws NullPointerException si l'un des deux <code>HashMap</code> 
     * renvoyés par les méthodes <code>parse</code> des classes héritant de
     * {@link Parser} vaut <code>null</code>
     */
	public SuffixArrayPatternSearch(String fastaFile, String fastqFile)
            throws IOException {
		super(fastaFile, fastqFile);
	}
	
	
	/**
	 * Détermine par recherche par tableau de suffixes toutes les occurrences
     * (avec chevauchement) de chaque <i>read</i> dans <code>reads</code> parmi
     * l'ensemble des génomes dans <code>genomes</code>.
     * <p>
     * La structure de données associée pour stocker les résultats de la
     * recherche des motifs par la recherche par tableau de suffixes est un
     * <code>ArrayList</code> d'objets de type {@link SearchResults}. C'est une
     * variable d'instance héritée de la classe {@link PatternSearch}, et sera
     * renvoyée en fin d'exécution.
     *  
     * @return référence vers un <code>ArrayList</code> d'objets de type 
     * {@link SearchResults} contenant les résultats de la recherche
	 */
	public ArrayList<SearchResults> search() {
	    /* Pour chaque génome désigné par son identifiant dans la variable 
	     * d'instance 'genomes', on crée un objet de type SuffixArray qui sera
	     * utilisé dans la recherche des occurrences de chaque 'read', dans le
	     * sens direct en inverse.
	     */
		for (Map.Entry<String, String> genome : genomes.entrySet()) {
			SuffixArray suffix = new SuffixArray(genome.getValue());
			for (Map.Entry<String, String> read : reads.entrySet()) {
				ArrayList<Integer> forwardOccurrences = 
					findOccurrences(read.getValue(), suffix);
				if (!forwardOccurrences.isEmpty()) {
					SearchResults sr = new SearchResults(
					        genome.getKey(),
                            read.getKey(),
                            read.getValue().length(),
                            true,
                            forwardOccurrences
					);
					results.add(sr);
				}

				ArrayList<Integer> reverseOccurrences = 
					findOccurrences(reverseComplement(read.getValue()), suffix);
				if (!reverseOccurrences.isEmpty()) {
					SearchResults sr = new SearchResults(
					        genome.getKey(),
                            read.getKey(),
                            read.getValue().length(),
                            false,
                            reverseOccurrences
					);
					results.add(sr);
				}
			}
		}
		return results;
	}
	
	
	/**
	 * Détermine les occurrences du <i>read</i> {@code query} dans le génome
     * dont le tableau de suffixes est {@code suffix}.
	 * 
	 * @param query chaîne de caractères désignant le <i>read</i> dont on
     * souhaite déterminer toutes les occurrences dans le cadre d'un génome
     * représenté par le tableau de suffixes {@code suffix}
	 * 
	 * @param suffix référence vers l'objet de type {@code SuffixArray}
     * représentant le tableau de suffixes du génome dans lequel on souhaite
     * déterminer toutes les occurrences de la chaîne de caractères
     * {@code query}
	 * 
	 * @return {@code ArrayList} d'entiers donnant toutes les occurrences de la
     * chaîne {@code query} dans le génome représenté par le tableau de suffixes
     * {@code suffix}
	 */
	private ArrayList<Integer> findOccurrences(
	        String query, SuffixArray suffix) {
		ArrayList<Integer> occ = new ArrayList<Integer>();
		int k = binarySearch(query, suffix, 0, suffix.length()-1, -1, -1);
		if (k != -1) {
			int i = k;
			occ.add(suffix.index(k));
			while (i > 0 && suffix.lcp(i) >= query.length())
				occ.add(suffix.index(--i));
			i = k;
			while (i < suffix.length()-1 && suffix.lcp(++i) >= query.length())
				occ.add(suffix.index(i));
		}        
		Collections.sort(occ);
		return occ;
	}


    /**
     * Détermine par recherche dichotomique la position des occurrences du
     * <i>read</i> {@code query} dans un génome représenté par le tableau de
     * suffixes {@code suffix}, entre les indices {@code lo} et {@code hi} du
     * génome.
     * <p>
     * Cette version de recherche dichotomique utilise comme amélioration
     * l'information donnée par les plus longs préfixes propres (entre le
     * <i>read</i> et un suffixe ou entre deux suffixes). À chaque appel, cette
     * méthode récursive a besoin de connaître la valeur de l'indice situé à
     * mi-chemin entre les {@code lo} et {@code hi} précédents, ainsi que la
     * longueur du dernier préfixe commun. Pour distinguer le premier appel de
     * tous les autres, {@code oldMid} et {@code oldCp} auront chacun la valeur
     * initiale -1.
     *
     * @param query chaîne de caractères désignant le <i>read</i> dont on
     * souhaite déterminer toutes les occurrences dans le cadre d'un génome
     * représenté par le tableau de suffixes {@code suffix}
     *
     * @param suffix référence vers l'objet de type {@code SuffixArray}
     * représentant le tableau de suffixes du génome dans lequel on souhaite
     * déterminer toutes les occurrences de la chaîne de caractères
     * {@code query}
     *
     * @param lo premier indice dans le génome où une occurrence de
     * {@code query} peut être trouvée
     *
     * @param hi dernier indice dans le génome où une occurrence de
     * {@code query} peut être trouvée
     *
     * @param oldMid valeur du dernier indice calculé à mi-chemin entre
     * {@code lo} et {@code hi}
     *
     * @param oldCp la dernière longueur calculée du plus long préfixe propre
     * (entre {@code query} et un suffixe ou entre deux suffixes)
     *
     * @return indice dans le génome où une occurrence de {@code query} a été
     * trouvée; la méthode appelante doit se charger de parcourir le tableau de
     * suffixes pour déterminer les événtuelles occurrences supplémentaires
     */
    private int binarySearch(
            String query, SuffixArray suffix,
            int lo, int hi,
            int oldMid, int oldCp) {
        if (lo > hi) return -1;
        int mid = lo + (hi - lo) / 2;
        if (oldMid == -1) { // le premier appel de binarySearch
            int cmp = suffix.compare(query, suffix.index(mid));
            if (cmp == 0) return mid;

            int cp = suffix.lcp(query, suffix.index(mid));
            if (cmp < 0 && cp >= query.length()) return mid;

            int newLo = cmp < 0 ? lo : mid + 1;
            int newHi = cmp < 0 ? mid - 1 : hi;
            return binarySearch(query, suffix, newLo, newHi, mid, cp);
        } else {
            int midCp = suffix.lcp(suffix.index(mid), suffix.index(oldMid));
            if (midCp == oldCp) {
                int cp = suffix.lcp(query, suffix.index(mid));
                if (cp >= query.length()) return mid;
                int cmp = suffix.compare(
                        query.substring(cp), suffix.index(mid) + cp);
                int newLo = cmp < 0 ? lo : mid + 1;
                int newHi = cmp < 0 ? mid - 1 : hi;
                return binarySearch(
                        query, suffix,
                        newLo, newHi,
                        cp > oldCp ? mid : oldMid,
                        cp > oldCp ? cp : oldCp
                );
            } else {
                int newLo, newHi;
                if (midCp < oldCp) {
                    newLo = mid < oldMid ? mid + 1 : lo;
                    newHi = mid < oldMid ? hi : mid - 1;
                } else {
                    newLo = mid < oldMid ? lo : mid + 1;
                    newHi = mid < oldMid ? mid - 1 : hi;
                }
                return binarySearch(query, suffix, newLo, newHi, oldMid, oldCp);
            }
        }
    }
}