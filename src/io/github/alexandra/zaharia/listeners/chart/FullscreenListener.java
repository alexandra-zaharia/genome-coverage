package io.github.alexandra.zaharia.listeners.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import io.github.alexandra.zaharia.gui.GUI;
import io.github.alexandra.zaharia.gui.GUIModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;


/**
 * La classe {@code FullscreenListener} implémente l'interface
 * {@code ActionListener}, permettant d'associer l'action appropriée au bouton
 * dans l'interface responsable du basculement en mode plein écran.
 */
public class FullscreenListener implements ActionListener {
    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */    
    private GUI gui;
    
    /**
     * Largeur du bouton permettant d'enregistrer l'image.
     */
    private static final int BUTTON_WIDTH = 135;
    
    
    /**
     * Constructeur de la classe.
     * 
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */
    public FullscreenListener(GUI gui) {
        this.gui = gui;
    }
    

    /**
     * Implémente le comportement souhaité quand on clique sur le bouton
     * permettant de basculer en mode plein écran.
     * <p>
     * La taille de la nouvelle fenêtre est calculée. Ensuite un nouveau
     * {@code JFrame} est construit, affichant sur son {@code JPanel} le
     * graphique et un {@code JButton} permettant de l'enregistrer.
     * <p>
     * Compromis: le graphique est affiché rapidement, ce qui déforme ses
     * proportions; en revanche, l'enregisrement de l'image affichée en plein
     * écran crée le graphique à une bonne qualité et l'écrit rapidement dans le
     * fichier de sortie spécifié en cliquant sur le bouton "Enregistrer...".
     */
    public void actionPerformed(ActionEvent e) {        
        double ratio = (double) GUIModel.CHART_WIDTH / GUIModel.CHART_HEIGHT;
        int chartHeight = gui.getHeight() - 75;
        int chartWidth  = (int) (chartHeight * ratio - 100);
       
        JPanel pane = new JPanel();
        pane.setLayout(null);
        
        JButton bSave = new JButton("Enregistrer...");
        bSave.addActionListener(
                new SaveChartListener(gui, chartWidth, chartHeight));
        pane.add(bSave);
        bSave.setBounds((chartWidth - BUTTON_WIDTH) / 2, 10, BUTTON_WIDTH, 20);
        
        int selection[] =
                gui.getGuiModel().getGenomeList().getSelectedIndices();
        JFreeChart chart = selection.length > 1 ?
                gui.getGuiModel().getMostRecentCompositeChartPanel().getChart() :
                gui.getGuiModel().getMostRecentChartPanel().getChart();
                
        ChartPanel chartPanel = new ChartPanel(chart);
        pane.add(chartPanel); 
        chartPanel.setBounds(0, 40, chartWidth, chartHeight);
        
        JFrame fullscreen = new JFrame("Courbe de couverture");
        fullscreen.setContentPane(pane);
        fullscreen.setSize(chartWidth + 5, gui.getHeight() - 5);
        fullscreen.setResizable(false);
        fullscreen.setVisible(true);
    }
}
