package gui.windows;
import gui.ButtonItem;
import gui.Dialoger;
import localization.Localization;
import lombok.SneakyThrows;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Button;
import java.util.ArrayList;

public class ButtonWindow extends JInternalFrame implements GetLocalizeLabel {
    public ArrayList<ButtonItem> buttons;
    public ButtonWindow() {
        super(GetLocalizeLabel.getLocalization("commandsLabel"),
                true, true, true, true);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        var exitButton = new Button(GetLocalizeLabel.getLocalization("quit"));
        exitButton.addActionListener(e -> {
            /**
             * здесь мы вызываем сообщение о закрытии
             **/
            var exitDialogResult = Dialoger.onExit();
            if (exitDialogResult == 0) System.exit(0);

        });

        var loadPreviousSessionButton = new Button("Session");
        loadPreviousSessionButton.addActionListener(e -> {
            var exitDialogResult = Dialoger.onExit();
            if (exitDialogResult == 0){
            }
        });

        buttons = new ArrayList<>();
        buttons.add(new ButtonItem(Localization::getQuit, exitButton));
        for (var button: buttons) {
            buttonPanel.add(button.ItemButton);
        }
        getContentPane().add(buttonPanel);
        pack();
    }

    public void updateButtonLabels(){
        for (var button: buttons) {
            button.updateItem();
        }
    }

    @SneakyThrows
    @Override public void doDefaultCloseAction() {
        var confirmResult = Dialoger.onExit();
        if (confirmResult == 0){
            super.doDefaultCloseAction();
        }
    }

}
