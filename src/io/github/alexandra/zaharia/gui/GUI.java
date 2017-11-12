package io.github.alexandra.zaharia.gui;

import io.github.alexandra.zaharia.listeners.SearchListener;
import io.github.alexandra.zaharia.listeners.browse.GenomesBrowseListener;
import io.github.alexandra.zaharia.listeners.browse.OutputBrowseListener;
import io.github.alexandra.zaharia.listeners.browse.ReadsBrowseListener;
import io.github.alexandra.zaharia.listeners.chart.SaveAllListener;
import io.github.alexandra.zaharia.listeners.path.GenomesPathListener;
import io.github.alexandra.zaharia.listeners.path.ReadsPathListener;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;


/**
 * La classe {@code GUI} est responsable de la création et de l'affichage de 
 * l'interface graphique, proposant comme principales fonctionnalités le
 * démarrage de la recherche et l'affichage et l'enregistrement des courbes
 * de couverture (pour un ou plusieurs génomes).
 */
public class GUI extends JPanel {
    /**
     * Serial version ID généré.
     */
    private static final long serialVersionUID = 6881040302799457389L;

    /**
     * Largeur utilisable de l'écran (en pixels).
     */
    private int width;

    /**
     * Hauteur utilisable de l'écran (en pixels).
     */
    private int height;

    /**
     * Le <i>content pane</i> sur lequel on affiche tous les composants de
     * l'interface.
     */
    private Container pane;

    /**
     * Méthodes de recherche disponibles.
     */
    private String[] searchMethods = { "Naïve", "Tableau de suffixes" };

    /**
     * Bouton permettant de parcourir le contenu du disque pour choisir le
     * fichier multi-fasta contenant les génomes.
     */
    private JButton bGenomes;

    /**
     * Bouton permettant de parcourir le contenu du disque pour choisir le
     * fichier FastQ contenant les <i>reads</i>.
     */
    private JButton bReads;

    /**
     * Bouton permettant de parcourir le contenu du disque pour choisir le
     * fichier de sortie (en vue de l'enregistremnet des résultats de la
     * recherche).
     */
    private JButton bOutput;

    /**
     * Bouton permettant de lancer la recherche des occurrences de tous les
     * <i>reads</i> contenus dans le fichier FastQ parmi l'ensemble de génomes
     * contenus dans le fichier multi-fasta.
     */
    private JButton bSearch;

    /**
     * Champ pour retenir le chemin vers le fichier multi-fasta contenant les
     * génomes.
     */
    private JTextField pathGenomes;

    /**
     * Champ pour retenir le chemin vers le fichier FastQ contenant les
     * <i>reads</i>.
     */
    private JTextField pathReads;

    /**
     * Champ pour spécifier le fichier de sortie (avec les résultats de la
     * recherche) - optionnel.
     */
    private JTextField pathOutput;

    /**
     * <i>ComboBox</i> permettant de choisir la méthode de recherche à employer.
     */
    private JComboBox<String> comboMethod;

    /**
     * Dialogue <code>JFileChooser</code> permettant de sélectionnner un fichier
     * sur le disque pour l'ouverture ou l'enregistrement.
     */
    private JFileChooser fc;

    /**
     * Item du menu "Fichier" correspondant au choix "Enregistrer touts les
     * graphiques...".
     */
    private JMenuItem saveAll;

    /**
     * Barre d'état servant à indiquer si la recherche est en cours ou terminée,
     * ainsi qu'à donner des informations sur l'utilisation de la liste de
     * génomes.
     */
    private JLabel statusBar;

    /**
     * Instance de la classe {@code GUIModel} comprenant le modèle des données
     * utilisées, ainsi que toute la logique interne de l'interface.
     */
    private GUIModel guiModel;


    /**
     * Constructeur de la classe.
     * <p>
     * Crée le menu de l'application et tous les composants graphiques à
     * afficher, les positionne dans le panneau et associe des
     * <code>ActionListener</code>s aux items du menu.
     */
    public GUI() {
        JFrame frame = new JFrame("Recherche de motifs nucléotidiques");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initializeComponents();
        setLookAndFeel();

        // Création du ContentPane pour le JFrame 'frame'
        pane = new JPanel();
        pane.setLayout(null);

        // Création d'un nouveau modèle pour ce GUI
        guiModel = new GUIModel(this);

        // Création et attribution de la barre de menus au frame
        frame.setJMenuBar(createMenuBar());

        // Création de composants nécessaires pour définir la recherche
        JLabel labelGenomes = new JLabel("Fichier multi-fasta contenant les génomes:");
        JLabel labelReads   = new JLabel("Fichier FastQ contenant les reads:");
        JLabel labelMethod  = new JLabel("Méthode de recherche:");
        JLabel labelOutput  = new JLabel("Fichier de sortie (optionnel):");
        
        // Ajout de composants au ContentPane
        pane.add(labelGenomes);
        pane.add(bGenomes);
        pane.add(pathGenomes);
        pane.add(bReads);
        pane.add(pathReads);
        pane.add(labelReads);
        pane.add(labelMethod);
        pane.add(comboMethod);
        pane.add(labelOutput);
        pane.add(pathOutput);
        pane.add(bOutput);
        pane.add(bSearch);
        pane.add(statusBar);
        
        /* Le bouton pour lancer la recherche est le bouton par défaut (il peut
         * être activé par la touche ENTER. 
         */
        frame.getRootPane().setDefaultButton(bSearch);
         
        // Positionnement des composants
        labelGenomes.setBounds( 20,  20, 380, 20);
        bGenomes    .setBounds(340,  20, 135, 20);
        pathGenomes .setBounds( 20,  50, 455, 20);
        labelReads  .setBounds( 20, 100, 380, 20);
        bReads      .setBounds(340, 100, 135, 20);
        pathReads   .setBounds( 20, 130, 455, 20);
        labelOutput .setBounds( 20, 180, 380, 20);
        bOutput     .setBounds(340, 180, 135, 20);
        pathOutput  .setBounds( 20, 210, 455, 20);
        labelMethod .setBounds( 20, 260, 380, 20);
        comboMethod .setBounds(210, 260, 265, 20);
        bSearch     .setBounds(340, 300, 135, 20);
        statusBar   .setBounds( 20, 510, 455, 20);
      
        pane.setPreferredSize(new Dimension(1270, 530));
        JScrollPane scrollPane = new JScrollPane(pane);
        frame.setContentPane(scrollPane);
        frame.setSize(Math.min(width, 1280), Math.min(height, 600));
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    
    // Méthodes 'getters'
    public int getWidth()                   { return width; }
    public int getHeight()                  { return height; }
    public Container getContainerPane()     { return pane; }
    public JLabel getStatusBar()            { return statusBar; }  
    public JTextField getGenomesTextField() { return pathGenomes; } 
    public JTextField getReadsTextField()   { return pathReads; }
    public JTextField getOutputTextField()  { return pathOutput; }   
    public int getSearchMethod()            { return comboMethod.getSelectedIndex(); }    
    public JButton getSearchButton()        { return bSearch; }    
    public JFileChooser getFileChooser()    { return fc; }
    public JMenuItem getSaveAllMenuItem()   { return saveAll; }
    public GUIModel getGuiModel()           { return guiModel; }
    
    public void setGuiModel(GUIModel model) { guiModel = model; }
    

    /**
     * Initialise certains composants de l'interface à leurs valeurs par défaut.
     */
    private void initializeComponents() {
        fc = new JFileChooser();        

        // Dimensions de l'écran
        Rectangle bounds = getLocalGraphicsEnvironment().getMaximumWindowBounds();
        width  = bounds.width;
        height = bounds.height;

        // Méthode de recherche par défaut: recherche par tableau de suffixes
        comboMethod = new JComboBox<String>(searchMethods);    
        comboMethod.setSelectedIndex(1);

        // Création de composants nécessaires pour définir la recherche
        bGenomes    = new JButton("Parcourir...");
        pathGenomes = new JTextField();
        bReads      = new JButton("Parcourir...");
        pathReads   = new JTextField();
        pathOutput  = new JTextField();
        bOutput     = new JButton("Parcourir...");
        bSearch     = new JButton("Rechercher");
        
        // Création de la barre d'état
        statusBar = new JLabel();
        Border paddingBorder = BorderFactory.createEmptyBorder(0,10,0,0);
        Border insideBorder  = BorderFactory.createLineBorder(Color.GRAY, 1);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                insideBorder, paddingBorder));
        statusBar.setFont(
                new Font(statusBar.getFont().getName(), Font.PLAIN, 11));
        statusBar.setVisible(false);

        // Le bouton qui lance la recherche est initialement inactif.
        bSearch.setEnabled(false);
        
        // Ajout de io.github.alexandra.zaharia.listeners aux composants concernés
        bGenomes.addActionListener(new GenomesBrowseListener(this));
        bReads  .addActionListener(new ReadsBrowseListener(this));
        bOutput .addActionListener(new OutputBrowseListener(this));
        bSearch .addActionListener(new SearchListener(this));
        pathGenomes.getDocument().addDocumentListener(new GenomesPathListener(this));
        pathReads  .getDocument().addDocumentListener(new ReadsPathListener(this));
    }

    
    /**
     * Configure quelques paramètres liés à l'aspect esthétique de l'interface.
     */
    private void setLookAndFeel() {
        UIManager.put("MenuBar.background",           GUIModel.ORANGE);
        UIManager.put("MenuItem.background",          GUIModel.ORANGE);
        UIManager.put("Menu.selectionBackground",     Color.BLACK);
        UIManager.put("Menu.selectionForeground",     GUIModel.ORANGE);
        UIManager.put("MenuItem.selectionBackground", Color.BLACK);
        UIManager.put("MenuItem.selectionForeground", GUIModel.ORANGE);
    }
       
    
    /**
     * Crée et renvoie la barre de menus de l'interface, en ajoutant des
     * <code>ActionListener</code>s aux items du menu.
     * 
     * @return la barre de menus
     */
    private JMenuBar createMenuBar() {
        // Barre de menus  
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(width, 20));
        
        // Les menus
        JMenu file = new JMenu("Fichier");
        JMenu help = new JMenu("Aide");
        
        menuBar.add(file);
        menuBar.add(help);
        
        // Items des menus
                  saveAll = new JMenuItem("Enregistrer tous les graphiques...");
        JMenuItem quit    = new JMenuItem("Quitter");
        JMenuItem usage   = new JMenuItem("Utilisation");
        JMenuItem about   = new JMenuItem("À propos");
        
        file.add(saveAll);
        file.add(quit);
        help.add(usage);
        help.add(about);
        
        saveAll.setEnabled(false);
        
        // Ajout de io.github.alexandra.zaharia.listeners aux items des menus
        saveAll.addActionListener(new SaveAllListener(this));
        
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        usage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Help.showUsage(width);
            }
        });
        
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    null,
                    Help.getAboutPanel(),
                    "À propos",
                    JOptionPane.PLAIN_MESSAGE
                );
            }
        });
        
        return menuBar;        
    }
    

    /**
     * Crée une nouvelle instance de la classe sur l'EDT (<i>Event
     * Dispatch Thread</i>).
     * 
     * @param args paramètres en ligne de commande (non pris en compte)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }
}
