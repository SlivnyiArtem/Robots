package gui;

import gui.windows.GetLocalizeLabel;

import javax.swing.JOptionPane;

public class Dialoger implements GetLocalizeLabel {
    public static int onExit() {
        Object[] options = {
                GetLocalizeLabel
                        .getLocalization("confirmYesOption"),
                GetLocalizeLabel.getLocalization("confirmNoOption")
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
    public static int confirmRecovery(){
        Object[] options = {
                GetLocalizeLabel
                        .getLocalization("confirmYesOption"),
                GetLocalizeLabel.getLocalization("confirmNoOption")
        };
        return JOptionPane.showOptionDialog(null,
                GetLocalizeLabel.getLocalization("recoverConfirmationQuestion"),
                GetLocalizeLabel.getLocalization("recoverConfirmation"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
    }
}