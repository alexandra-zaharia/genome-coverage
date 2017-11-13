package io.github.alexandra.zaharia.gui;

import io.github.alexandra.zaharia.listeners.HelpHyperlinkListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * La classe <code>Help</code> permet d'afficher le panneau "À propos"
 * et la fenêtre graphique "Utilisation" du menu "Aide" de l'interface.
 */
public class Help {
    /**
     * Affiche la fenêtre graphique "Utilisation" du menu "Aide".
     *  @param width entier donnant la largeur utilisable de l'écran (en pixels)
     *
     */
    public static void showUsage(int width) {
        JFrame usage = new JFrame("Utilisation");
        usage.setContentPane(getUsagePanel());
        usage.setSize(Math.max(600, width/3), 600);
        usage.setLocation(width-usage.getWidth(), 0);
        usage.setVisible(true);
    }

    
    /**
     * Crée et renvoie le panneau de type {@code JPanel} qui sera affiché
     * dans la fenêtre correspondant à l'item "Utilisation" du menu "Aide".
     * 
     * @return référence vers l'objet de type {@code JPanel} contenant le
     * guide d'utilisation du logiciel
     */
    private static JScrollPane getUsagePanel() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        
        String html = getHtmlUsagePath();
        try {
            editorPane.setPage(new URL(html));
        } catch (Exception exception) {
            ExceptionHandlingGUI.showExceptionPanel(exception);
        }
        
        editorPane.addHyperlinkListener(new HelpHyperlinkListener());
        
        return new JScrollPane(editorPane);
    }
    
    
    /**
     * Crée et renvoie le panneau de type {@code JPanel} qui sera affiché dans
     * la boîte de dialogue correspondant à l'item "À propos" du menu "Aide".
     * 
     * @return référence vers l'objet de type {@code JPanel} contenant
     * l'<i>à propos</i> du logiciel
     */
    public static JPanel getAboutPanel() {        
        JPanel panel = new JPanel();
        BorderLayout layout = new BorderLayout();
        layout.setVgap(20);
        panel.setLayout(layout);
        panel.setOpaque(true);
        panel.setBackground(GUIModel.ORANGE);
        panel.setPreferredSize(new Dimension(350, 300));
        
        String projectTitle = "Recherche de motifs nucléotidiques";
        JLabel title = new JLabel(projectTitle, JLabel.CENTER);
        title.setOpaque(true);
        title.setBackground(Color.BLACK);
        title.setForeground(GUIModel.ORANGE);
        title.setPreferredSize(new Dimension(300, 50));
        
        JEditorPane about = new JEditorPane();
        about.setEditable(false);
        about.setContentType("text/html");
        about.setOpaque(true);
        about.setBackground(GUIModel.ORANGE);
        about.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        
        String html = getHtmlAboutPath();
        try {
            about.setPage(new URL(html));
        } catch (Exception exception) {
            ExceptionHandlingGUI.showExceptionPanel(exception);
        }

        about.addHyperlinkListener(new HelpHyperlinkListener());
      
        panel.add(title, BorderLayout.NORTH);
        panel.add(about, BorderLayout.CENTER);
        
        return panel;
    }


    /**
     * Renvoie le chemin vers la ressource HTML spécifiée.
     *
     * @param html fichier HTML demandé
     * @return chemin vers le fichier HTML demandé
     */
    private static String getHtmlPath(String html) {
        String thisClassPath = Help.class.getResource("Help.class").toString();
        String htmlPath = "";

        if (!thisClassPath.startsWith("jar:")) {
            String sep = Character.toString(File.separatorChar);
            htmlPath = System.getProperty("user.dir");
            File htmlFile = new File(htmlPath + sep + html);
            htmlPath = "file:" + htmlPath + sep;
            if (!htmlFile.exists()) // probablement en ligne de commande
                htmlPath += "res" + sep;
        } else if (thisClassPath.contains("!")) { // à partir d'un JAR
            htmlPath = thisClassPath.split("!")[0] + "!/";
        }
        htmlPath += html;
        return htmlPath;
    }


    /**
     * Renvoie le chemin vers la ressource HTML donnant le contenu de la
     * fenêtre "Utilisation" sous la forme d'une chaîne de caractères.
     * 
     * @return chemin vers le fichier {@code utilisation.html} du répertoire
     * {@code res} du projet
     */
    private static String getHtmlUsagePath() {
        return getHtmlPath("utilisation.html");
    }

    
    /**
     * Renvoie le chemin vers la ressource HTML donnant le contenu de la
     * boîte de dialogue "À propos" sous la forme d'une chaîne de caractères.
     * 
     * @return chemin vers le fichier {@code apropos.html} du répertoire
     * {@code res} du projet
     */
    
    private static String getHtmlAboutPath() {
        return getHtmlPath("apropos.html");
    } 
}    
  
