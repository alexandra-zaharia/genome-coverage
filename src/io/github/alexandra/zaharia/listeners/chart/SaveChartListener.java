package io.github.alexandra.zaharia.listeners.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import io.github.alexandra.zaharia.gui.ExceptionHandlingGUI;
import io.github.alexandra.zaharia.gui.GUI;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;


/**
 * La classe {@code SaveChartListener} implémente l'interface
 * {@code ActionListener}, en associant l'action appropriée au bouton dans
 * l'interface permettant d'enregistrer le graphique affiché.
 */
public class SaveChartListener implements ActionListener {
    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */            
    private GUI gui;
    
    /**
     * Largeur du graphique à enregistrer. 
     */
    private int width;
    
    /**
     * Hauteur du graphique à enregistrer.
     */
    private int height;
    
    
    /**
     * Constructeur de la classe.
     *
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     * @param width largeur du graphique à enregistrer
     * @param height hauteur du graphique à enregistrer
     */
    public SaveChartListener(GUI gui, int width, int height) {
        this.gui    = gui;
        this.width  = width;
        this.height = height;
    }

    
    /**
     * Implémente le comportement souhaité quand on clique sur le bouton
     * permettant d'enregistrer le graphique affiché.
     * <p>
     * Une boîte de dialogue est affichée, permettant à l'utilisateur de choisir
     * le fichier de sortie pour le graphique affiché (en format PNG). Si le
     * fichier spécifié n'a pas l'extension ".png", celle-là est automatiquement
     * ajoutée.
     */
    public void actionPerformed(ActionEvent e) {
        int selected[] = gui.getGuiModel().getGenomeList().getSelectedIndices();
        JFreeChart chart = selected.length > 1 ?
            gui.getGuiModel().getMostRecentCompositeChartPanel().getChart() :
            gui.getGuiModel().getMostRecentChartPanel().getChart();
            
        JFileChooser fc =
                new JFileChooser(gui.getFileChooser().getCurrentDirectory());
        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("PNG file", "png");
        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);
        
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gui);
        int option = fc.showSaveDialog(frame);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fc.getSelectedFile().getPath();
            if (!filename.toLowerCase().endsWith(".png"))
                filename = filename + ".png";
            try {
                ChartUtilities.saveChartAsPNG(
                        new File(filename), chart, width, height);
            } catch (IOException exception) {
                gui.getStatusBar().setText("Erreur");
                ExceptionHandlingGUI.showExceptionPanel(exception);
            }
        }
    }
}
