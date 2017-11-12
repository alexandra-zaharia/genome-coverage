package io.github.alexandra.zaharia.listeners.browse;

import io.github.alexandra.zaharia.gui.GUI;
import io.github.alexandra.zaharia.gui.OpenFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * La classe {@code ReadsBrowseListener} implémente l'interface
 * {@code ActionListener}, en associant l'action appropriée au bouton dans
 * l'interface responsable de l'affichage du dialogue permettant de choisir le
 * fichier d'entrée FastQ contenat les <i>reads</i>.
 */
public class ReadsBrowseListener implements ActionListener {
    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */            
    private GUI gui;

    
    /**
     * Constructeur de la classe.
     * 
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */    
    public ReadsBrowseListener(GUI gui) {        
        this.gui = gui;
    }

    
    /**
     * Implémente le comportement souhaité quand on clique sur le bouton
     * permettant de choisir le fichier d'entrée FastQ contenat les
     * <i>reads</i>.
     * <p>
     * Si le bouton "Ouvrir" a été cliqué dans le cadre du {@code JFileChooser},
     * le chemin absolu vers ce fichier est récupéré et affiché dans le champ de
     * texte en dessous du bouton dans l'interface ayant déclenché cette action.
     * <p>
     * Si le fichier contenant les génomes a également été spécifé, le 
     * bouton permettant de lancer la recherche d'occurrences est activé.
     */    
    public void actionPerformed(ActionEvent e) {
        OpenFile.choose(
                gui,
                gui.getReadsTextField(),
                gui.getGenomesTextField()
        );
    }
}
