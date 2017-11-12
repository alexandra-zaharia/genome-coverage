package io.github.alexandra.zaharia.listeners;

import io.github.alexandra.zaharia.gui.DesktopAPI;
import io.github.alexandra.zaharia.gui.ExceptionHandlingGUI;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 * La classe {@code HelpHyperlinkListener} implémente l'interface
 * {@code HyperlinkListener}, en associant l'action appropriée 
 * à l'événement de clic sur un lien HTTP dans l'interface.
 */
public class HelpHyperlinkListener implements HyperlinkListener {
    /**
     * Implémente le comportement souhaité quand on clique sur un lien HTTP dans
     * l'interface.
     * <p>
     * Une méthode privée permettant d'ouvrir le lien dans un navigateur web est
     * appelée.
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        try {
            if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                openWebPage(e.getURL());
        } catch(Exception exception) {
            ExceptionHandlingGUI.showExceptionPanel(exception);
        }
    }
    
    
    /**
     * Ouvre le lien {@code url} dans un navigateur web, en faisant appel à
     * la méthode privée surchargée {@code openWebPage(URI)}.
     * 
     * @param url l'URL à ouvrir dans le navigateur web
     */
    private static void openWebPage(URL url) {
        try {
            openWebPage(url.toURI());
        } catch (URISyntaxException e) {
            ExceptionHandlingGUI.showExceptionPanel(e);
        }
    }
    
    
    /**
     * Ouvre l'URI reçu en argument dans le navigateur web.
     * 
     * @param uri l'URI à ouvrir
     */
    private static void openWebPage(URI uri) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop == null)
                throw new Exception("Desktop is null.");
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                System.out.println("Opening " + uri.toString());
                desktop.browse(uri);
            } else {
                if (!DesktopAPI.browse(uri))
                    throw new Exception("Cannot open link in web browser.");
            }
        } catch (Exception e) {
            ExceptionHandlingGUI.showExceptionPanel(e);
        }
    }
}
