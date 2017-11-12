package io.github.alexandra.zaharia.gui;

import javax.swing.*;
import java.awt.*;

/**
 * La classe {@code ButtonManager} propose des méthodes statiques permettant
 * d'activer ou de désactiver des boutons de l'interface.
 */
public class ButtonManager {
    /**
     * Active le bouton spécifié et lui attribue la couleur spécifiée.
     * @param button le bouton à activer
     * @param color la couleur (peut être null)
     */
    public static void enable(JButton button, Color color) {
        if (color == null) color = (Color) UIManager.get("Button.background");
        button.setEnabled(true);
        button.setBackground(color);
    }

    /**
     * Désactive le bouton spécifié et lui attribue la couleur spécifiée.     *
     * @param button le bouton à désactiver
     * @param color la couleur (peut être null)
     */
    public static void disable(JButton button, Color color) {
        if (color == null) color = (Color) UIManager.get("Button.background");
        button.setEnabled(false);
        button.setBackground(color);
    }

    /**
     * Active ou désactive les boutons permettant d'aller au premier et au
     * dernier graphique dans l'interface, selon la nécessité.
     *
     * @param guiModel le {@code GUIModel} à utiliser
     * @param bNext bouton permettant d'aller au premier graphique
     * @param bLast bouton permettant d'aller au dernier graphique
     */
    public static void toggle(GUIModel guiModel, JButton bNext, JButton bLast) {
        if (guiModel.getGenomeCoverage().length == 1) {
            ButtonManager.disable(bNext, null);
            ButtonManager.disable(bLast, null);
        } else {
            ButtonManager.enable(bNext, GUIModel.ORANGE);
            ButtonManager.enable(bLast, GUIModel.ORANGE);
        }
    }
}
