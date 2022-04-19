package gui;

import localization.Localization;

import javax.swing.JOptionPane;

public class Exiter {
    public static int onExit() {
        var answerCode =
                JOptionPane.showConfirmDialog(null, Localization.getExitConfirmationQuestion(),Localization.getExitConfirmation(), JOptionPane.YES_NO_OPTION);
        return answerCode;
    }
}
