package io.github.alexandra.zaharia.listeners;

import io.github.alexandra.zaharia.gui.ButtonManager;
import io.github.alexandra.zaharia.gui.GUI;
import io.github.alexandra.zaharia.gui.SearchWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingWorker;


/**
 * La classe {@code SearchListener} implémente l'interface
 * {@code ActionListener}, en associant l'action appropriée au bouton permettant
 * de lancer la recherche d'occurrences des <i>reads</i> du fichier d'entrée
 * FastQ dans les génomes dans le fichier d'entrée multi-fasta.
 */
public class SearchListener implements ActionListener {
    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */                
    private GUI gui;
    
    /**
     * Le bouton dans l'interface permettant de lancer la recherche
     * d'occurrences.
     */
    private JButton bSearch;
    
    /**
     * La barre de statut de l'interface.
     */    
    private JLabel statusBar;
    
    
    /**
     * Constructeur de la classe.
     * 
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */
    public SearchListener (GUI gui) {
        this.gui  = gui;
        bSearch   = gui.getSearchButton();
        statusBar = gui.getStatusBar();
    }

    
    /**
     * Implémente le comportement souhaité quand on clique sur le bouton
     * permettant de lancer la recherche d'occurrences.
     * <p>
     * Le bouton permettant de lancer la recherche est désactivé et la liste de
     * génomes est vidée. Un fil d'exécution de type {@code SwingWorker} est
     * créé pour effectuer la recherche d'occurrences en <i>background</i>, ce
     * qui permet de laisser l'interface graphique utilisable pendant ce
     * temps-là.
     * <p>
     * Si le {@code SwingWorker} finit sans erreurs, la liste de génomes est
     * initialisée aux identifiants des génomes trouvés dans le fichier
     * multi-fasta, le calcul de la couverture pour chaque génome est effectué
     * et la courbe de couverture pour le premier génome est affichée.
     */
    public void actionPerformed(ActionEvent e) {
        ButtonManager.disable(bSearch, null);
        gui.getGuiModel().getListModel().clear();
        gui.getGuiModel().stopMouseListener();

        final SearchWorker worker = new SearchWorker(gui);
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if ("state".equals(event.getPropertyName()) &&
                        SwingWorker.StateValue.DONE == event.getNewValue()) {
                    if (!worker.threwException()) {
                        statusBar.setText("Recherche terminée");
                        gui.getGuiModel().initializeGenomeList();
                        gui.getGuiModel().computeCoverage();
                        gui.getGuiModel().initializeCoverageChart();
                    }
                }
            }               
        };
        worker.addPropertyChangeListener(listener);
        worker.execute();
    }    
}
