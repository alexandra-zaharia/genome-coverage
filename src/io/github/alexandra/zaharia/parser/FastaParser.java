package io.github.alexandra.zaharia.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * La classe <code>FastaParser</code> sert à parcourir un fichier multi-fasta
 * afin de récupérer les génomes et leurs identifiants qui y sont contenus.
 * <p>
 * Toutes les paires (identifiant, génome) sont renvoyées par la méthode 
 * <code>parse</code> de cette classe sous la forme d'un <code>HashMap</code>
 * associant des clés sous la forme de chaînes de caractères à des valeurs sous
 * la forme de chaînes de caractères.
 */
public class FastaParser extends Parser {	
	/**
	 * Constructeur de la classe.
	 * <p>
	 * Initialise la variable d'instance <code>input</code> au fichier  
	 * multi-fasta à io.github.alexandra.zaharia.parser, spécifié par la chaîne de caractères
	 * <code>file</code>. Le constructeur s'occupe aussi de vérifier que la 
	 * chaîne de caractères <code>file</code> est valide et que le fichier
	 * désigné par celle-là existe et qu'il est un fichier normal non-vide
	 * qui peut être lu.
	 * <p>
	 * @param file chaîne de caractères spécifiant le chemin vers le fichier
	 * multi-fasta
	 * 
	 * @throws IOException si le fichier désigné par la chaîne de caractères
	 * <code>file</code> n'existe pas, s'il n'est pas un fichier normal, s'il
	 * ne peut pas être lu ou s'il est vide
	 * 
	 * @throws NullPointerException si la chaîne de caractères <code>file</code>
	 * vaut <code>null</code> ou si elle est vide 
	 */
	public FastaParser(String file) throws IOException {
		super(file);
	}
	
	
	/**
	 * Parcourt le fichier multi-fasta <code>input</code> et remplit un objet de
     * type <code>HashMap</code> avec les génomes et leurs identifiants. Une
     * référence vers le <code>HashMap</code> sera renvoyée à la méthode
     * appelante.
	 * <p>
	 * Le <code>HashMap</code> créé et renvoyé par cette méthode possède pour 
	 * clés les identifiants des génomes, et pour valeurs les génomes contenus 
	 * dans le fichier désigné par <code>input</code>. 
	 * 
	 * @return référence vers un objet de type <code>HashMap</code> contenant 
	 * les identifiants des génomes et les génomes eux-mêmes
	 * 
	 * @throws IllegalArgumentException si le fichier à io.github.alexandra.zaharia.parser n'est pas au
	 * format multi-fasta, s'il contient des identifiants dupliqués ou si l'on 
	 * essaie d'ajouter au <code>HashMap</code> une clé ou une valeur vide
	 * 
	 * @throws IOException si une erreur de type I/O est rencontrée lors du 
	 * parsing du fichier de données
	 *
	 * @throws NullPointerException si le <code>HashMap</code> à renvoyer à la
	 * méthode appelante est vide en fin d'exécution
	 */
	public Map<String, String> parse() throws IOException {
		Map<String, String> genomes = new HashMap<String, String>();
		String key = ""; // identifiant du génome : clé du HashMap
		Scanner sc = new Scanner(input);
			
		/* Pour récupérer chaque génome, il faut concaténer tous les caractères
		 * compris entre deux symboles '>'. Pour ce faire et par souci
		 * d'efficacité, on construit manuellement un objet de type
		 * StringBuilder. Entre le i-ème génome et le génome i+1, le
		 * StringBuilder est vidé. Le dernier génome est récupéré à part.
		 */
		StringBuilder sb = new StringBuilder();
	
		while (sc.hasNextLine()) {
			String line = sc.nextLine(); 
			if (!line.trim().isEmpty()) {
				if (line.startsWith(">")) { // nouveau génome
					if (sb.length() != 0) { // si on n'est pas sur la 1e ligne
						if (genomes.containsKey(key)) {
							sc.close();
							throw new IllegalArgumentException(
								"FastaParser.parse(): la clé " + key + 
								" est déjà présente dans le HashMap."
							);
						}
						if (key.equals("")) {
							sc.close();
							throw new IllegalArgumentException(
								"FastaParser.parse(): un identifiant ne peut " + 
								"pas être vide ou nul."
							);
						}
						if (sb.toString().equals("")) {
							sc.close();
							throw new IllegalArgumentException(
								"FastaParser.parse(): un génome ne peut pas " + 
								"être vide."
							);
						}
						genomes.put(key, sb.toString());
						sb.delete(0, sb.length());
					}
					
					key = line.substring(1); // on enlève le préfixe '>'
					key = key.split("\\s+")[0]; // ID du génome
					if (key.endsWith("|")) // on supprime le dernier '|'
					    key = key.substring(0, key.length() - 1);
				} else { // en train de lire un génome
				    sb.append(line);
                }
			}
		}

		sc.close();
		
		if (genomes.containsKey(key))
			throw new IllegalArgumentException(
				"FastaParser.parse(): la clé " + key + 
				" est déjà présente dans le HashMap."
			);
		if (key.equals(""))
			throw new IllegalArgumentException(
				"FastaParser.parse(): un identifiant ne peut pas être vide. " +
				"Le fichier n'est pas au format multi-fasta."
			);

		genomes.put(key, sb.toString()); // dernier génome lu

		return genomes;
	}
}