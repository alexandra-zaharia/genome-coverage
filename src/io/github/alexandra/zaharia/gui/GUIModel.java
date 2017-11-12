package io.github.alexandra.zaharia.gui;

import java.awt.Color;
import java.awt.Container;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import io.github.alexandra.zaharia.listeners.GenomeListMouseListener;
import io.github.alexandra.zaharia.listeners.chart.*;
import org.jfree.chart.ChartPanel;
import io.github.alexandra.zaharia.search.GenomeCoverage;
import io.github.alexandra.zaharia.search.SearchResults;


/**
 * La classe {@code GUIModel} est responsable de la création du modèle
 * utilisé par l'interface; il s'agit du modèle des données utilisées, et 
 * il comprend toute la logique interne de l'interface.
 */
public class GUIModel {
    /**
     * Couleur orange utilisée pour certains composants de l'interface.
     */
    public static final Color ORANGE = new Color(250, 171, 92);
    
    /**
     * Coordonnée x du graphique des courbes de couverture.
     */
    public static final int CHART_X = 500;
    
    /**
     * Coordonnée y du graphique des courbes de couverture.
     */
    public static final int CHART_Y =  20;
    
    /**
     * Largeur du graphique des courbes de couverture (en pixels).
     */    
    public static final int CHART_WIDTH = 750;

    /**
     * Hauteur du graphique des courbes de couverture (en pixels).
     */
    public static final int CHART_HEIGHT = 480;

    /**
     * Référence de l'objet de type {@code GUI} affichant l'interface.
     */
    private GUI gui;
    
    /**
     * {@code ArrayList} d'objets de type {@link SearchResults}, retenant les 
     * résultats de la recherche d'occurrences.
     */
    private ArrayList<SearchResults> results;
    
    /**
     * Tableau d'objets de type {@link GenomeCoverage} contenant les
     * informations nécessaires à la réalisation des courbes de couverture pour
     * chaque génome.
     */
    private GenomeCoverage[] gc;
    
    /**
     * Modèle pour la liste de génomes.
     */
    private DefaultListModel<String> listModel;
    
    /**
     * La liste de génomes.
     */
    private JList<String> genomeList;
    
    /**
     * {@code JLabel} contenant le texte affiché à côté de la liste de génomes.
     */
    private JLabel labelGenomeList;
    
    /**
     * {@code JScrollPane} attaché à la liste de génomes pour permettre son
     * défilement.
     */
    private JScrollPane genomePane = null;
    
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
     * Le bouton responsable du basculement en mode plein écran.
     */
    private JButton bFull;
    
    /**
     * Le bouton responsable de l'enregistrement du graphique courant. 
     */
    private JButton bSave;
    
    /**
     * Indice du graphique courant, correspondant à l'indice du génome dont la
     * courbe de couverture est affichée dans le cadre de la liste de génomes.
     */
    private int currentChart;
    
    /**
     * {@code HashMap} associant à chaque indice dans la liste de génomes
     * le graphique donnant la courbe de couverture qui lui est associée.
     */
    private Map<Integer, ChartPanel> charts;
    
    /**
     * Le {@code ChartPanel} contenant le graphique représentant la courbe
     * de couverture d'un seul génome.
     */
    private ChartPanel chartPanel = null;

    /**
     * Le {@code ChartPanel} contenant le graphique représentant les courbes
     * de couverture de plusieurs génomes.
     */
    private ChartPanel compositeChartPanel = null;
    
    /**
     * Instance de la classe implémentant l'interface {@code MouseListener},
     * servant à associer des comportements aux événements liés au déplacement
     * de la souris et au clic gauche dans la région de l'interface occupée par
     * la liste de génomes.
     */
    private GenomeListMouseListener genomeListMouseListener = null;
    
    
    /**
     * Constructeur de la classe.
     * <p>
     * Crée les boutons de navigation, d'enregistrement et de basculement en
     * mode plein écran. Initialise la variable d'instance {@code charts} 
     * à un {@code HashMap} vide.
     *  
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     */
    public GUIModel(GUI gui) {
        this.gui = gui;
        listModel = new DefaultListModel<String>();
        labelGenomeList = new JLabel("Liste de génomes:");

        charts = new HashMap<Integer, ChartPanel>();

        bFull  = new JButton("Plein écran");
        bFirst = new JButton("<<");
        bPrev  = new JButton("<");
        bNext  = new JButton(">");
        bLast  = new JButton(">>");
        bSave  = new JButton("Enregistrer...");

        Container pane = this.gui.getContainerPane();            

        pane.add(bFull);
        pane.add(bFirst);
        pane.add(bPrev);
        pane.add(bNext);
        pane.add(bLast);
        pane.add(bSave);
        
        bFull .setBounds( 500, 510, 135, 20);
        bFirst.setBounds( 745, 510,  60, 20); 
        bPrev .setBounds( 815, 510,  60, 20);
        bNext .setBounds( 885, 510,  60, 20);
        bLast .setBounds( 955, 510,  60, 20); 
        bSave .setBounds(1115, 510, 135, 20);
        
        bFull .setVisible(false);
        bFirst.setVisible(false);
        bPrev .setVisible(false);
        bNext .setVisible(false);
        bLast .setVisible(false);
        bSave .setVisible(false);
        
        bFull .addActionListener(new FullscreenListener(this.gui));
        bFirst.addActionListener(new FirstChartListener(this));
        bPrev .addActionListener(new  PrevChartListener(this));
        bNext .addActionListener(new  NextChartListener(this));
        bLast .addActionListener(new  LastChartListener(this));
        bSave .addActionListener(new  SaveChartListener(this.gui, CHART_WIDTH, CHART_HEIGHT));
    }
    
    
    // Méthodes 'getters'
    public GenomeCoverage[] getGenomeCoverage()          { return gc; }
    public DefaultListModel<String> getListModel()       { return listModel; } 
    public JList<String> getGenomeList()                 { return genomeList; } 
    public JButton getFirstChartButton()                 { return bFirst; }
    public JButton getPrevChartButton()                  { return bPrev; }
    public JButton getNextChartButton()                  { return bNext; }
    public JButton getLastChartButton()                  { return bLast; }
    public JButton getFullscreenButton()                 { return bFull; }
    public JButton getSaveButton()                       { return bSave; }
    public int getChartIndex()                           { return currentChart; }
    public Container getContainerPane()                  { return gui.getContainerPane(); }
    public Map<Integer, ChartPanel> getCharts()          { return charts; }
    public ChartPanel getMostRecentChartPanel()          { return chartPanel; }
    public ChartPanel getMostRecentCompositeChartPanel() { return compositeChartPanel; }
    
    // Méthodes 'setters'
    public void setSearchResults(ArrayList<SearchResults> results) { this.results = results; }   
    public void setGenomeCoverage(GenomeCoverage[] gc)             { this.gc = gc; }
    public void setChartIndex(int i)                               { currentChart = i; }
    public void setCharts(Map<Integer, ChartPanel> charts)         { this.charts = charts; }
    
    
    /**
     * Si l'objet instance de la classe {@code GenomeListMouseListener} est 
     * initialisé, le listener est désactivé.
     */
    public void stopMouseListener() {
        if (genomeListMouseListener != null)
            genomeListMouseListener.disable();
    }
    
    
    /**
     * Initialise la liste de génomes.
     * <p>
     * Elle contiendra les identifiants de tous les génomes dans le fichier
     * d'entrée multi-fasta classés dans l'ordre alphabétique.
     */
    public void initializeGenomeList() {
        listModel = new DefaultListModel<String>();
        for (GenomeCoverage coverage : gc)
            listModel.addElement(coverage.getGenomeId());

        genomeList = new JList<String>(listModel);        
        genomeList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        genomeList.setSelectedIndex(0);
        
        Container pane = gui.getContainerPane();
        
        if (genomeListMouseListener == null) {
            pane.add(labelGenomeList);
            labelGenomeList.setBounds(20, 300, 380, 20);
        }
        
        genomeListMouseListener = new GenomeListMouseListener(gui);
        genomeList.addMouseListener(genomeListMouseListener);
        
        if (genomePane != null) genomePane.setVisible(false);
        genomePane = new JScrollPane(genomeList); 
        pane.add(genomePane);
        genomePane.setBounds(20, 340, 455, 160);        
    }
    
    
    /**
     * Calcule la couverture pour chaque génome.
     */
    public void computeCoverage() {
        for (SearchResults sr : results) {
            String genomeId = sr.getGenomeId();
            int n = sr.getReadLength();
            ArrayList<Integer> occ = sr.getOccurrences();
            int i;
            for (i = 0;
                 i < gc.length && !gc[i].getGenomeId().equals(genomeId);
                 i++);
            int[] coverage = gc[i].getCoverage();
            for (Integer o : occ) {
                for (int j = o; j < o + n; j++)
                    coverage[j]++;
            }
            gc[i].setCoverage(coverage);
        }
    }    
    
    
    /**
     * Affiche la courbe de couverture correspondant au premier génome dans la
     * liste de génomes.
     */
    public void initializeCoverageChart() {
        if (charts.size() != 0) {
            charts.get(currentChart).setVisible(false);
            charts = new HashMap<Integer, ChartPanel>();
        }
        
        if (compositeChartPanel != null) compositeChartPanel.setVisible(false);

        if (gc.length > 0) {
            CoverageChart chart = new CoverageChart(gc[0]);
            currentChart = 0;
            chartPanel = chart.getChartPanel();
            
            Container pane = gui.getContainerPane();            
            pane.add(chartPanel);
            chartPanel.setBounds(CHART_X, CHART_Y, CHART_WIDTH, CHART_HEIGHT);
            
            charts.put(0, chartPanel);
            
            gui.getSaveAllMenuItem().setEnabled(true);
            
            bFull .setVisible(true);
            bFirst.setVisible(true);
            bPrev .setVisible(true);
            bNext .setVisible(true);
            bLast .setVisible(true);
            bSave .setVisible(true);            
            
            bFull .setEnabled(true);
            bSave .setEnabled(true);
            ButtonManager.disable(bFirst, null);
            ButtonManager.disable(bPrev, null);
            ButtonManager.toggle(this, bNext, bLast);
        }
    }
    
    
    /**
     * Affiche la courbe de couverture correspondant au génome d'indice 
     * {@code index} dans la liste de génomes.
     * 
     * @param index indice du génome dont on souhaite afficher la courbe de
     * couverture
     */
    public void displayChart(int index) {
        if (compositeChartPanel != null) compositeChartPanel.setVisible(false);

        if (!charts.containsKey(index)) {  
            CoverageChart chart = new CoverageChart(gc[index]);
            chartPanel = chart.getChartPanel();

            charts.put(index, chartPanel);
        } else {
            chartPanel = charts.get(index);
            chartPanel.setVisible(true);
        }
        
        Container pane = getContainerPane();
        pane.add(chartPanel);
        chartPanel.setBounds(CHART_X, CHART_Y, CHART_WIDTH, CHART_HEIGHT);
    }
    
    
    /**
     * Affiche les courbes de couverture superposées correspondant aux génomes
     * dont les indices dans la liste de génomes sont donnés par le tableau
     * {@code index}.
     * 
     * @param index tableau d'indices donnant les génomes dont on souhaite
     * afficher les courbes de couverture superposées
     */    
    public void displayChart(int[] index) {
        if (chartPanel != null) chartPanel.setVisible(false);
        if (compositeChartPanel != null) compositeChartPanel.setVisible(false);

        GenomeCoverage[] overlap = new GenomeCoverage[index.length];
        for (int i = 0; i < index.length; i++) overlap[i] = gc[index[i]];
        CoverageChart chart = new CoverageChart(overlap);
        compositeChartPanel = chart.getChartPanel();
 
        Container pane = getContainerPane();
        pane.add(compositeChartPanel);
        compositeChartPanel.setBounds(
                CHART_X, CHART_Y,
                CHART_WIDTH, CHART_HEIGHT
        );
    }
    
    
    /**
     * Renvoie la date et l'heure courante sous la forme d'une chaîne de
     * caractères.
     * 
     * @return la date et l'heure courante sous la forme "yyyy/MM/dd HH:mm:ss"
     */
    public static String getTimeStamp() {
        return new 
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                    Calendar.getInstance().getTime());
    }
}
