package io.github.alexandra.zaharia.listeners.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;

import io.github.alexandra.zaharia.gui.ButtonManager;
import io.github.alexandra.zaharia.gui.GUIModel;
import org.jfree.chart.ChartPanel;


/**
 * La classe {@code NextChartListener} implémente l'interface
 * {@code ActionListener}, permettant d'associer l'action appropriée au bouton
 * dans l'interface responsable du déplacement au graphique correspondant au
 * génome suivant dans la liste de génomes.
 */
public class NextChartListener implements ActionListener {
    /**
     * Le {@code GUIModel} à utiliser.
     */    
    private GUIModel guiModel;
    
    /**
     * Le bouton responsable du déplacement au graphique correspondant
     * au premier génome dans la liste de génomes.
     */
    private JButton bFirst;
    
    /**
     * Le bouton responsable du déplacement au graphique correspondant
     * au génome précédent dans la liste de génomes.
     */
    private JButton bPrev;

    /**
     * Le bouton responsable du déplacement au graphique correspondant
     * au génome suivant dans la liste de génomes.
     */    
    private JButton bNext;

    /**
     * Le bouton responsable du déplacement au graphique correspondant
     * au dernier génome dans la liste de génomes.
     */
    private JButton bLast;

    
    /**
     * Constructeur de la classe.
     * 
     * @param guiModel le {@code GUIModel} à utiliser
     */    
    public NextChartListener(GUIModel guiModel) {
        this.guiModel = guiModel;
        bFirst = guiModel.getFirstChartButton();
        bPrev  = guiModel.getPrevChartButton();
        bNext  = guiModel.getNextChartButton();
        bLast  = guiModel.getLastChartButton();
    }
    
    
    /**
     * Implémente le comportement souhaité quand on clique sur le bouton
     * correspondant au génome suivant dans la liste de génomes.
     * <p>
     * La sélection courante dans la liste de génomes se déplace sur 
     * l'élément suivant et le graphique correspondant est affiché. Les 
     * boutons {@code bNext} et {@code bLast} sont désactivés si on arrive 
     * ainsi sur le dernier élément dans la liste. Les boutons {@code bFirst}
     * et {@code bPrev} sont activés s'il y a plus qu'un génome dans la liste
     * de génomes.
     */    
    public void actionPerformed(ActionEvent e) {
        Map<Integer, ChartPanel> charts = guiModel.getCharts();
        
        int currentChart = guiModel.getChartIndex();
        charts.get(currentChart).setVisible(false);
        guiModel.setChartIndex(++currentChart);
        guiModel.getGenomeList().setSelectedIndex(currentChart);
        
        if (currentChart == guiModel.getGenomeCoverage().length - 1) {
            ButtonManager.disable(bNext, null);
            ButtonManager.disable(bLast, null);
        }

        ButtonManager.enable(bFirst, GUIModel.ORANGE);
        ButtonManager.enable(bPrev, GUIModel.ORANGE);
        guiModel.displayChart(currentChart);
    }
}
