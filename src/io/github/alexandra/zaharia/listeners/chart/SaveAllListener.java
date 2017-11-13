package io.github.alexandra.zaharia.listeners.chart;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import io.github.alexandra.zaharia.gui.ExceptionHandlingGUI;
import io.github.alexandra.zaharia.gui.GUI;
import io.github.alexandra.zaharia.gui.GUIModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import io.github.alexandra.zaharia.search.GenomeCoverage;


/**
 * La classe {@code SaveAllListener} implémente l'interface
 * {@code ActionListener}, en associant l'action appropriée à l'item
 * "Enregistrer tous les graphiques..." du menu "Fichier" de l'interface.
 * <p>
 * Ceci a pour conséquence d'enregistrer les graphiques contenant 
 * les courbes de couverture pour chaque génome (un graphique par génome) en
 * format PNG, dans le répertoire choisi par l'utilisateur. Les noms des
 * fichiers sont déterminés automatiquement, en remplaçant les caractères
 * spéciaux présents dans les identifiants des génomes par des
 * <i>underscores</i>.
 */
public class SaveAllListener implements ActionListener {
    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */                
    private GUI gui;
    
    /**
     * Le répertoire choisi pour enregistrer les courbes de couverture.
     */
    private File dir;

    
    /**
     * Constructeur de la classe.
     * 
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */
    public SaveAllListener(GUI gui) {
        this.gui = gui;
    }
    
    
    /**
    * Implémente le comportement souhaité quand on clique sur l'item 
    * "Enregistrer tous les graphiques..." du menu "Fichier" de l'interface.
    * <p>
    * Une boîte de dialogue est affichée, permettant à l'utilisateur de
    * choisir le répertoire où les courbes de couverture seront enregistrées.
    * S'il a droit d'accès à ce répertoire en mode lecture et écriture,
    * l'enregistrement des graphique est lancé sur un nouveau fil d'exécution.
    * Le {@code JLabel statusBar} de {@code gui} affichera le texte
    * "Enregistrement en cours..." pendant l'enregistrement, puis 
    * "Enregistrement terminé".
    */
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = gui.getFileChooser();        
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gui);
        int returnVal = fc.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {            
            try {
                dir = fc.getSelectedFile();
                if (!dir.canRead() || !dir.canWrite())
                    throw new IOException(
                            "Permissions insuffisantes sur " + dir);
                
                gui.getStatusBar().setText("Enregistrement en cours...");
                String timeStamp = GUIModel.getTimeStamp();
                System.out.println(timeStamp +
                        " - Enregistrement des graphiques commencé");
                
                Thread t = new Thread() {
                    public void run() {
                        saveAllToDirectory(dir);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                gui.getStatusBar().setText(
                                        "Enregistrement terminé");
                                String timeStamp = GUIModel.getTimeStamp();
                                System.out.println(timeStamp +
                                        " - Enregistrement des graphiques " +
                                        "terminé");
                            }
                        });
                    }
                };
                t.start();                
            } catch (IOException exception) {
                gui.getStatusBar().setText("Erreur");
                ExceptionHandlingGUI.showExceptionPanel(exception);
            }           
        }
    }
    
    
    /**
     * Enregistre toutes les courbes de couverture dans le répertoire
     * {@code dir}. Les fichiers seront enregistrés en format PNG. Le
     * nom de chaque fichier est obtenu en remplaçant les caractères
     * spéciaux dans l'identifiant du génome par un <i>underscore</i>.
     *
     * @param dir répertoire choisi pour enregistrer les courbes de couverture
     */
    private void saveAllToDirectory(File dir) {
        GenomeCoverage[] gc = gui.getGuiModel().getGenomeCoverage();
        
        for (int i = 0; i < gc.length; i++) {
            String id = gc[i].getGenomeId();
            id = id.replaceAll("\\|", "_");
            String output = dir + "/" + id + ".png";
            File png = new File(output);
            
            gui.getGuiModel().displayChart(i);
            ChartPanel chartPanel = gui.getGuiModel().getMostRecentChartPanel();            
            JFreeChart chart = chartPanel.getChart();
            int currentChart = gui.getGuiModel().getChartIndex();
            if (i != currentChart) chartPanel.setVisible(false);
            
            Container pane = gui.getContainerPane();
            pane.add(chartPanel);
            chartPanel.setBounds(
                    GUIModel.CHART_X,
                    GUIModel.CHART_Y,
                    GUIModel.CHART_WIDTH,
                    GUIModel.CHART_HEIGHT
            );
            int width  = chartPanel.getWidth();
            int height = chartPanel.getHeight();
            
            try {
                ChartUtilities.saveChartAsPNG(png, chart, width, height);
            } catch (IOException exception) {
                gui.getStatusBar().setText("Erreur");
                ExceptionHandlingGUI.showExceptionPanel(exception);
            }
        }        
    }
}
