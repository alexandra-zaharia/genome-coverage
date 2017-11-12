package io.github.alexandra.zaharia.listeners.path;

import io.github.alexandra.zaharia.gui.ButtonManager;
import io.github.alexandra.zaharia.gui.GUI;
import io.github.alexandra.zaharia.gui.GUIModel;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * La classe {@code GenomesPathListener} implémente l'interface
 * {@code DocumentListener}, en associant l'action appropriée aux événements
 * d'insertion et de suppression de texte dans le champs de texte déstiné à
 * afficher le chemin vers le fichier d'entrée de type multi-fasta contenant les
 * génomes.
 */
public class GenomesPathListener implements DocumentListener {
    /**
     * Le champ de texte affichant le chemin vers le fichier multi-fasta.
     */
    private JTextField pathGenomes;
    
    /**
     * Le champ de texte affichant le chemin vers le fichier FastQ.
     */
    private JTextField pathReads;
    
    /**
     * Le bouton dans l'interface permettant de lancer la recherche
     * d'occurrences.
     */
    private JButton bSearch;
    
    
    /**
     * Constructeur de la classe.
     * 
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */
    public GenomesPathListener(GUI gui) {
        pathGenomes = gui.getGenomesTextField();
        pathReads   = gui.getReadsTextField();
        bSearch     = gui.getSearchButton();
    }

    
    /**
     * Implémente le comportement souhaité quand on insère des caractères dans
     * le champ de texte correspondant au chemin vers le fichier multi-fasta.
     * <p>
     * Si la longueur du chemin spécifié pour le fichier FastQ n'est pas nulle,
     * le bouton permettant de lancer la recherche sera activé.
     */
    public void insertUpdate(DocumentEvent e) {
        if (pathReads.getText().trim().length() != 0)
            ButtonManager.enable(bSearch, GUIModel.ORANGE);
    }

    
    /**
     * Implémente le comportement souhaité quand on enlève des caractères du
     * champ de texte correspondant au chemin vers le fichier multi-fasta.
     * <p>
     * Si la longueur du chemin spécifié pour ce fichier est nulle,
     * le bouton permettant de lancer la recherche sera désactivé.
     */
    public void removeUpdate(DocumentEvent e) {
        if (pathGenomes.getText().trim().length() == 0)
            ButtonManager.disable(bSearch, null);
    }

    
    /**
     * Non implémenté.
     */
    public void changedUpdate(DocumentEvent e) {}
}
