package io.github.alexandra.zaharia.listeners.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;

import io.github.alexandra.zaharia.gui.ButtonManager;
import io.github.alexandra.zaharia.gui.GUIModel;
import org.jfree.chart.ChartPanel;


/**
 * La classe {@code FirstChartListener} implémente l'interface
 * {@code ActionListener}, permettant d'associer l'action appropriée au bouton
 * dans l'interface responsable du déplacement au graphique correspondant au
 * premier génome dans la liste de génomes.
 */
public class FirstChartListener implements ActionListener {
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
    public FirstChartListener(GUIModel guiModel) {
        this.guiModel = guiModel;
        bFirst = guiModel.getFirstChartButton();
        bPrev  = guiModel.getPrevChartButton();
        bNext  = guiModel.getNextChartButton();
        bLast  = guiModel.getLastChartButton();
    }
    
    
    /**
     * Implémente le comportement souhaité quand on clique sur le bouton
     * correspondant au premier génome dans la liste de génomes.
     * <p>
     * La sélection courante dans la liste de génomes se déplace sur le
     * premier élément et le graphique correspondant est affiché. Les 
     * boutons {@code bFirst} et {@code bPrev} sont désactivés. Les boutons 
     * {@code bNext} et {@code bLast} le sont aussi s'il n'y a qu'un seul 
     * génome dans la liste de génomes.
     */
    public void actionPerformed(ActionEvent e) {
        Map<Integer, ChartPanel> charts = guiModel.getCharts();
        
        charts.get(guiModel.getChartIndex()).setVisible(false);
        guiModel.setChartIndex(0);
        guiModel.getGenomeList().setSelectedIndex(0);

        ButtonManager.disable(bFirst, null);
        ButtonManager.disable(bPrev, null);
        ButtonManager.toggle(guiModel, bNext, bLast);

        guiModel.displayChart(0);
    }
}
