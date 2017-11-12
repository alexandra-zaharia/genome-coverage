package io.github.alexandra.zaharia.parser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * La classe <code>Parser</code> est une classe abstraite étendue
 * actuellement par les classes {@link FastaParser} et {@link FastQParser}.
 * Elle sert de point de départ pour toute classe permettant de io.github.alexandra.zaharia.parser
 * un fichier de données génomiques.
 */
public abstract class Parser {
	/**
	 * Fichier de données à io.github.alexandra.zaharia.parser.
	 */
	protected final File input;
	
	
	/**
	 * Constructeur de la classe.
	 * <p>
	 * Initialise la variable d'instance <code>input</code> au fichier de 
	 * données à io.github.alexandra.zaharia.parser, spécifié par la chaîne de caractères <code>file</code>.
	 * Le constructeur s'occupe aussi de vérifier que la chaîne de caractères
	 * <code>file</code> est valide et que le fichier désigné par celle-là
	 * existe et qu'il est un fichier normal non-vide qui peut être lu.
	 * 
	 * @param file chaîne de caractères spécifiant le chemin vers le fichier
	 * de données à io.github.alexandra.zaharia.parser
	 * 
	 * @throws IOException si le fichier désigné par la chaîne de caractères
	 * <code>file</code> n'existe pas, s'il n'est pas un fichier normal, s'il
	 * ne peut pas être lu ou s'il est vide
	 * 
	 * @throws NullPointerException si la chaîne de caractères <code>file</code>
	 * vaut <code>null</code> ou si elle est vide
	 */
	public Parser(String file) throws IOException {
		if (file == null || file.length() == 0)
			throw new NullPointerException(
				"FastaParser.parse(): le nom du fichier est nul ou vide."
			);
		
		input = new File(file);
		
		if (!input.exists())
			throw new IOException(
				"FastaParser.parse(): le fichier " + file + " n'existe pas."
			);
		
		if (!input.isFile())
			throw new IOException(
				"FastaParser.parse(): le fichier " + file + " n'est pas un " +
				"fichier normal."
			);
		
		if (!input.canRead()) 
			throw new IOException(
				"FastaParser.parse(): le fichier " + file + " ne peut pas " +
				"être lu."
			);
		
		if (input.length() == 0)
			throw new IOException(
				"FastaParser.parse(): le fichier " + file + " est vide."
			);
	}
	
	
	/**
	 * L'implémentation de cette méthode doit faire en sorte d'extraire les
	 * informations pertinentes à partir du fichier de données 
	 * <code>input</code> et de renvoyer en fin d'exécution un 
	 * <code>HashMap</code> associant des clés sous la forme de chaînes de 
	 * caractères à des valeurs sous la forme de chaîne de caractères, chaque
	 * tel couple représentant une paire (identifiant, séquence) d'intérêt du
	 * fichier de données à io.github.alexandra.zaharia.parser.
	 * 
	 * @return référence vers un <code>HashMap</code> associant des chaînes
	 * de caractères représentant les identifiants des séquences à des chaînes
	 * de caractères représentant les séquences désignées par ces identifiants
	 * 
	 * @throws IOException si une erreur de type I/O est rencontrée lors du
	 * parsing du fichier de données
	 */
	public abstract Map<String, String> parse() throws IOException;
}
