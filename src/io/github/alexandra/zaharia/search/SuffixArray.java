package io.github.alexandra.zaharia.search;

/**
 * La classe <code>SuffixArray</code> propose des méthodes permettant de
 * représenter tous les suffixes d'un texte triés dans l'ordre lexicographique
 * sous la forme d'un tableau d'entiers dont la <i>i</i>-ème valeur indique
 * l'indice du début du <i>i</i>-ème suffixe dans le texte d'origine (dans
 * l'ordre lexicographique des suffixes).
 * <p>
 * L'avantage de cette représentation est évident en termes de mémoire
 * utilisée. Stocker les suffixes d'un texte dans l'ordre lexicographique 
 * sous la forme de tableau d'entiers nécessite <i>n</i> entiers, alors que si 
 * on voulait les stocker sous la forme de tableaux de caractères on aurait 
 * besoin de 1+2+...+<i>n</i> caractères, soit de <i>n</i>*(<i>n</i>+1)/2 
 * caractères au total.
 * <p>
 * Le tableau d'entiers représentant les suffixes dans l'ordre lexicographique
 * est créé au moment de l'instanciation de la classe. Pour cela, le 
 * constructeur appelle la méthode de tri (une version de <i>quicksort</i> 
 * améliorée, qui fait appel au tri par insertion quand la méthode est appelée
 * sur un sous-tableau de faible dimension, car dans la pratique il s'avère 
 * qu'il est plus rapide de trier des petits sous-tableaux par tri insertion que
 * par l'algorithme <i>quicksort</i>).
 * <p>
 * Cette classe est une adaptation de l'implémentation de Robert Sedgewick et
 * Kevin Wayne, disponible
 * <a href="http://algs4.cs.princeton.edu/63suffix/SuffixArrayX.java.html">en 
 * ligne</a>.
 */
public class SuffixArray {
	/**
	 * Désigne le seuil (inclusif) en dessous duquel on utilise le tri par
	 * insertion pour trier un sous-tableau d'entiers, au lieu du tri rapide
	 * (<i>quicksort</i>). Ce choix s'explique par le fait que, dans la
	 * pratique, il est plus rapide de trier un sous-tableau de faible dimension
	 * par tri insertion que par tri rapide.
	 */
    private static final int CUTOFF = 10;

    /**
     * Représentation du texte dont on souhaite "trier les suffixes" sous la 
     * forme de tableau de caractères.
     */
    private final char[] text;
    
    /**
     * Tableau d'entiers donnant les indices du début de chaque suffixe du texte 
     * {@code text} dans l'ordre lexicographique.
     * <p>
     * Plus précisément, {@code index[i] = j} signifie que le suffixe commençant 
     * en position {@code j} dans le texte est le {@code i}-ème plus grand
     * suffixe de ce texte.
     */
    private final int[] index;
    
    /**
     * Taille du texte {@code text}.
     */
    private final int N;       

    
    /**
     * Constructeur de la classe.
     *
     * @param text chaîne de caractère représentant le texte dont on veut
     * créer le tableau de suffixes
     */
    public SuffixArray(String text) {
        N = text.length();
        text = text + '\0';
        this.text = text.toCharArray();
        this.index = new int[N];
        for (int i = 0; i < N; i++)
            index[i] = i;
        sort(0, N-1, 0);
    }

    
    /**
     * Trie les suffixes d'un texte dans l'ordre lexicographique, entre ses 
     * indices {@code lo} et {@code hi}, avec un décalage de {@code d}
     * positions par rapport à {@code lo}.
     * <p>     
     * Par défaut, la méthode de tri utilisée est le tri rapide
     * (<i>quicksort</i>). Si le nombre de caractères compris entre {@code lo}
     * et {@code hi} est inférieur au <code>CUTOFF</code> fixé, le tri utilisé
     * est le tri insertion.
     * 
     * @param lo le premier indice dans le tableau {@code text} pris en compte
     * par le tri; c'est un entier compris entre 0 et N-1 (inclus) 
     * 
     * @param hi le dernier indice dans le tableau {@code text} pris en compte
     * par le tri; c'est un entier compris entre 0 et N-1 (inclus) et supérieur 
     * ou égal à {@code lo} 
     * 
     * @param d décalage dans le tableau {@code text} par rapport à {@code lo}, 
     * à partir duquel le tri sera effetué; c'est un entier compris entre
     * 0 et N-1 (inclus) 
     */
    private void sort(int lo, int hi, int d) { 
        if (hi < lo + CUTOFF) { // tri insertion si peu d'éléments
            insertion(lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        char v = text[index[lo] + d];
        int i = lo + 1;
        while (i <= gt) {
            char t = text[index[i] + d];
            if      (t < v) exch(lt++, i++);
            else if (t > v) exch(i, gt--);
            else            i++;
        }

        sort(lo, lt-1, d);
        if (v > 0) sort(lt, gt, d+1);
        sort(gt+1, hi, d);
    }

    
    /**
     * Réalise le tri par insertion du sous-tableau de {@code text} compris
     * entre ses indices {@code lo} et {@code hi}, avec un décalage de {@code d}
     * positions par rapport à {@code lo}.
     * 
     * @param lo le premier indice dans le tableau {@code text} pris en compte
     * par le tri; c'est un entier compris entre 0 et N-1 (inclus) 
     * 
     * @param hi le dernier indice dans le tableau {@code text} pris en compte
     * par le tri; c'est un entier compris entre 0 et N-1 (inclus) et supérieur 
     * ou égal à {@code lo} 
     * 
     * @param d décalage dans le tableau {@code text} par rapport à {@code lo}, 
     * à partir duquel le tri sera effetué; c'est un entier compris entre
     * 0 et N-1 (inclus) 
     */
    private void insertion(int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(index[j], index[j-1], d); j--)
                exch(j, j-1);
    }

    
    /**
     * Détermine si le suffixe du texte commençant à la position {@code i+d} 
     * est inférieur au suffixe commençant à la position {@code j+d}.
     * 
     * @param i entier compris entre 0 et N-1
     * 
     * @param j entier compris entre 0 et N-1
     * 
     * @param d décalage
     * 
     * @return true si le suffixe commençant à la position {@code i+d}
     * est inférieur lexicographiquement au suffixe commençant à la position
     * {@code j+d}, ou false sinon
     */
    private boolean less(int i, int j, int d) {
        if (i == j) return false;
        i = i + d;
        j = j + d;
        while (i < N && j < N) {
            if (text[i] < text[j]) return true;
            if (text[i] > text[j]) return false;
            i++;
            j++;
        }
        return i > j;
    }

    
    /**
     * Échange les deux éléments aux positions {@code i} et {@code j}
     * du tableau d'entiers {@code index}.
     *  
     * @param i entier compris entre 0 et N-1 ayant pour valeur la position du 
     * {@code i}-ème suffixe dans le tableau de suffixes
     * 
     * @param j entier compris entre 0 et N-1 ayant pour valeur la position du 
     * {@code j}-ème suffixe dans le tableau de suffixes
     */
    private void exch(int i, int j) {
        int swap = index[i];
        index[i] = index[j];
        index[j] = swap;
    }

    
    /**
     * Renvoie la longueur du tableau de caractères {@code text}.
     * 
     * @return la longueur du tableau de caractères {@code text}
     */
    public int length() {
        return N;
    }


    /**
     * Renvoie l'indice du début du {@code i}-ème suffixe du texte dans l'ordre
     * lexicographique.
     * 
     * @param i entier compris entre 0 et N-1 représentant la position dans le
     * tableau de suffixes du suffixe i
     * 
     * @throws IndexOutOfBoundsException si l'entier passé en argument n'est 
     * pas compris entre 0 inclus et N exclus
     * 
     * @return position du {@code i}-ème suffixe dans le texte
     */
    public int index(int i) {
        if (i < 0 || i >= N) throw new IndexOutOfBoundsException();
        return index[i];
    }
    

    /**
     * Calcule et renvoie la longueur du plus long préfixe propre (LCP, 
     * <i>Longest Common Prefix</i>) entre deux suffixes du texte, l'un 
     * commençant à la position {@code i}, l'autre à la position {@code i-1}.
     * 
     * @param i entier compris entre 1 et N-1 représentant l'indice à partir
     * duquel le premier suffixe du texte commence dans ce dernier
     * 
     * @throws IndexOutOfBoundsException si {@code i} n'est pas compris entre 1
     * et N-1
     * 
     * @return entier valant le LCP entre le suffixe commençant à la position
     * {@code i} et le suffixe commençant à la position {@code i-1} dans le
     * texte
     */
    public int lcp(int i) {
        if (i < 1 || i >= N) throw new IndexOutOfBoundsException();
        return lcp(index[i], index[i-1]);
    }

    
    /**
     * Calcule et renvoie la longueur du plus long préfixe propre (LCP, 
     * <i>Longest Common Prefix</i>) entre deux suffixes du texte, l'un 
     * commençant à la position {@code i}, l'autre à la position {@code j}.
     *      
     * @param i entier compris entre 1 et N-1 représentant l'indice à partir
     * duquel le premier suffixe du texte commence dans ce dernier
     * 
     * @param j entier compris entre 1 et N-1 représentant l'indice à partir
     * duquel le deuxième suffixe du texte commence dans ce dernier
     * 
     * @return entier valant le LCP entre le suffixe commençant à la position
     * {@code i} et le suffixe commençant à la position {@code j} dans le texte
     */
    public int lcp(int i, int j) {
        int length = 0;
        while (i < N && j < N) {
            if (text[i] != text[j]) return length;
            i++;
            j++;
            length++;
        }
        return length;
    }
    
    
    /**
     * Calcule et renvoie la longueur du plus long préfixe propre (LCP, 
     * <i>Longest Common Prefix</i>) entre la chaîne de caractères {@code query}
     * et le suffixe du texte commençant à la position {@code i}.
     * 
     * @param query chaîne de caractère représentant le <i>read</i>
     * 
     * @param i entier compris entre 0 et N-1 représentant l'indice (dans le 
     * cadre du texte) à partir duquel la comparaison entre le texte et la
     * chaîne requête va commencer
     * 
     * @return entier valant le LCP entre la chaîne {@code query} et le 
     * suffixe commençant à la position {@code i} dans le texte
     */
    public int lcp(String query, int i) {
        int length = 0;
        int M = query.length();
        int j = 0; 
        while (i < N && j < M) {
            if (text[i] != query.charAt(j)) return length;
            i++;
            j++;
            length++;
        }
        return length;
    }

    
    /**
     * Renvoie le {@code i}-ème plus petit suffixe dans l'ordre lexicographique,
     * sous la forme d'une chaîne de caractères.
     * 
     * @param i l'indice du {@code i}-ème plus petit suffixe
     * 
     * @return le {@code i}-ème plus petit suffixe dans l'ordre lexicographique,
     * sous la forme d'une chaîne de caractères
     * 
     * @throws IndexOutOfBoundsException si {@code i} est inférieur à 0 ou plus
     * grand que N-1.
     */
    public String select(int i) {
        if (i < 0 || i >= N) throw new IndexOutOfBoundsException();
        return new String(text, index[i], N - index[i]);
    }

    
    /**
     * Compare un à un les caractères de la chaîne de caractères {@code query} 
     * passée en argument avec le suffixe du texte commençant à la position
     * {@code i}.
     *
     * @param query chaîne de caractères représentant le <i>read</i>
     * 
     * @param i entier représentant l'indice du début du suffixe dans le texte
     * 
     * @return Entier
     * <ul>
     * <li>
     * &lt; 0 si le <i>read</i> est plus petit de point de vue lexicographique
     * que le suffixe;
     * </li>
     * <li>
     * &gt; 0 si le <i>read</i> est plus grand de point de vue lexicographique
     * que le suffixe;
     * </li>
     * <li>0 si le <i>read</i> est équivalent de point de vue lexicographique
     * au suffixe.
     * </li>
     * </ul>
     */
    public int compare(String query, int i) {
        int M = query.length();
        int j = 0;
        while (i < N && j < M) {
            if (query.charAt(j) != text[i]) return query.charAt(j) - text[i];
            i++;
            j++;

        }
        if (i < N) return -1;
        if (j < M) return +1;
        return 0;
    }    
}