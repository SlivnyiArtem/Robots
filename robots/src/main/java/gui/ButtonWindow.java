package gui;

import localization.Localization;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Supplier;

public class ButtonWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;
    public ArrayList<ButtonItem> buttons;

    public ButtonWindow() {
        super(Localization.getCommandsLabel(), true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel buttonPanel = new JPanel(new BorderLayout());
        var exitButton = new Button(Localization.getQuit());
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /**
                 * здесь мы вызываем сообщение о закрытии
                 **/
                var exitDialogResult = Exiter.onExit();
                if (exitDialogResult == 0) System.exit(0);

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

    @Override public void doDefaultCloseAction() {
        var confirmResult = Exiter.onExit();
        if (confirmResult == 0)
            super.doDefaultCloseAction();
    }

}
