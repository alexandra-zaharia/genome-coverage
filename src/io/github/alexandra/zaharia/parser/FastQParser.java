package io.github.alexandra.zaharia.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * La classe <code>FastQParser</code> sert à parcourir un fichier FastQ afin de
 * récupérer les <i>reads</i> et leurs identifiants qui y sont contenus.
 * <p>
 * Toutes les paires (identifiant, <i>read</i>) sont renvoyées par la méthode 
 * <code>parse</code> de cette classe sous la forme d'un <code>HashMap</code>
 * associant des clés sous la forme de chaînes de caractères à des valeurs sous
 * la forme de chaînes de caractères.
 */
public class FastQParser extends Parser {
    /**
     * Constructeur de la classe.
     * <p>
     * Initialise la variable d'instance <code>input</code> au fichier
     * FastQ à io.github.alexandra.zaharia.parser, spécifié par la chaîne de caractères
     * <code>file</code>. Le constructeur s'occupe aussi de vérifier que la
     * chaîne de caractères <code>file</code> est valide et que le fichier
     * désigné par celle-là existe et qu'il est un fichier normal non-vide
     * qui peut être lu.
     * <p>
     * @param file chaîne de caractères spécifiant le chemin vers le fichier
     * FastQ
     *
     * @throws IOException si le fichier désigné par la chaîne de caractères
     * <code>file</code> n'existe pas, s'il n'est pas un fichier normal, s'il
     * ne peut pas être lu ou s'il est vide
     *
     * @throws NullPointerException si la chaîne de caractères <code>file</code>
     * vaut <code>null</code> ou si elle est vide
     */
    public FastQParser(String file) throws IOException {
        super(file);
    }


    /**
     * Parcourt le fichier FastQ <code>input</code> et remplit un objet de type
     * <code>HashMap</code> avec les <i>reads</i> et leurs identifiants. Une
     * référence vers le <code>HashMap</code> sera renvoyée à la méthode
     * appelante.
     * <p>
     * La méthode effectue des tests pour chaque ligne lue dans le fichier FastQ
     * pour s'assurer que le fichier respecte la spécification de ce format.
     * Ainsi, pour chaque bloc de quatre lignes, il faut que la première ligne
     * lue commence par un symbole '@', que la troisième commence par un symbole
     * '+', et il faut que la deuxième et la quatrième ligne aient la même
     * longueur. De plus, il faut que le fichier d'entrée contienne un nombre de
     * lignes qui soit multiple de quatre, avec chaque ligne dans un tel bloc
     * respectant les contraintes ci-dessus. Si ce n'est pas le cas, une
     * exception de type I/O est levée.
     *
     * @return référence vers un objet de type <code>HashMap</code> contenant
     * les identifiants des <i>reads</i> et les <i>reads</i> eux-mêmes
     *
     * @throws IllegalArgumentException si le fichier à io.github.alexandra.zaharia.parser contient des
     * identifiants dupliqués ou si l'on essaie d'ajouter au
     * <code>HashMap</code> une clé ou une valeur vide
     *
     * @throws IOException si une erreur de type I/O a lieu, ou si le fichier
     * d'entrée ne respecte pas la spécification FastQ
     */
    public Map<String, String> parse() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(input));
        Map<String, String> reads = new HashMap<String, String>();

        while (br.ready()) {
            String id;
            do { // on ignore les lignes vides
                id = br.readLine();
                if (!br.ready()) {
                    br.close();
                    throw new IOException(
                        "FastQParser.parse(): fin du fichier non attendue."
                    );
                }
            } while (id.equals(""));

            // La première ligne contient l'identifiant du read.
            if (!id.startsWith("@")) {
                br.close();
                throw new IOException(
                    "FastQParser.parse(): l'identifiant de l'une des " +
                    "séquences ne commence pas par un '@'."
                );
            }
            id = id.substring(1).split("\\s+")[0];

            // La deuxième ligne contient le read.
            if (!br.ready()) {
                br.close();
                throw new IOException(
                    "FastQParser.parse(): fin du fichier non attendue."
                );
            }
            String read = br.readLine();

            // La troisième ligne doit commencer par un symbole '+'.
            if (!br.ready()) {
                br.close();
                throw new IOException(
                    "FastQParser.parse(): fin du fichier non attendue."
                );
            }
            String thirdLine = br.readLine();
            if (!thirdLine.startsWith("+")) {
                br.close();
                throw new IOException(
                    "FastQParser.parse(): la troisième ligne d'un bloc " +
                    "supposé de format FastQ ne commence pas par un '+'."
                );
            }

            // Il faut que le read et la quatrième ligne aient la même longueur.
            if (!br.ready()) {
                br.close();
                throw new IOException(
                    "FastQParser.parse(): fin du fichier non attendue."
                );
            }
            String fourthLine = br.readLine();
            if (read.length() != fourthLine.length()) {
                br.close();
                throw new IOException(
                    "FastQParser.parse(): dans un bloc supposé de format " +
                    "FastQ, le read et la ligne de scores de qualité " +
                    "associée n'ont pas la même longueur."
                );
            }

            if (reads.containsKey(id)) {
                br.close();
                throw new IllegalArgumentException(
                    "FastQParser.parse(): la clé " + id +
                    " est déjà présente dans le HashMap."
                );
            }
            if (id == null || id.equals("")) {
                br.close();
                throw new IllegalArgumentException(
                    "FastQParser.parse(): un identifiant ne peut pas être " +
                    "vide. Le fichier n'est pas au format FastQ."
                );
            }
            reads.put(id, read);
        }

        br.close();
        return reads;
    }
}