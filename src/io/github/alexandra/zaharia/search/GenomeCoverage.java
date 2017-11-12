package io.github.alexandra.zaharia.search;

import io.github.alexandra.zaharia.gui.GUIModel;

/**
 * La classe {@code GenomeCoverage} sert à offrir la structure de données 
 * nécessaire à l'extraction des informations permettant de construire les 
 * courbes de couverture pour chaque génome à partir des résultats de la 
 * recherche d'occurrences.
 * <p>
 * Pour chaque génome, on objet de cette classe retiendra son identifiant, sa
 * longueur, ainsi qu'un tableau d'entiers de la même longueur que celle du
 * génome. Le tableau est initialement vide, et il sera rempli avec les valeurs
 * représentant le nombre de séquences qui ont une occurrence à la position
 * respective dans le cadre du génome en question. Cette opération sera 
 * effectuée en fin de recherche, par un objet de type {@link GUIModel}.
 */
public class GenomeCoverage {
    /**
     * L'identifiant du génome.
     */
    private final String genomeId;
    
    /**
     * La longueur du génome.
     */
    private final int N;
    
    /**
     * Tableau d'entiers de taille {@code N} donnant la couverture du génome
     * (en nombre de <i>reads</i> à chaque position).
     */
    private int[] coverage;
    
    
    /**
     * Constructeur de la classe.
     *
     * @param genomeId chaîne de caractères représentant l'identifiant du génome
     * 
     * @param N taille du génome
     */
    public GenomeCoverage(String genomeId, int N) {
        this.genomeId = genomeId;
        this.N = N;
        coverage = new int[N];
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
     * Renvoie la longueur du génome.
     * 
     * @return la longueur du génome
     */
    public int length() {
        return N;
    }
    
    /**
     * Renvoie le tableau d'entiers donnant la couverture du génome.
     * 
     * @return le tableau d'entiers donnant la couverture du génome
     */
    public int[] getCoverage() {
        return coverage;
    }
    
    /**
     * Initialise le tableau d'entiers donnant la couverture du génome au
     * tableau reçu en argument et calculé ailleurs (par un objet de type
     * {@link GUIModel}).
     * 
     * @param coverage tableau d'entiers donnant la couverture du génome
     */
    public void setCoverage(int[] coverage) {
        System.arraycopy(coverage, 0, this.coverage, 0, N);
    }
}
