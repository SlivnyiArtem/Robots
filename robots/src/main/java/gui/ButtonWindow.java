package gui;

import localization.Localization;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;

    public ButtonWindow() {
        super(Localization.getCommandsLabel(), true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton exitButton = new JButton(Localization.getQuit());
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /**
                 * здесь мы вызываем сообщение о закрытии
                 **/
                var exitDialogResult = Exiter.onExit();
                if (exitDialogResult == 0) System.exit(0);

            }
        });
        buttonPanel.add(exitButton);
        getContentPane().add(buttonPanel);
        pack();
    }

    @Override public void doDefaultCloseAction() {
        var confirmResult = Exiter.onExit();
        if (confirmResult == 0)
            super.doDefaultCloseAction();
    }

}
