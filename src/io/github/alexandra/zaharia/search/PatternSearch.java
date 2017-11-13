package io.github.alexandra.zaharia.search;

import io.github.alexandra.zaharia.gui.GUIModel;
import io.github.alexandra.zaharia.parser.FastQParser;
import io.github.alexandra.zaharia.parser.FastaParser;
import io.github.alexandra.zaharia.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/**
 * La classe <code>PatternSearch</code> est une classe abstraite étendue
 * actuellement par les classes {@link NaivePatternSearch} et
 * {@link SuffixArrayPatternSearch}. Elle sert de point de départ pour toute
 * classe permettant de trouver toutes les occurrences exactes (avec 
 * chevauchement) d'un motif nucléotidique dans un ensemble de génomes.
 */
public abstract class PatternSearch {
    /**
     * <code>HashMap</code> qui contiendra pour clés les identifiants
     * des génomes et, pour valeurs, les génomes.
     * <p>
     * <code>genomes</code> sera initialisé au moment de la construction d'une
     * instance d'une classe héritant de <code>PatternSearch</code>.
     */
    protected final Map<String, String> genomes;

    /**
     * <code>HashMap</code> qui contiendra pour clés les identifiants des
     * <i>reads</i> et, pour valeurs, les <i>reads</i>.
     * <p>
     * <code>reads</code> sera initialisé au moment de la construction d'une
     * instance d'une classe héritant de <code>PatternSearch</code>.
     */
    protected final Map<String, String> reads;

    /**
     * <code>ArrayList</code> d'objets de type {@link SearchResults} contenant
     * les résultats de la recherche.
     * <p>
     * <code>results</code> sera rempli au fur et à mesure que la recherche
     * d'occurrences se poursuit, par la méthode <code>search</code>
     * implémentée par les classes héritant de <code>PatternSearch</code>.
     */
    protected final ArrayList<SearchResults> results;


    /**
     * Constructeur de la classe.
     * <p>
     * @param fastaFile chaîne de caractères spécifiant le chemin vers le
     * fichier multi-fasta contenant les génomes et leurs identifiants
     *
     * @param fastqFile chaîne de caractères spécifiant le chemin vers le
     * fichier FastQ contenant les <i>reads</i> et leurs identifiants
     *
     * @throws IOException si l'une des chaîne de caractères
     * <code>fastaFile</code> ou <code>fastqFile</code> vaut <code>null</code>
     * ou est vide, ou si le fichier indiqué par cette chaîne n'existe pas,
     * s'il n'est pas un fichier normal, s'il ne peut pas être lu ou s'il est
     * vide
     *
     * @throws IllegalArgumentException si l'un des deux <code>HashMap</code>
     * renvoyés par les méthodes <code>parse</code> des classes héritant de
     * {@link Parser} est vide ou s'il contient des clés ou valeurs illégales
     *
     * @throws NullPointerException si l'un des deux <code>HashMap</code>
     * renvoyés par les méthodes <code>parse</code> des classes héritant de
     * {@link Parser} vaut <code>null</code>
     */
    public PatternSearch(String fastaFile, String fastqFile)
            throws IOException {
        FastaParser fasta = new FastaParser(fastaFile);
        FastQParser fastq = new FastQParser(fastqFile);
        Map<String, String> genomes = fasta.parse();
        Map<String, String> reads   = fastq.parse();

        if (genomes == null)
            throw new NullPointerException(
                "PatternSearch.PatternSearch(): le HashMap 'genomes' " +
                        "ne peut pas être nul."
            );
        if (genomes.isEmpty())
            throw new IllegalArgumentException(
                "PatternSearch.PatternSearch(): le HashMap 'genomes' " +
                        "ne peut pas être vide."
            );
        if (genomes.containsKey(null)   || genomes.containsKey("") ||
            genomes.containsValue(null) || genomes.containsValue(""))
            throw new IllegalArgumentException(
                "PatternSearch.PatternSearch(): le HashMap 'genomes' " +
                        "ne peut pas contenir des clés ou valeurs " +
                        "nulles ou vides."
            );
        this.genomes = genomes;

        if (reads == null)
            throw new NullPointerException(
                "PatternSearch.PatternSearch(): le HashMap 'reads' " +
                        "ne peut pas être nul."
            );
        if (reads.isEmpty())
            throw new IllegalArgumentException(
                "PatternSearch.PatternSearch(): le HashMap 'reads' " +
                        "ne peut pas être vide."
            );
        if (reads.containsKey(null)   || reads.containsKey("") ||
            reads.containsValue(null) || reads.containsValue(""))
            throw new IllegalArgumentException(
                "PatternSearch.PatternSearch(): le HashMap 'reads' " +
                        "ne peut pas contenir des clés ou valeurs " +
                        "nulles ou vides."
            );
        this.reads = reads;

        results = new ArrayList<SearchResults>();
    }


    /**
     * Renvoie le réverse-complément d'une séquence nucléotidique.
     *
     * @param sequence chaîne de caractères représentant la séquence (le
     * <i>read</i>) dont on souhaite obtenir la forme inversée et complémentée
     *
     * @return la séquence inversée et complémentée
     *
     * @throws IllegalArgumentException si la séquence reçue en argument n'est
     * pas une séquence d'ADN (ne contient pas que des A, C, G et T)
     */
    public static String reverseComplement(String sequence) {
        if (!sequence.toUpperCase().matches("^[ACGT]+$"))
            throw new IllegalArgumentException(
                "PatternSearch.reverseComplement(): Séquence d'ADN invalide: " +
                "elle ne contient pas que des A, C, G, et T."
            );

        StringBuilder reverse =
            new StringBuilder(sequence.toUpperCase()).reverse();
        String alphabet = "ACGT";

        for (int i = 0; i < reverse.length(); i++) {
            char character = reverse.charAt(i);
            int j = alphabet.indexOf(character);
            reverse.setCharAt(i, alphabet.charAt(alphabet.length()-1-j));
        }

        return reverse.toString();
    }


    /**
     * L'implémentation de cette méthode doit faire en sorte de rechercher
     * toutes les occurrences de chaque <i>read</i> dans <code>reads</code>,
     * dans sa forme directe ainsi que dans sa forme inversée et complémentée,
     * parmi l'ensemble de génomes contenus dans <code>genomes</code>.
     *
     * @return <code>ArrayList</code> d'objets de type {@link SearchResults}
     * contenant les résultats de la recherche de chaque <i>read</i> dans
     * <code>reads</code>, dans sa forme directe ainsi que dans sa forme
     * inversée et complémentée, parmi l'ensemble de génomes dans
     * <code>genomes</code>
     */
    public abstract ArrayList<SearchResults> search();


    /**
     * Initialise un tableau d'objets de type {@link GenomeCoverage} à partir
     * des résultats de la recherche d'occurences.
     * <p>
     * La taille de ce tableau est donnée par le nombre de génomes trouvés dans
     * le fichier d'entrée multi-fasta. Chaque objet de type
     * {@code GenomeCoverage} aura pour identifiant l'identifiant du génome
     * auquel il correspond, et la taille de chaque génome sera aussi retenue.
     * Le tableau de couverture de chaque génome sera rempli dès que la
     * recherche d'occurrences a eu lieu, dans le cadre d'un objet de type
     * {@link GUIModel}.
     *
     * @return le tableau d'objets de type {@code GenomeCoverage} déterminé à
     * partir des génomes contenus dans le fichier d'entrée multi-fasta
     */
    public GenomeCoverage[] doGenomeCoverage() {
        int N = genomes.size();
        Object[] genomeSet = genomes.keySet().toArray();
        String[] genomeIds = new String[N];

        for (int i = 0; i < N; i++) genomeIds[i] = genomeSet[i].toString();
        Arrays.sort(genomeIds);

        GenomeCoverage[] gc = new GenomeCoverage[N];
        for (int i = 0; i < N; i++)
            gc[i] = new GenomeCoverage(
                    genomeIds[i],
                    genomes.get(genomeIds[i]).length()
            );

        return gc;
    }
}
