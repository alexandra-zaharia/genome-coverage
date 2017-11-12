package io.github.alexandra.zaharia.search;

import io.github.alexandra.zaharia.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


/**
 * La classe <code>NaivePatternSearch</code> étend la classe abstraite
 * {@link PatternSearch}, permettant de trouver toutes les occurrences exactes 
 * (avec chevauchement) d'un motif nucléotidique dans un ensemble de génomes par
 * recherche naïve.
 * <p>
 * Toutes les occurrences exactes de chaque motif sont trouvées (avec
 * chevauchement) dans chacun des génomes transmis à l'instance de cette classe
 * au moment de la création de l'objet.
 */
public class NaivePatternSearch extends PatternSearch
{
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
	public NaivePatternSearch(String fastaFile, String fastqFile)
            throws IOException {
		super(fastaFile, fastqFile);
	}
	
	
	/**
	 * Détermine par recherche naïve toutes les occurrences (avec chevauchement)
	 * de chaque <i>read</i> dans <code>reads</code> parmi l'ensemble des
     * génomes dans <code>genomes</code>.
	 * <p>
	 * La structure de données associée pour stocker les résultats de la
	 * recherche des motifs par l'algorithme naïf est un <code>ArrayList</code>
	 * d'objets de type {@link SearchResults}. C'est une variable 
	 * d'instance héritée de la classe {@link PatternSearch}, et sera renvoyée
	 * en fin d'exécution. 
	 *  
	 * @return référence vers un <code>ArrayList</code> d'objets de type 
	 * {@link SearchResults} contenant les résultats de la recherche
	 */
	public ArrayList<SearchResults> search() {
		for (Map.Entry<String, String> read : reads.entrySet()) {
			char[] forwardRead = read.getValue().toCharArray();
			char[] reverseRead =
                    reverseComplement(read.getValue()).toCharArray();
			
			for (Map.Entry<String, String> genomeEntry : genomes.entrySet()) {
				char[] genome = genomeEntry.getValue().toCharArray();
				
				ArrayList<Integer> forwardOccurrences =
                        naiveAlgorithm(forwardRead, genome);
				if (!forwardOccurrences.isEmpty()) {
					SearchResults sr = new SearchResults(
					        genomeEntry.getKey(),
                            read.getKey(),
                            read.getValue().length(),
                            true,
                            forwardOccurrences
					);
					results.add(sr);
				}
				
				ArrayList<Integer> reverseOccurrences =
                        naiveAlgorithm(reverseRead, genome);
				if (!reverseOccurrences.isEmpty()) {
					SearchResults sr = new SearchResults(
					        genomeEntry.getKey(),
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
	 * Implémente la recherche naïve de toutes les occurrences (avec 
	 * chevauchement) du motif <code>read</code> dans le texte
     * <code>genome</code>.
	 *
	 * @param read tableau de caractères représentant le motif dont on souhaite
	 * déterminer toutes les occurrences dans le tableau de caractères 
	 * <code>genome</code>
	 * 
	 * @param genome tableau de caractères représentant le texte dans lequel on
	 * cherche toutes les occurrences du motif donné par le tableau de 
	 * caractères <code>read</code>
	 * 
	 * @return référence vers un <code>ArrayList</code> d'entiers contenant les
	 * positions de toutes les occurrences (avec chevauchement) du motif
	 * <code>read</code> dans le cadre du texte <code>genome</code>
	 * 
	 * @throws NullPointerException si <code>read</code> ou <code>genome</code>
	 * valent <code>null</code>  
	 * 
	 * @throws IllegalArgumentException si au moins un des tableaux de 
	 * caractères <code>read</code> et <code>genome</code> est de longuer zéro
	 * 
	 * @throws IndexOutOfBoundsException si la taille du <code>read</code> est
	 * supérieure à celle de <code>genome</code>
	 */
	private static ArrayList<Integer> naiveAlgorithm(
	        char[] read, char[] genome) {
		if (read == null || genome == null)
			throw new NullPointerException(
			        "NaivePatternSearch.naiveAlgorithm(): " +
                            "Le read ou le génome est nul."
            );
		
		if (read.length == 0 || genome.length == 0)
			throw new IllegalArgumentException(
				"NaivePatternSearch.naiveAlgorithm(): " +
                        "Le read ou le génome est vide."
			);
		
		if (read.length > genome.length) 
			throw new IndexOutOfBoundsException(
				"NaivePatternSearch.naiveAlgorithm(): " +
                        "La longueur du motif est supérieure " +
                        "à la longueur du génome."
			);
		
		ArrayList<Integer> occurrences = new ArrayList<Integer>();
		
		int i = 0; 			   // balaye le tableau de caractères 'genome'
		int j = 0;             // balaye le tableau de caractères 'read'
		int n = genome.length; // longueur du génome
		int m = read.length;   // longueur du read
		
		/* Tant qu'il est encore possible de trouver une occurrence du read dans
		 * le génome, on avance d'une position dans le génome et dans le read.
		 * Si une occurrence n'est pas possible, on revient en arrière avec i et
		 * j. Quand on trouve une occurrence, on recommence la recherche en
		 * début du motif et en revenant en arrière dans le génome jusqu'à la
		 * position immédiatement suivante au début de l'occurrence trouvée afin
		 * d'autoriser les chevauchements.
		 */
		while (i < n-m+1 || j != 0) {
			if (genome[i] == read[j]) {
				i++;
				j++;				
			} else {
				i = i-j+1;
				j = 0;
			}
			if (j == m) {
				occurrences.add(i-m);
				i = i-j+1;
				j = 0;
			}
		}		
		
		return occurrences;
	}
}
