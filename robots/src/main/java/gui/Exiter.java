package gui;

import gui.windows.GetLocalizeLabel;

import javax.swing.JOptionPane;

public class Exiter implements GetLocalizeLabel {
    public static int onExit() {
        Object[] options = {
                GetLocalizeLabel
                        .getLocalization("exitConfirmYesOption"),
                GetLocalizeLabel.getLocalization("exitConfirmNoOption")
        };
        return JOptionPane.showOptionDialog(null,
                GetLocalizeLabel.getLocalization("exiterConfirmationQuestion"),
                GetLocalizeLabel.getLocalization("exiterConfirmation"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
    }
}