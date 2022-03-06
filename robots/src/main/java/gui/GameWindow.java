package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());

        /*
        var textField = new JTextField();
        textField.setColumns(23);
        panel.add(textField);
         */

        JButton exitButton = new JButton("Ex");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //textField.setText("!");



                //panel.setVisible(false);
                /**
                 * здесь мы вызываем сообщение о закрытии
                 **/


                var exitDialogResult = Exiter.onExit();
                if (exitDialogResult == 0) System.exit(0);

            }
        });
        panel.add(m_visualizer, BorderLayout.CENTER);

        buttonPanel.add(exitButton, BorderLayout.CENTER);

        getContentPane().add(panel);
        getContentPane().add(buttonPanel);
        pack();
    }
}
