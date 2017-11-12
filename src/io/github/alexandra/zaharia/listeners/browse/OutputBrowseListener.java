package io.github.alexandra.zaharia.listeners.browse;

import io.github.alexandra.zaharia.gui.GUI;
import io.github.alexandra.zaharia.gui.OpenFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * La classe {@code OutputBrowseListener} implémente l'interface
 * {@code ActionListener}, en associant l'action appropriée au bouton dans
 * l'interface responsable de l'affichage du dialogue permettant de choisir le
 * fichier de sortie pour récupérer les résultats de la recherche d'occurrences.
 */
public class OutputBrowseListener implements ActionListener {
    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */   
    private GUI gui;
    
    
    /**
     * Constructeur de la classe.
     * 
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */        
    public OutputBrowseListener(GUI gui) {
        this.gui = gui;
    }

    
    /**
     * Implémente le comportement souhaité quand on clique sur le bouton
     * permettant de choisir le fichier de sortie pour récupérer les résultats
     * de la recherche d'occurrences.
     * <p>
     * Si le bouton "Enregistrer" a été cliqué dans le cadre du
     * {@code JFileChooser}, le chemin absolu vers ce fichier est récupéré et
     * affiché dans le champ de texte en dessous du bouton dans l'interface
     * ayant déclenché cette action.
     * <p>
     * Les résultats de la recherche d'occurrences seront enregistrés dans ce
     * fichier (si c'est un fichier normal et si les permissions de
     * l'utilisateur pour ce chemin le permettent).
     */        
    public void actionPerformed(ActionEvent e) {
        OpenFile.choose(gui, gui.getOutputTextField(), null);
    }
}