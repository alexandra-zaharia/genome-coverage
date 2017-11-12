package io.github.alexandra.zaharia.gui;

import io.github.alexandra.zaharia.search.NaivePatternSearch;
import io.github.alexandra.zaharia.search.PatternSearch;
import io.github.alexandra.zaharia.search.SearchResults;
import io.github.alexandra.zaharia.search.SuffixArrayPatternSearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingWorker;


/**
 * La classe {@code SearchWorker} permet de lancer la recherche d'occurrences
 * comme tâche d'arrière plan (<i>background</i>), pour que l'interface
 * graphique du logiciel reste disponible pendant ce temps-là.
 */
public class SearchWorker extends SwingWorker<ArrayList<SearchResults>, Void> {
    /**
     * Référence de l'objet de type {@link GUI} affichant l'interface.
     */
    private GUI gui;
    
    /**
     * Entier donnant le code numérique associé à la méthode de recherche
     * sélectionnée par l'utilisateur.
     */
	private int methodId;
	
	/**
	 * Chaîne de caractères donnant le chemin vers le fichier d'entrée
	 * multi-fasta.
	 */
	private String fastaFile;
	
	/**
	 * Chaîne de caractères donnant le chemin vers le fichier d'entrée FastQ.
	 */
	private String fastqFile;
	
	/**
	 * Chaîne de caractères donnant le chemin vers le fichier de sortie (pour 
	 * l'enregistrement des résultats de la recherche).
	 */
	private String outputFile;
	
	/**
	 * Composant de type {@code JLabel} de l'interface, représentant la barre
     * d'état.
	 */
	private JLabel statusBar;
	
	/**
     * Le bouton dans l'interface permettant de lancer la recherche
     * d'occurrences.
	 */
	private JButton bSearch;
	
	/**
	 * Booléen valant true uniquement si le thread SwingWorker représenté par
	 * l'objet courant a levé une exception.
	 */
	private boolean exceptionEncountered = false;
	
	/**
	 * {@code ArrayList} d'objets de type {@link SearchResults}, retenant les 
	 * résultats de la recherche d'occurrences.
	 */
	private ArrayList<SearchResults> results;
	
	
	/**
	 * Constructeur de la classe.
	 *
	 * @param gui référence de l'objet de type {@code GUI} affichant l'interface
	 */
	public SearchWorker(GUI gui) {
	    this.gui   = gui;
		methodId   = gui.getSearchMethod();
		fastaFile  = gui.getGenomesTextField().getText().trim();
		fastqFile  = gui.getReadsTextField()  .getText().trim();
		outputFile = gui.getOutputTextField() .getText().trim();
		statusBar  = gui.getStatusBar();
		bSearch    = gui.getSearchButton();
	}
	

	/**
	 * Permet de dire si l'objet courant a levé une exception.
	 * 
	 * @return la valeur du booléen {@code exceptionEncountered}
	 */
	public boolean threwException() {
	    return exceptionEncountered;
	}

	
	/**
	 * Implémentation de la méthode abstraite {@code doInBackground} de la 
	 * classe {@code SwingWorker}, permettant de lancer la recherche
	 * d'occurrences.
	 * <p>
	 * Renvoie un {@code ArrayList} d'objets de type {@link SearchResults}. 
	 */
	public ArrayList<SearchResults> doInBackground() {
	    disableGuiComponents();
        statusBar.setVisible(true);
        statusBar.setText("Recherche en cours...");
		String timeStamp = GUIModel.getTimeStamp();
		String method = methodId == 0 ? "(naïve)" : "(tableau de suffixes)";
		System.out.println(timeStamp + " - recherche commencée " + method);
		return searchOccurrences(fastaFile, fastqFile, methodId);
	}
	
	
	/**
	 * Méthode à exécuter en fin d'exécution de la tâche de recherche
     * d'occurrences.
	 * <p>
	 * La variable d'instance {@code results} est initialisée à la valeur
     * renvoyée par la méthode {@code get} de la classe {@code SwingWorker}. Si
     * un fichier de sortie a été spécifié, les résultats de la recherche y
     * seront enregistrés.
	 */
	public void done() {
		bSearch.setEnabled(true);
        bSearch.setBackground(GUIModel.ORANGE);
		try {
		    results = get();
		    gui.getGuiModel().setSearchResults(results);
		    String timeStamp = GUIModel.getTimeStamp();
		    String method = methodId == 0 ? "(naïve)" : "(tableau de suffixes)";
		    System.out.println(timeStamp + " - recherche terminée " + method);

			// On essaie d'écrire dans le fichier de sortie (si spécifié).
			if (outputFile.length() > 0) saveSearchResultsToFile();
		} catch (Exception e) {
		    exceptionEncountered = true;
		    statusBar.setText("Erreur");
		}
	}
	
	
	/**
	 * Désactive les boutons de navigation, d'enregistrement, de basculement en 
	 * mode plein écran, ainsi que l'item "Enregistrer tous les graphiques..."
	 * du menu "Fichier".
	 */
	private void disableGuiComponents() {
	    gui.getSaveAllMenuItem().setEnabled(false);
        JButton bFull  = gui.getGuiModel().getFullscreenButton();
        JButton bSave  = gui.getGuiModel().getSaveButton();
        JButton bFirst = gui.getGuiModel().getFirstChartButton();
        JButton bPrev  = gui.getGuiModel().getPrevChartButton();
        JButton bNext  = gui.getGuiModel().getNextChartButton();
        JButton bLast  = gui.getGuiModel().getLastChartButton();
        ButtonManager.disable(bFull, null);
        ButtonManager.disable(bSave, null);
        ButtonManager.disable(bFirst, null);
        ButtonManager.disable(bPrev, null);
        ButtonManager.disable(bNext, null);
        ButtonManager.disable(bLast, null);
	}
	
	
	/**
	 * Effectue la recherche d'occurrences de toutes les séquences
     * nucléotidiques contenues dans le fichier d'entrée {@code fastq} dans
     * l'ensemble de génomes contenus dans le fichier d'entrée {@code fasta},
     * par la méthode de recherche désignée par le code numérique
     * {@code method}.
	 * 
	 * @param fasta chaîne de caractères spécifiant le chemin vers le fichier
	 * d'entrée multi-fasta
	 * 
	 * @param fastq chaîne de caractères spécifiant le chemin vers le fichier
	 * d'entrée FastQ
	 * 
	 * @param method code numérique associé à la méthode de recherche à utiliser
	 * 
	 * @return {@code ArrayList} d'objets de type {@code SearchResults},
     * contenant les résultats de la recherche d'occurrences
	 */
	private ArrayList<SearchResults> searchOccurrences(
	        String fasta, String fastq, int method) {
        ArrayList<SearchResults> results;
        PatternSearch ps = null;
        try {
            switch (method) {
                case 0: { // Recherche naïve
                    ps = new NaivePatternSearch(fasta, fastq);
                    break;
                }
                case 1: { // Recherche par tableau de suffixes
                    ps = new SuffixArrayPatternSearch(fasta, fastq);
                    break;
                }
            }
        } catch (Exception e) {
            exceptionEncountered = true;
            statusBar.setText("Erreur");
            ExceptionHandlingGUI.showExceptionPanel(e);
        }
        
        gui.getGuiModel().setGenomeCoverage(ps.doGenomeCoverage());
        results = ps.search();

        return results;
    }
	
	
	/**
	 * Si un fichier de sortie pour les résultats de la recherche a été
     * spécifié, cette méthode essaie de les enregistrer dans le fichier de
     * sortie spécifié par la chaîne de caractères {@code outputFile}.
	 */
	private void saveSearchResultsToFile() {
	    BufferedWriter bw;
	    try {
            bw = new BufferedWriter(new FileWriter(outputFile));
            String timeStamp = GUIModel.getTimeStamp();
            System.out.println(timeStamp + " - écriture dans fichier commencée");
            for (SearchResults sr : results) {                  
                StringBuilder match = new StringBuilder();
                match.append(sr.getReadId());
                match.append(" ");
                match.append(sr.getReadLength());
                match.append(" ");
                boolean forward = sr.isForward();
                if (forward) match.append("forward");
                else         match.append("reverse");   
                match.append(" "); 
                match.append(sr.getGenomeId());
                match.append(" ");
                ArrayList<Integer> occurrences = sr.getOccurrences();
                for (Integer o : occurrences) {
                    match.append(o);
                    match.append(" ");
                }
                bw.write(match.toString());
                bw.newLine();
            }
            bw.close();
            timeStamp = GUIModel.getTimeStamp();
            System.out.println(timeStamp + " - écriture dans fichier terminée");
	    } catch (Exception e) {
            statusBar.setText("Erreur");
            ExceptionHandlingGUI.showExceptionPanel(e);
	    }
	}	
}
