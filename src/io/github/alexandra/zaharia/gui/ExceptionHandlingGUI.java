package io.github.alexandra.zaharia.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * La classe <code>ExceptionHandlingGUI</code> permet d'afficher une boîte de
 * dialogue contenant les message et les stack traces des exceptions rencontrées 
 * au cours de l'exécution du logiciel.
 */
public class ExceptionHandlingGUI {
    /**
     * Affiche la boîte de dialogue qui décrit l'exception.
     * 
     * @param e l'exception
     */
    public static void showExceptionPanel(Throwable e) {
        JOptionPane.showMessageDialog(
            null,
            getErrorPanel(e),
            "Une erreur a eu lieu",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    
    /**
     * Crée le {@code JPanel} utilisé par la méthode {@code showExceptionPanel}
     * pour afficher le message et le stack trace de l'exception rencontrée.
     * 
     * @param e l'exception
     * 
     * @return le {@code JPanel} contenant deux {@code JScrollPane} (l'un pour
     * le message, l'autre pour le stack trace de l'exception)
     */
	private static JPanel getErrorPanel(Throwable e) {
	    // On crée un paneau (JPanel).
		JPanel panel = new JPanel();
		BorderLayout layout = new BorderLayout();
		layout.setVgap(20);
		panel.setLayout(layout);
		
		/* On crée un JLabel contenant le message de l'exception et on lui 
		 * ajoute un JScrollPane.
		 */
		JLabel label = new JLabel(e.getMessage());
		JScrollPane scrollMessage = new JScrollPane(label);
		scrollMessage.setPreferredSize(new Dimension(500, 50));
		
		/* On crée un JTextArea contenant le stack trace et on lui ajoute un
		 * JScrollPane.
		 */
		JTextArea stackTrace = new JTextArea();
		stackTrace.setEditable(false);
		stackTrace.setText(stackTraceToString(e));		
		JScrollPane scrollTrace = new JScrollPane(stackTrace);
		scrollTrace.setPreferredSize(new Dimension(600, 200));
		
		// On ajoute les deux JScrollPanes au JPanel.
		panel.add(scrollMessage, BorderLayout.NORTH);
		panel.add(scrollTrace,  BorderLayout.CENTER);
		
		// On renvoie le JPanel.
		return panel;
	}

	
	/**
	 * Renvoie le stack trace d'une exception sous la forme d'une chaîne de
	 * caractères.
	 * 
	 * @param e l'exception
	 * 
	 * @return chaîne de caractères représentant le stack trace de l'exception
	 */
	private static String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}    
}
