package io.github.alexandra.zaharia.gui;

import javax.swing.*;
import java.io.File;

/**
 * La classe {@code OpenFile} permet de simplifier la logique derrière le choix
 * de fichier d'entrée.
 */
public class OpenFile {
    /**
     * Si un fichier a été sélectionné, son chemin est affiché dans le champ
     * approprié et le bouton permettant de lancer la recherche d'occurrences
     * est activé si l'autre fichier d'entrée a lui aussi été spécifié.
     *
     * @param gui référence de l'objet de type {@code GUI} affichant l'interface
     *
     * @param thisField le champ concerné par le fichier sélectionné
     *
     * @param otherField l'autre champ
     */
    public static void choose(
            GUI gui, JTextField thisField, JTextField otherField) {
        JFileChooser fc = gui.getFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gui);
        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            thisField.setText(file.getPath());
            if (otherField != null && otherField.getText().length() != 0)
                ButtonManager.enable(gui.getSearchButton(), GUIModel.ORANGE);
        }
    }
}
