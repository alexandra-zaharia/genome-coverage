package io.github.alexandra.zaharia.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JList;

import io.github.alexandra.zaharia.gui.ButtonManager;
import io.github.alexandra.zaharia.gui.GUI;
import io.github.alexandra.zaharia.gui.GUIModel;
import org.jfree.chart.ChartPanel;



/**
 * La classe {@code GenomeListMouseListener} implémente l'interface
 * {@code MouseListener}, permettant d'associer l'action appropriée 
 * à l'événement de clic dans la liste de génomes, ainsi qu'aux
 * événements {@code mouseEntered} et {@code mouseExited} dans la région
 * de l'interface occupée par la liste de génomes.
 */
public class GenomeListMouseListener implements MouseListener {
    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */        
    private GUI gui;
    
    /**
     * Booléean responsable du contrôle de cet objet. Le listener est actif s'il
     * vaut true, et inactif sinon.
     */
    private boolean enabled = true;
    
    /**
     * L'ancien texte affiché par le {@code JLabel statusBar} de l'objet 
     * {@code gui}.
     */
    private String oldText;
    
    /**
     * Le nouveau texte à afficher dans le {@code JLabel statusBar} de l'objet
     * {@code gui} quand la souris entre dans la région occupée par la liste de
     * génomes.
     */
    private String newText = 
        "Pour sélectionner plusieurs génomes, maintenez la touche CTRL enfoncée.";

    
    /**
     * Constructeur de la classe.
     * 
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */
    public GenomeListMouseListener(GUI gui) {
        this.gui = gui; 
        oldText = this.gui.getStatusBar().getText();
    }
    
    
    /**
     * Désactive ce listener.
     */
    public void disable() { enabled = false; }
    
    
    /**
     * Active ce listener. 
     */
    public void enable()  { enabled = true; }
    
    
    /**
     * Affiche la courbe de couverture correspondant au(x) génome(s)
     * sélectionné(s) quand on clique dans la liste de génomes.
     * <p>
     * S'il s'agit d'une sélection simple (un seul génome sélectionné), on
     * essaie d'abord de récupérer la courbe de couverture correspondante à
     * partir du {@code HashMap charts} de l'objet de type {@code GUIModel}
     * attaché à {@code gui}. Si ce graphique n'est pas présent dans le
     * {@code HashMap}, il est généré, affiché et stocké dans le
     * {@code HashMap}. C'est une approche de type <i>lazy initialization</i>
     * permettant de gagner en performance lorsque l'utilisateur redemande
     * l'affichage du graphique, ce qui est vraisemblable dans le cas des
     * courbes de couverture pour un seul génome. L'état des boutons de
     * navigation en dessous de la courbe de couverture est mis à jour.
     * <p>
     * S'il s'agit d'une sélection multiple (plusieurs génomes sélectionnés), le
     * graphique correspondant qui superpose leurs courbes de couverture est
     * généré et affiché (mais il n'est pas enregistré).
     */
    public void mouseClicked(MouseEvent e)  {
        JList<String> list = gui.getGuiModel().getGenomeList();
        if (list.getSelectedIndex() != -1) {
            if (list.getSelectedIndices().length == 1) {
                int index = list.getSelectedIndex();
                Map<Integer, ChartPanel> charts = gui.getGuiModel().getCharts();
                
                charts.get(gui.getGuiModel().getChartIndex()).setVisible(false);
                gui.getGuiModel().setChartIndex(index);
                
                updateButtons(index);
                
                gui.getGuiModel().displayChart(index);
            }
            else { // Plus qu'un item sélectionné : superposition de courbes
                int[] selection = list.getSelectedIndices();
                gui.getGuiModel().displayChart(selection);
            }
        }
    }
    
    
    /**
     * Non implémenté.
     */
    public void mousePressed(MouseEvent e)  {}
    
    
    /**
     * Non implémenté.
     */
    public void mouseReleased(MouseEvent e) {}

    
    /**
     * Implémente le comportement souhaité quand la souris entre dans la région
     * occupée par la liste de génomes.
     * <p>
     * Le texte affiché par le {@code JLabel statusBar} de l'objet {@code GUI}
     * est la chaîne de caractères {@code newText}.
     */
    public void mouseEntered(MouseEvent e) {  
        if (enabled) gui.getStatusBar().setText(newText);
    }


    /**
     * Implémente le comportement souhaité quand la souris quitte la région
     * occupée par la liste de génomes.
     * <p>
     * Le texte affiché par le {@code JLabel statusBar} de l'objet {@code GUI}
     * revient à la chaîne de caractères {@code oldText}.
     */
    public void mouseExited(MouseEvent e) {
        if (enabled) gui.getStatusBar().setText(oldText);
    }
    
    
    /**
     * Met à jour l'état des quatre boutons de navigation en dessous du 
     * graphique, selon l'indice du génome sélectionné dans la liste de
     * génomes.
     *  
     * @param index la valeur de la sélection simple dans la liste de génomes
     */
    private void updateButtons(int index) {
        JButton bFirst = gui.getGuiModel().getFirstChartButton();
        JButton bPrev  = gui.getGuiModel().getPrevChartButton();
        JButton bNext  = gui.getGuiModel().getNextChartButton();
        JButton bLast  = gui.getGuiModel().getLastChartButton();

        int gcLength = gui.getGuiModel().getGenomeCoverage().length;
        
        if (index == 0) {
            ButtonManager.disable(bFirst, null);
            ButtonManager.disable(bPrev, null);
            if (gcLength > 1) {
                ButtonManager.enable(bNext, GUIModel.ORANGE);
                ButtonManager.enable(bLast, GUIModel.ORANGE);
            }
        } else if (0 < index && index < gcLength - 1) {
            if (gcLength > 1) {
                ButtonManager.enable(bFirst, GUIModel.ORANGE);
                ButtonManager.enable(bPrev, GUIModel.ORANGE);
                ButtonManager.enable(bNext, GUIModel.ORANGE);
                ButtonManager.enable(bLast, GUIModel.ORANGE);
            }
        } else {
            ButtonManager.disable(bNext, null);
            ButtonManager.disable(bLast, null);
            if (gui.getGuiModel().getGenomeCoverage().length > 1) {
                ButtonManager.enable(bFirst, GUIModel.ORANGE);
                ButtonManager.enable(bPrev, GUIModel.ORANGE);
            }
        }
    }
}
