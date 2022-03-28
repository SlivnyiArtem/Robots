package gui;

import javax.swing.JOptionPane;

public class Exiter {
    public static int onExit() {
        var answerCode =
                JOptionPane.showConfirmDialog(null,"Please, confirm exit",
                        "Are you really want to exit", JOptionPane.YES_NO_OPTION);
        return answerCode;
    }
}
